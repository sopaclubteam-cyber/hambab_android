package com.hambab.app.util

import com.hambab.app.data.model.District
import com.hambab.app.data.model.MenuCategory
import com.hambab.app.data.model.TrustGrade
import com.hambab.app.data.model.VibeTag

// Web lib/constants.ts 와 동기화된 카탈로그.

data class DistrictItem(val key: District, val label: String, val sub: String)
val DISTRICTS = listOf(
    DistrictItem(District.gangnam, "강남", "강남구 일대"),
    DistrictItem(District.seongsu, "성수", "성수동/뚝섬"),
    DistrictItem(District.hongdae, "홍대", "홍대/합정/연남"),
)

data class MenuItem(val key: MenuCategory, val label: String, val emoji: String)
val MENUS = listOf(
    MenuItem(MenuCategory.gogi, "고기", "🥩"),
    MenuItem(MenuCategory.hwe, "회", "🐟"),
    MenuItem(MenuCategory.mala, "마라/훠궈", "🍲"),
    MenuItem(MenuCategory.gopchang, "곱창", "🍢"),
    MenuItem(MenuCategory.yang, "양갈비", "🍖"),
    MenuItem(MenuCategory.jjimdak, "찜닭", "🍗"),
    MenuItem(MenuCategory.omakase, "오마카세", "🍣"),
)

data class VibeItem(val key: VibeTag, val label: String)
val VIBE_TAGS = listOf(
    VibeItem(VibeTag.spicy_ok, "매운 거 환영"),
    VibeItem(VibeTag.no_drink, "노술"),
    VibeItem(VibeTag.quiet, "조용한 식사"),
    VibeItem(VibeTag.quick, "빠른 식사"),
    VibeItem(VibeTag.talk_moderate, "대화 적당히"),
    VibeItem(VibeTag.honbab_ok, "혼밥러 환영"),
    VibeItem(VibeTag.late_night, "야식 가능"),
)

data class TrustItem(val label: String)
val TRUST_GRADES: Map<TrustGrade, TrustItem> = mapOf(
    TrustGrade.newbie to TrustItem("신규 함밥러"),
    TrustGrade.verified to TrustItem("검증된 함밥러"),
    TrustGrade.regular to TrustItem("단골 함밥러"),
)

fun MenuCategory.label() = MENUS.firstOrNull { it.key == this }?.label ?: name
fun MenuCategory.emoji() = MENUS.firstOrNull { it.key == this }?.emoji ?: "🍽️"
fun District.label() = DISTRICTS.firstOrNull { it.key == this }?.label ?: name
fun VibeTag.label() = VIBE_TAGS.firstOrNull { it.key == this }?.label ?: name
fun TrustGrade.label() = TRUST_GRADES[this]?.label ?: name

// District 레이블 (PostCard 에서 import 가능하도록 top-level alias 추가)
fun District.districtLabel() = label()
