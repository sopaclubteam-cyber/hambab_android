package duckring.hambab.com.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Supabase PostgREST JSON ↔ Kotlin 매핑 DTO.
// 필드명 = DB snake_case 정본 (0003_feed.sql).
// 앱 도메인 모델(data.model.Models.kt) 은 별도 유지 — UI 는 변환 후 사용.

@Serializable
data class PostRow(
    val id: String,
    val kind: String,
    @SerialName("author_id")        val authorId: String,
    @SerialName("photo_urls")       val photoUrls: List<String>,
    val caption: String? = null,
    val menu: String,
    val district: String,
    @SerialName("restaurant_id")    val restaurantId: String? = null,
    @SerialName("spot_label")       val spotLabel: String? = null,
    @SerialName("time_context")     val timeContext: String,   // ISO-8601 timestamptz
    val tags: List<String> = emptyList(),
    @SerialName("meal_id")          val mealId: String? = null,
    val moderation: String,
    @SerialName("report_count")     val reportCount: Int = 0,
    @SerialName("appetite_score")   val appetiteScore: Int = 0,
    @SerialName("view_count")       val viewCount: Int = 0,
    @SerialName("tap_count")        val tapCount: Int = 0,
    @SerialName("cta_click_count")  val ctaClickCount: Int = 0,
    @SerialName("boost_until")      val boostUntil: String? = null,
    @SerialName("created_at")       val createdAt: String,
)

@Serializable
data class AppetiteLogInsert(
    @SerialName("post_id")  val postId: String,
    val action: String,             // "view" | "tap" | "cta_click"
    @SerialName("user_id")  val userId: String? = null,
)

@Serializable
data class PostToMealInsert(
    @SerialName("post_id")  val postId: String,
    @SerialName("meal_id")  val mealId: String,
    @SerialName("user_id")  val userId: String,
)

@Serializable
data class PostInsert(
    val kind: String,
    @SerialName("author_id")        val authorId: String,
    @SerialName("photo_urls")       val photoUrls: List<String>,
    val caption: String? = null,
    val menu: String,
    val district: String,
    @SerialName("restaurant_id")    val restaurantId: String? = null,
    @SerialName("spot_label")       val spotLabel: String? = null,
    @SerialName("time_context")     val timeContext: String,   // ISO-8601
    val tags: List<String> = emptyList(),
)
