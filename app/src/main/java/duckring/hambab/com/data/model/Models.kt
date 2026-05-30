package duckring.hambab.com.data.model

// Web lib/types.ts 와 1:1. snake_case 그대로 유지 (Supabase 직렬화 호환).

enum class TrustGrade { newbie, verified, regular }
enum class MealMode   { instant, scheduled }
enum class MealStatus { open, full, confirmed, in_progress, completed, cancelled }
enum class ParticipantStatus { pending, joined, no_show, attended, cancelled }
enum class District { gangnam, seongsu, hongdae }
enum class MenuCategory { gogi, hwe, mala, gopchang, yang, jjimdak, omakase }
enum class VibeTag { spicy_ok, no_drink, quiet, quick, talk_moderate, honbab_ok, late_night }

data class User(
    val id: String,
    val nickname: String,
    val mannerScore: Int,
    val trustGrade: TrustGrade,
    val favTags: List<VibeTag> = emptyList(),
    val favMenus: List<MenuCategory> = emptyList(),
    val noShowCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
)

data class Meal(
    val id: String,
    val hostId: String,
    val mode: MealMode,
    val menu: MenuCategory,
    val title: String,
    val district: District,
    val spotLabel: String? = null,
    val meetAt: Long,          // epoch ms
    val maxPeople: Int,
    val tags: List<VibeTag> = emptyList(),
    val notes: String? = null,
    val status: MealStatus = MealStatus.open,
    val createdAt: Long = System.currentTimeMillis(),
)

data class Participant(
    val id: String,
    val mealId: String,
    val userId: String,
    val status: ParticipantStatus = ParticipantStatus.joined,
    val joinedAt: Long = System.currentTimeMillis(),
)

data class Restaurant(
    val id: String,
    val name: String,
    val district: District,
    val menuCategories: List<MenuCategory>,
    val partner: Boolean = false,
    val hambabSet: String? = null,
    val discountLabel: String? = null,
)

// 조립된 뷰 — UI 친화
data class MealWithMeta(
    val meal: Meal,
    val host: User,
    val participants: List<ParticipantWithUser>,
) {
    val joinedCount: Int = participants.count {
        it.participant.status == ParticipantStatus.joined ||
            it.participant.status == ParticipantStatus.attended
    }
    val isFull: Boolean = joinedCount >= meal.maxPeople
}

data class ParticipantWithUser(
    val participant: Participant,
    val user: User,
)

data class DensityStat(
    val district: District,
    val menu: MenuCategory?,
    val openMeals: Int,
    val pendingSeats: Int,
    val label: String,
)

// ─── 콘텐츠 피드 (Web lib/types.ts PostKind 1:1 미러) ─────────────────────────
// 끝점이 "같이 먹기 1탭 CTA" — 자랑/팔로워/체류 KPI 없음.

enum class PostKind { live, scheduled, restaurant, review }
enum class PostModeration { pending, approved, rejected, hidden }
enum class PostAppetiteAction { view, tap, cta_click }

data class Post(
    val id: String,
    val kind: PostKind,
    val authorId: String,
    // 콘텐츠 — emoji 로 대체 (mock)
    val photoUrls: List<String>,              // mock 에서 emoji 1개 문자열로 대체
    val caption: String? = null,
    // 필수 메타
    val menu: MenuCategory,
    val district: District,
    val restaurantId: String? = null,
    val spotLabel: String? = null,
    val timeContext: Long,                    // epoch ms
    val tags: List<VibeTag> = emptyList(),
    // 전환 연결
    val mealId: String? = null,
    // 모더레이션
    val moderation: PostModeration = PostModeration.pending,
    val reportCount: Int = 0,
    // 식욕 신호 — cta*5 + tap*2 + view*0.1
    val appetiteScore: Int = 0,
    val viewCount: Int = 0,
    val tapCount: Int = 0,
    val ctaClickCount: Int = 0,
    // 부스트 (식당만, epoch ms null = 미부스트)
    val boostUntil: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
)

data class PostAppetiteLog(
    val id: String,
    val postId: String,
    val userId: String? = null,
    val action: PostAppetiteAction,
    val at: Long = System.currentTimeMillis(),
)

data class PostToMeal(
    val id: String,
    val postId: String,
    val mealId: String,
    val userId: String,
    val convertedAt: Long = System.currentTimeMillis(),
)

// UI 조립 뷰
data class PostWithMeta(
    val post: Post,
    val author: User,
    val restaurant: Restaurant? = null,
    val meal: MealWithMeta? = null,
)
