package com.hambab.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.hambab.app.ui.theme.HbBorder
import com.hambab.app.ui.theme.HbCreamCard
import com.hambab.app.ui.theme.HbShadow
import com.hambab.app.ui.theme.HbShadowStrong

// 함밥 cream-card 카드 — brown 그림자, 16dp 라운드.
@Composable
fun HbCard(
    modifier: Modifier = Modifier,
    padding: androidx.compose.ui.unit.Dp = 16.dp,
    elevated: Boolean = false,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .shadow(
                elevation = if (elevated) 8.dp else 3.dp,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                ambientColor = if (elevated) HbShadowStrong else HbShadow,
                spotColor = if (elevated) HbShadowStrong else HbShadow,
            )
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
            .background(HbCreamCard)
            .padding(padding),
        content = content,
    )
}

@Suppress("unused")
val DividerHairline = HbBorder
