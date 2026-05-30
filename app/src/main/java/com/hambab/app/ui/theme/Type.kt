package com.hambab.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Pretendard 폰트 추가 전엔 system sans-serif 로 — Android 기본 본문 폰트와 비슷.
// v2 에 res/font/pretendard.xml 추가하면 FontFamily.Default → Pretendard 로 교체.
private val HbFontFamily = FontFamily.SansSerif

// 함밥 typography (hambab-design 스킬 위계 기준)
val HambabTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = HbFontFamily, fontWeight = FontWeight.ExtraBold,
        fontSize = 32.sp, lineHeight = 38.sp, letterSpacing = (-0.5).sp,
    ),
    displayMedium = TextStyle(
        fontFamily = HbFontFamily, fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp, lineHeight = 34.sp, letterSpacing = (-0.4).sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = HbFontFamily, fontWeight = FontWeight.Bold,
        fontSize = 24.sp, lineHeight = 30.sp, letterSpacing = (-0.4).sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = HbFontFamily, fontWeight = FontWeight.Bold,
        fontSize = 20.sp, lineHeight = 26.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = HbFontFamily, fontWeight = FontWeight.Bold,
        fontSize = 18.sp, lineHeight = 24.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = HbFontFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp, lineHeight = 22.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = HbFontFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp, lineHeight = 18.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = HbFontFamily, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = HbFontFamily, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 21.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = HbFontFamily, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 16.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = HbFontFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp, lineHeight = 18.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = HbFontFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = HbFontFamily, fontWeight = FontWeight.Medium,
        fontSize = 11.sp, lineHeight = 14.sp, letterSpacing = 0.5.sp,
    ),
)
