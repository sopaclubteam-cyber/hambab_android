package com.hambab.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val HambabShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small      = RoundedCornerShape(10.dp),
    medium     = RoundedCornerShape(12.dp),
    large      = RoundedCornerShape(16.dp),   // 카드 기본
    extraLarge = RoundedCornerShape(24.dp),
)

// chip / pill — Material3 Shapes 에 없어 별도 노출
val HbChipShape = RoundedCornerShape(percent = 50)
val HbCardShape = RoundedCornerShape(16.dp)
val HbButtonShape = RoundedCornerShape(12.dp)
