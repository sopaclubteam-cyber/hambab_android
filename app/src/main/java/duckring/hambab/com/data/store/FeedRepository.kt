package duckring.hambab.com.data.store

import android.util.Log
import duckring.hambab.com.data.SupabaseClient
import duckring.hambab.com.data.model.District
import duckring.hambab.com.data.model.MenuCategory
import duckring.hambab.com.data.model.Post
import duckring.hambab.com.data.model.PostAppetiteAction
import duckring.hambab.com.data.model.PostKind
import duckring.hambab.com.data.model.PostModeration
import duckring.hambab.com.data.model.PostWithMeta
import duckring.hambab.com.data.model.TrustGrade
import duckring.hambab.com.data.model.User
import duckring.hambab.com.data.model.VibeTag
import duckring.hambab.com.data.remote.AppetiteLogInsert
import duckring.hambab.com.data.remote.PostInsert
import duckring.hambab.com.data.remote.PostRow
import duckring.hambab.com.data.remote.PostToMealInsert
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

// FeedRepository — Supabase vs mock 분기 계층.
// isConfigured 가 true 이면 Supabase posts 테이블 사용.
// false (URL 미설정 / offline / CI) 이면 FeedStore mock fall-through.
//
// 화면 호출 패턴 = LaunchedEffect 안에서 suspend 함수 호출 (coroutine-safe).
// Real-time channel 없음 — pull-to-refresh + LaunchedEffect(key) 로 충분.

private const val TAG = "FeedRepository"

object FeedRepository {

    private val useSupabase: Boolean get() = SupabaseClient.isConfigured

    // ─── 조회 ─────────────────────────────────────────────────────────────────

    suspend fun listFeed(
        kind: PostKind? = null,
        district: District? = null,
        menu: MenuCategory? = null,
    ): List<PostWithMeta> = withContext(Dispatchers.IO) {
        if (!useSupabase) {
            FeedStore.ensureSeeded()
            return@withContext FeedStore.listFeed(kind, district, menu)
        }
        try {
            val sb = SupabaseClient.client
            val result = sb.postgrest["posts"].select {
                filter {
                    eq("moderation", "approved")
                    if (kind != null) eq("kind", kind.name)
                    if (district != null) eq("district", district.name)
                    if (menu != null) eq("menu", menu.name)
                }
                order("boost_until", Order.DESCENDING, nullsFirst = false)
                order("appetite_score", Order.DESCENDING)
                limit(60)
            }
            val rows = result.decodeList<PostRow>()
            rows.map { it.toDomain() }
        } catch (e: Exception) {
            Log.w(TAG, "listFeed Supabase failed, falling back to mock: ${e.message}")
            FeedStore.ensureSeeded()
            FeedStore.listFeed(kind, district, menu)
        }
    }

    suspend fun listLiveStrip(): List<PostWithMeta> = withContext(Dispatchers.IO) {
        if (!useSupabase) {
            FeedStore.ensureSeeded()
            return@withContext FeedStore.listLiveStrip()
        }
        try {
            val sb = SupabaseClient.client
            val oneHourAgoIso = Instant.now().minusSeconds(3600)
                .atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            val result = sb.postgrest["posts"].select {
                filter {
                    eq("moderation", "approved")
                    eq("kind", "live")
                    gte("time_context", oneHourAgoIso)
                }
                order("appetite_score", Order.DESCENDING)
                limit(3)
            }
            val rows = result.decodeList<PostRow>()
            rows.map { it.toDomain() }
        } catch (e: Exception) {
            Log.w(TAG, "listLiveStrip failed, falling back to mock: ${e.message}")
            FeedStore.ensureSeeded()
            FeedStore.listLiveStrip()
        }
    }

    suspend fun getPostById(id: String): PostWithMeta? = withContext(Dispatchers.IO) {
        if (!useSupabase) {
            FeedStore.ensureSeeded()
            return@withContext FeedStore.getPostById(id)
        }
        try {
            val sb = SupabaseClient.client
            val result = sb.postgrest["posts"].select {
                filter { eq("id", id) }
                limit(1)
            }
            val rows = result.decodeList<PostRow>()
            rows.firstOrNull()?.toDomain()
        } catch (e: Exception) {
            Log.w(TAG, "getPostById failed, falling back to mock: ${e.message}")
            FeedStore.ensureSeeded()
            FeedStore.getPostById(id)
        }
    }

    // ─── 식욕 기록 ────────────────────────────────────────────────────────────

    /**
     * view/tap/cta_click 1행 insert.
     * DB 트리거(recalc_appetite_on_log)가 posts 카운터 + appetite_score 자동 갱신.
     * best-effort: 실패 시 로그만, UX 미중단.
     */
    suspend fun recordAppetite(
        postId: String,
        action: PostAppetiteAction,
        userId: String? = null,
    ) = withContext(Dispatchers.IO) {
        // mock 도 항상 업데이트 (오프라인 UX 연속성)
        FeedStore.recordAppetite(postId, action, userId)

        if (!useSupabase) return@withContext
        try {
            val sb = SupabaseClient.client
            sb.postgrest["post_appetite_logs"].insert(
                AppetiteLogInsert(
                    postId = postId,
                    action = action.name,
                    userId = userId,
                )
            )
        } catch (e: Exception) {
            Log.w(TAG, "recordAppetite Supabase failed (best-effort): ${e.message}")
        }
    }

