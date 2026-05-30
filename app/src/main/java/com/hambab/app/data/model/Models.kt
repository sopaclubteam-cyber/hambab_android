package com.hambab.app.data.model

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
