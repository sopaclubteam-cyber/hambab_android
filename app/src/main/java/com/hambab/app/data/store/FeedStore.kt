package com.hambab.app.data.store

import com.hambab.app.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// 콘텐츠 피드 mock 스토어 — Web lib/feed-store.ts 와 동일 시그니처.
// Supabase 실 연동 시 이 object 만 교체 (시그니처 유지).
//
// 본질: 끝점이 "같이 먹기 1탭 CTA". 체류/팔로워/좋아요 KPI 없음.
// 정렬: boost 활성 → live → appetite_score desc

object FeedStore {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    private val _appetiteLogs = MutableStateFlow<List<PostAppetiteLog>>(emptyList())
    private val _postToMealLinks = MutableStateFlow<List<PostToMeal>>(emptyList())
    private var seeded = false

    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    @Synchronized
    fun ensureSeeded() {
        if (seeded) return
        _posts.value = FeedSeed.posts
        seeded = true
    }

    // ─── 식욕 점수 계산 ─────────────────────────────────────────────────────
    private fun calcAppetite(view: Int, tap: Int, cta: Int): Int =
        Math.round(cta * 5f + tap * 2f + view * 0.1f)

    // ─── 조회 ───────────────────────────────────────────────────────────────

    /**
     * 피드 정렬: boost 활성 → live → appetite_score desc.
     * approved 만 노출 (pending/rejected/hidden 은 어드민 전용).
     */
    fun listFeed(
        kind: PostKind? = null,
        district: District? = null,
        menu: MenuCategory? = null,
    ): List<PostWithMeta> {
        val nowMs = System.currentTimeMillis()
        val filtered = _posts.value.filter { p ->
            if (p.moderation != PostModeration.approved) return@filter false
            if (kind != null && p.kind != kind) return@filter false
            if (district != null && p.district != district) return@filter false
            if (menu != null && p.menu != menu) return@filter false
            true
        }
        val sorted = filtered.sortedWith(
            compareByDescending<Post> {
                if (it.boostUntil != null && it.boostUntil > nowMs) 1 else 0
            }.thenByDescending {
                if (it.kind == PostKind.live) 1 else 0
            }.thenByDescending { it.appetiteScore }
        )
        return sorted.map { hydrate(it) }
    }

    /** LiveStrip 용 — approved live 최대 3개 */
    fun listLiveStrip(): List<PostWithMeta> =
        listFeed(kind = PostKind.live).take(3)

    fun getPostById(id: String): PostWithMeta? =
        _posts.value.firstOrNull { it.id == id }?.let { hydrate(it) }

    /** 어드민 — 모든 상태 (pending 우선) */
    fun listFeedAdmin(moderation: PostModeration? = null): List<PostWithMeta> {
        val list = if (moderation != null)
            _posts.value.filter { it.moderation == moderation }
        else _posts.value
        return list.sortedWith(
            compareByDescending<Post> { if (it.moderation == PostModeration.pending) 1 else 0 }
                .thenByDescending { it.createdAt }
        ).map { hydrate(it) }
    }

    // ─── 쓰기 ───────────────────────────────────────────────────────────────

    /**
     * 콘텐츠 발행. 메뉴/지역/시간/사진 강제 검증.
     * 신규는 moderation=pending — 어드민 승인 후 노출.
     */
    fun createPost(
        kind: PostKind,
        authorId: String,
        photoUrls: List<String>,
        caption: String? = null,
        menu: MenuCategory,
        district: District,
        restaurantId: String? = null,
        spotLabel: String? = null,
        timeContext: Long,
        tags: List<VibeTag> = emptyList(),
    ): Post {
        require(photoUrls.isNotEmpty()) { "사진을 1장 이상 올려 주세요." }
        val post = Post(
            id = "po_${System.currentTimeMillis()}_${(1000..9999).random()}",
            kind = kind,
            authorId = authorId,
            photoUrls = photoUrls,
            caption = caption,
            menu = menu,
            district = district,
            restaurantId = restaurantId,
            spotLabel = spotLabel,
            timeContext = timeContext,
            tags = tags,
            moderation = PostModeration.pending,
            createdAt = System.currentTimeMillis(),
        )
        _posts.value = _posts.value + post
        return post
    }

    /** 식욕 신호 누적. 1액션 1로그. */
    fun recordAppetite(postId: String, action: PostAppetiteAction, userId: String? = null) {
        val log = PostAppetiteLog(
            id = "pa_${System.currentTimeMillis()}",
            postId = postId,
            userId = userId,
            action = action,
        )
        _appetiteLogs.value = _appetiteLogs.value + log

        val posts = _posts.value.toMutableList()
        val idx = posts.indexOfFirst { it.id == postId }
        if (idx < 0) return
        val p = posts[idx]
        val newView = p.viewCount + if (action == PostAppetiteAction.view) 1 else 0
        val newTap  = p.tapCount  + if (action == PostAppetiteAction.tap) 1 else 0
        val newCta  = p.ctaClickCount + if (action == PostAppetiteAction.cta_click) 1 else 0
        posts[idx] = p.copy(
            viewCount     = newView,
            tapCount      = newTap,
            ctaClickCount = newCta,
            appetiteScore = calcAppetite(newView, newTap, newCta),
        )
        _posts.value = posts
    }

    /** 피드 → 함밥 전환 기록. KPI 단일 진실원. */
    fun recordPostToMeal(postId: String, mealId: String, userId: String) {
        val link = PostToMeal(
            id = "ptm_${System.currentTimeMillis()}",
            postId = postId,
            mealId = mealId,
            userId = userId,
        )
        _postToMealLinks.value = _postToMealLinks.value + link
        _posts.value = _posts.value.map {
            if (it.id == postId) it.copy(mealId = mealId) else it
        }
    }

    fun resetAll() {
        seeded = false
        _posts.value = emptyList()
        _appetiteLogs.value = emptyList()
        _postToMealLinks.value = emptyList()
        ensureSeeded()
    }

    // ─── UI 조립 ───────────────────────────────────────────────────────────
    private fun hydrate(p: Post): PostWithMeta {
        val author = MockStore.userById(p.authorId) ?: User(
            id = p.authorId, nickname = "익명 함밥러",
            mannerScore = 80, trustGrade = TrustGrade.newbie,
        )
        val restaurant = p.restaurantId?.let { rid ->
            MockStore.restaurants.value.firstOrNull { it.id == rid }
        }
        return PostWithMeta(post = p, author = author, restaurant = restaurant)
    }
}