    // ─── 피드 → 함밥 전환 기록 ────────────────────────────────────────────────

    suspend fun recordPostToMeal(
        postId: String,
        mealId: String,
        userId: String,
    ) = withContext(Dispatchers.IO) {
        FeedStore.recordPostToMeal(postId, mealId, userId)

        if (!useSupabase) return@withContext
        try {
            val sb = SupabaseClient.client
            sb.postgrest["post_to_meal"].insert(
                PostToMealInsert(
                    postId = postId,
                    mealId = mealId,
                    userId = userId,
                )
            )
            // posts.meal_id 캐시 갱신 — 트리거가 처리하지만 best-effort 직접 업데이트
            sb.postgrest["posts"].update(
                { set("meal_id", mealId) }
            ) {
                filter { eq("id", postId) }
            }
        } catch (e: Exception) {
            Log.w(TAG, "recordPostToMeal failed (best-effort): ${e.message}")
        }
    }

    // ─── 발행 ─────────────────────────────────────────────────────────────────

    /**
     * 새 포스트 발행.
     * Supabase 모드: DB insert → 트리거(force_post_pending)가 moderation='pending' 강제.
     * Mock 모드: FeedStore.createPost() — 동일 pending 로직.
     */
    suspend fun createPost(
        kind: PostKind,
        authorId: String,
        photoUrls: List<String>,
        caption: String? = null,
        menu: MenuCategory,
        district: District,
        restaurantId: String? = null,
        spotLabel: String? = null,
        timeContextMs: Long,
        tags: List<VibeTag> = emptyList(),
    ): Post = withContext(Dispatchers.IO) {
        require(photoUrls.isNotEmpty()) { "사진을 1장 이상 올려 주세요." }

        // 항상 mock 에도 기록 (오프라인 연속성)
        val mockPost = FeedStore.createPost(
            kind = kind,
            authorId = authorId,
            photoUrls = photoUrls,
            caption = caption,
            menu = menu,
            district = district,
            restaurantId = restaurantId,
            spotLabel = spotLabel,
            timeContext = timeContextMs,
            tags = tags,
        )

        if (!useSupabase) return@withContext mockPost

        try {
            val sb = SupabaseClient.client
            val isoTime = Instant.ofEpochMilli(timeContextMs)
                .atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            sb.postgrest["posts"].insert(
                PostInsert(
                    kind = kind.name,
                    authorId = authorId,
                    photoUrls = photoUrls,
                    caption = caption,
                    menu = menu.name,
                    district = district.name,
                    restaurantId = restaurantId,
                    spotLabel = spotLabel,
                    timeContext = isoTime,
                    tags = tags.map { it.name },
                )
            )
        } catch (e: Exception) {
            Log.w(TAG, "createPost Supabase insert failed: ${e.message}")
        }
        mockPost   // UI 는 mock 포스트로 즉각 피드백 (낙관적 업데이트)
    }

    // ─── Row → Domain 변환 ────────────────────────────────────────────────────

    private fun PostRow.toDomain(): PostWithMeta {
        val post = Post(
            id = id,
            kind = PostKind.entries.firstOrNull { it.name == kind } ?: PostKind.review,
            authorId = authorId,
            photoUrls = photoUrls,
            caption = caption,
            menu = MenuCategory.entries.firstOrNull { it.name == menu } ?: MenuCategory.gogi,
            district = District.entries.firstOrNull { it.name == district } ?: District.gangnam,
            restaurantId = restaurantId,
            spotLabel = spotLabel,
            timeContext = runCatching {
                OffsetDateTime.parse(timeContext).toInstant().toEpochMilli()
            }.getOrElse { System.currentTimeMillis() },
            tags = tags.mapNotNull { t -> VibeTag.entries.firstOrNull { it.name == t } },
            mealId = mealId,
            moderation = PostModeration.entries.firstOrNull { it.name == moderation }
                ?: PostModeration.pending,
            reportCount = reportCount,
            appetiteScore = appetiteScore,
            viewCount = viewCount,
            tapCount = tapCount,
            ctaClickCount = ctaClickCount,
            boostUntil = boostUntil?.let {
                runCatching { OffsetDateTime.parse(it).toInstant().toEpochMilli() }.getOrNull()
            },
            createdAt = runCatching {
                OffsetDateTime.parse(createdAt).toInstant().toEpochMilli()
            }.getOrElse { System.currentTimeMillis() },
        )

        // author: MockStore 에서 먼저 찾고, 없으면 anonymous stub
        val author = MockStore.userById(authorId) ?: User(
            id = authorId,
            nickname = "함밥러",
            mannerScore = 80,
            trustGrade = TrustGrade.newbie,
        )

        val restaurant = restaurantId?.let { rid ->
            MockStore.restaurants.value.firstOrNull { it.id == rid }
        }

        return PostWithMeta(post = post, author = author, restaurant = restaurant)
    }
}
