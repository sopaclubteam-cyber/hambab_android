package duckring.hambab.com.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import duckring.hambab.com.data.model.MealStatus
import duckring.hambab.com.data.model.MealWithMeta
import duckring.hambab.com.ui.theme.HbAmberDeep
import duckring.hambab.com.ui.theme.HbFg
import duckring.hambab.com.ui.theme.HbFgMuted
import duckring.hambab.com.ui.theme.HbFgSoft
import duckring.hambab.com.ui.theme.HbWarn
import duckring.hambab.com.util.emoji
import duckring.hambab.com.util.formatCountdown
import duckring.hambab.com.util.formatMeetTime
import duckring.hambab.com.util.label

@Composable
fun MealCard(
    meal: MealWithMeta,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val m = meal.meal
    val cancelled = m.status == MealStatus.cancelled

    HbCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = !cancelled, onClick = onClick),
        padding = 16.dp,
    ) {
        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = m.menu.emoji(),
                fontSize = 30.sp,
                modifier = Modifier.size(36.dp),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = m.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = if (cancelled) HbFgMuted else HbFg,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    maxLines = 2,
                )
                Text(
                    text = buildString {
                        append(m.district.label())
                        append(" · ")
                        append(m.menu.label())
                        if (!m.spotLabel.isNullOrBlank()) {
                            append(" · ")
                            append(m.spotLabel)
                        }
                    },
                    style = MaterialTheme.typography.bodySmall.copy(color = HbFgSoft),
                    maxLines = 1,
                )
                Text(
                    text = "${formatMeetTime(m.meetAt)}  ·  ${formatCountdown(m.meetAt)}",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = if (cancelled) HbFgMuted else HbAmberDeep,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            }
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "${meal.joinedCount}/${m.maxPeople}명",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = when {
                            cancelled -> HbFgMuted
                            meal.isFull -> HbWarn
                            else -> HbAmberDeep
                        },
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }
        }

        if (m.tags.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                m.tags.forEach { tag -> TagChip(label = tag.label()) }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            MannerBadge(meal.host.trustGrade)
            Text(
                text = "호스트 · ${meal.host.nickname}  매너 ${meal.host.mannerScore}",
                style = MaterialTheme.typography.labelSmall.copy(color = HbFgSoft),
            )
            if (cancelled) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                    Text(
                        "취소됨",
                        style = MaterialTheme.typography.labelSmall.copy(color = HbWarn),
                    )
                }
            }
        }
    }
}
