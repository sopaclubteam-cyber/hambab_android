package com.hambab.app.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hambab.app.data.model.MealMode
import com.hambab.app.data.store.MockStore
import com.hambab.app.ui.component.DensityStrip
import com.hambab.app.ui.component.EmptyState
import com.hambab.app.ui.component.MealCard
import com.hambab.app.ui.theme.HbAmber
import com.hambab.app.ui.theme.HbAmberDeep
import com.hambab.app.ui.theme.HbBrown
import com.hambab.app.ui.theme.HbButtonShape
import com.hambab.app.ui.theme.HbCreamCard
import com.hambab.app.ui.theme.HbCreamSoft
import com.hambab.app.ui.theme.HbFg
import com.hambab.app.ui.theme.HbFgSoft

@Composable
fun HomeScreen(
    onNow: () -> Unit,
    onScheduled: () -> Unit,
    onMeal: (String) -> Unit,
    onCreate: () -> Unit,
) {
    // mock store 변경 시 자동 리프레시
    val mealsFlow by MockStore.meals.collectAsState()
    val partsFlow by MockStore.participants.collectAsState()
    val usersFlow by MockStore.users.collectAsState()
    @Suppress("UNUSED_VARIABLE")
    val recomposeTrigger = mealsFlow.size + partsFlow.size + usersFlow.size

    val instant = MockStore.listMeals(mode = MealMode.instant).take(3)
    val density = MockStore.densityStats()

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "함밥",
                    style = MaterialTheme.typography.displayMedium.copy(color = HbBrown),
                )
                Text(
                    text = "혼자라서 포기했던 메뉴를, 같이 먹어요.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = HbFgSoft),
                )
            }
        }

        if (density.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "오늘의 밀도",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = HbAmberDeep, fontWeight = FontWeight.SemiBold,
                        ),
                    )
                    DensityStrip(stats = density)
                }
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                EntryCard(
                    title = "지금 함밥",
                    subtitle = "3시간 안에 시작",
                    cta = "바로 보기",
                    background = HbAmber,
                    foreground = HbCreamCard,
                    modifier = Modifier.weight(1f),
                    onClick = onNow,
                )
                EntryCard(
                    title = "예약 함밥",
                    subtitle = "내일·주말 미리",
                    cta = "둘러보기",
                    background = HbCreamSoft,
                    foreground = HbBrown,
                    modifier = Modifier.weight(1f),
                    onClick = onScheduled,
                )
            }
        }

        item {
            Text(
                text = "지금 진행 중인 함밥",
                style = MaterialTheme.typography.titleMedium.copy(color = HbFg),
            )
        }

        if (instant.isEmpty()) {
            item {
                EmptyState(
                    title = "아직 지금 함밥이 없어요",
                    subtitle = "내가 먼저 만들면 다른 사람이 합류해요.",
                    ctaLabel = "지금 함밥 만들기",
                    onCta = onCreate,
                )
            }
        } else {
            items(instant) { meal ->
                MealCard(meal = meal, onClick = { onMeal(meal.meal.id) })
            }
        }
    }
}

@Composable
private fun EntryCard(
    title: String,
    subtitle: String,
    cta: String,
    background: Color,
    foreground: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(HbButtonShape)
            .background(background)
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = foreground, fontWeight = FontWeight.Bold,
                ),
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = foreground.copy(alpha = 0.85f),
                ),
            )
            Text(
                text = "$cta →",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = foreground, fontWeight = FontWeight.SemiBold,
                ),
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
