package com.hambab.app.ui.theme

import androidx.compose.ui.graphics.Color

// 함밥 톤 (hambab-design 스킬 정본).
// Web tailwind.config.ts 의 hb.* 와 1:1.

val HbCream       = Color(0xFFFEF7E5)
val HbCreamSoft   = Color(0xFFFBF1D8)
val HbCreamCard   = Color(0xFFFFFFFF)

val HbAmber       = Color(0xFFF59E0B)
val HbAmberSoft   = Color(0xFFFBBF24)
val HbAmberDeep   = Color(0xFFD97706)

val HbBrown       = Color(0xFF7C2D12)
val HbBrownSoft   = Color(0xFF9A3412)

val HbFg          = Color(0xFF1F2937)
val HbFgSoft      = Color(0xFF4B5563)
val HbFgMuted     = Color(0xFF9CA3AF)

val HbBorder        = Color(0xFFF3E8D2)
val HbBorderStrong  = Color(0xFFE5D5B0)

val HbOk      = Color(0xFF16A34A)
val HbWarn    = Color(0xFFF97316)
val HbDanger  = Color(0xFFDC2626)

// 그림자 (brown 알파 8%) — Compose Modifier.shadow 의 ambientColor/spotColor 로 사용
val HbShadow = Color(0x147C2D12)        // ~8% alpha
val HbShadowStrong = Color(0x297C2D12)  // ~16% alpha
