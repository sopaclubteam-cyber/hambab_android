package com.hambab.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.hambab.app.data.model.TrustGrade
import com.hambab.app.ui.theme.HbAmberDeep
import com.hambab.app.ui.theme.HbBrown
import com.hambab.app.ui.theme.HbChipShape
import com.hambab.app.ui.theme.HbCreamSoft
import com.hambab.app.ui.theme.HbFgSoft
import com.hambab.app.util.label

@Composable
fun MannerBadge(grade: TrustGrade, modifier: Modifier = Modifier) {
    val (bg, fg) = when (grade) {
        TrustGrade.newbie   -> HbCreamSoft to HbFgSoft
        TrustGrade.verified -> HbCreamSoft to HbAmberDeep
        TrustGrade.regular  -> HbCreamSoft to HbBrown
    }
    Text(
        text = grade.label(),
        style = MaterialTheme.typography.labelSmall.copy(color = fg),
        modifier = modifier
            .clip(HbChipShape)
            .background(bg)
            .padding(PaddingValues(horizontal = 10.dp, vertical = 4.dp)),
    )
}
