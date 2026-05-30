package com.hambab.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hambab.app.ui.theme.HbBrown
import com.hambab.app.ui.theme.HbChipShape
import com.hambab.app.ui.theme.HbCreamSoft

@Composable
fun TagChip(
    label: String,
    modifier: Modifier = Modifier,
    background: Color = HbCreamSoft,
    foreground: Color = HbBrown,
) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(color = foreground),
        modifier = modifier
            .clip(HbChipShape)
            .background(background)
            .padding(PaddingValues(horizontal = 10.dp, vertical = 4.dp)),
    )
}
