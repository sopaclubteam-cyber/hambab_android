package duckring.hambab.com.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import duckring.hambab.com.data.model.DensityStat
import duckring.hambab.com.ui.theme.HbAmberDeep
import duckring.hambab.com.ui.theme.HbChipShape
import duckring.hambab.com.ui.theme.HbCreamSoft

@Composable
fun DensityStrip(stats: List<DensityStat>, modifier: Modifier = Modifier) {
    if (stats.isEmpty()) return
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        stats.forEach { s ->
            Text(
                text = s.label,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = HbAmberDeep,
                    fontWeight = FontWeight.SemiBold,
                ),
                modifier = Modifier
                    .clip(HbChipShape)
                    .background(HbCreamSoft)
                    .padding(PaddingValues(horizontal = 14.dp, vertical = 8.dp)),
            )
        }
    }
}
