package com.hambab.app.ui.screen.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hambab.app.data.auth.AuthRepository
import com.hambab.app.data.model.MealStatus
import com.hambab.app.data.model.ParticipantStatus
import com.hambab.app.data.store.MockStore
import com.hambab.app.ui.component.HbCard
import com.hambab.app.ui.component.MannerBadge
import com.hambab.app.ui.component.TagChip
import com.hambab.app.ui.theme.HbAmber
import com.hambab.app.ui.theme.HbAmberDeep
import com.hambab.app.ui.theme.HbBrown
import com.hambab.app.ui.theme.HbButtonShape
import com.hambab.app.ui.theme.HbCreamCard
import com.hambab.app.ui.theme.HbCreamSoft
import com.hambab.app.ui.theme.HbDanger
import com.hambab.app.ui.theme.HbFg
import com.hambab.app.ui.theme.HbFgSoft
import com.hambab.app.ui.theme.HbOk
import com.hambab.app.ui.theme.HbWarn
import com.hambab.app.util.emoji
import com.hambab.app.util.formatCountdown
import com.hambab.app.util.formatMeetTime
import com.hambab.app.util.label

@Composable
fun MealDetailScreen(
    mealId: String,
    onBack: () -> Unit,
    onRequireLogin: () -> Unit,
) {
    val mealsFlow by MockStore.meals.collectAsState()
    val partsFlow by MockStore.participants.collectAsState()
    @Suppress("UNUSED_VARIABLE") val t = mealsFlow.size + partsFlow.size

    val meal = MockStore.mealWithMeta(mealId)
    if (meal == null) {
        Box(Modifier.fillMaxWidth().padding(24.dp)) {
            Text(
                "이 함밥은 더 이상 존재하지 않아요.",
                style = MaterialTheme.typography.bodyMedium.copy(color = HbFgSoft),
            )
        }
        return
    }

    val me = AuthRepository.currentUser()
    val isHost = me?.id == meal.meal.hostId
    val joined = me != null && meal.participants.any {
        it.user.id == me.id &&
            (it.participant.status == ParticipantStatus.joined ||
                it.participant.status == ParticipantStatus.attended)
    }
    var toast by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // 뒤로가기
        Text(
            "← 뒤로",
            style = MaterialTheme.typography.labelMedium.copy(color = HbFgSoft),
            modifier = Modifier
                .clickable(onClick = onBack)
                .padding(vertical = 4.dp),
        )

        // 메뉴 + 제목
        HbCard {
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = meal.meal.menu.emoji(), fontSize = 36.sp)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                    Text(
                        meal.meal.title,
                        style = MaterialTheme.typography.headlineMedium.copy(color = HbFg),
                    )
                    Text(
                        "${meal.meal.district.label()} · ${meal.meal.menu.label()}" +
                            (meal.meal.spotLabel?.let { " · $it" } ?: ""),
                        style = MaterialTheme.typography.bodyMedium.copy(color = HbFgSoft),
                    )
                    Text(
                        "${formatMeetTime(meal.meal.meetAt)} · ${formatCountdown(meal.meal.meetAt)}",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = HbAmberDeep, fontWeight = FontWeight.SemiBold,
                        ),
                    )
                    if (meal.meal.tags.isNotEmpty()) {
                        Row(
                            Modifier
                                .horizontalScroll(rememberScrollState())
                                .padding(top = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) { meal.meal.tags.forEach { TagChip(label = it.label()) } }
                    }
                    if (!meal.meal.notes.isNullOrBlank()) {
                        Text(
                            "“${meal.meal.notes}”",
                            style = MaterialTheme.typography.bodySmall.copy(color = HbFgSoft),
                            modifier = Modifier.padding(top = 6.dp),
                        )
                    }
                }
            }
        }

        // 인원
        HbCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "자리",
                        style = MaterialTheme.typography.labelMedium.copy(color = HbFgSoft),
                    )
                    Text(
                        "${meal.joinedCount} / ${meal.meal.maxPeople}명 모임",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = if (meal.isFull) HbWarn else HbAmberDeep,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                }
                StatusBadge(meal.meal.status)
            }
        }

        // 호스트
        HbCard {
            Text("호스트", style = MaterialTheme.typography.labelMedium.copy(color = HbFgSoft))
            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    meal.host.nickname,
                    style = MaterialTheme.typography.titleMedium.copy(color = HbFg),
                )
                MannerBadge(meal.host.trustGrade)
                Text(
                    "매너 ${meal.host.mannerScore}",
                    style = MaterialTheme.typography.labelMedium.copy(color = HbFgSoft),
                )
            }
        }

        // 참여자 리스트
        HbCard {
            Text(
                "함께하는 함밥러 (${meal.joinedCount})",
                style = MaterialTheme.typography.labelMedium.copy(color = HbFgSoft),
            )
            Column(
                modifier = Modifier.padding(top = 6.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                meal.participants.filter {
                    it.participant.status == ParticipantStatus.joined ||
                        it.participant.status == ParticipantStatus.attended
                }.forEach { p ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            p.user.nickname + if (p.user.id == meal.meal.hostId) " · 호스트" else "",
                            style = MaterialTheme.typography.bodyMedium.copy(color = HbFg),
                            modifier = Modifier.weight(1f),
                        )
                        MannerBadge(p.user.trustGrade)
                        Text(
                            "매너 ${p.user.mannerScore}",
                            style = MaterialTheme.typography.labelSmall.copy(color = HbFgSoft),
                        )
                    }
                }
                if (meal.participants.none {
                        it.participant.status == ParticipantStatus.joined ||
                            it.participant.status == ParticipantStatus.attended
                    }) {
                    Text(
                        "아직 참여자가 없어요.",
                        style = MaterialTheme.typography.bodySmall.copy(color = HbFgSoft),
                    )
                }
            }
        }

        // 액션
        when {
            meal.meal.status == MealStatus.cancelled || meal.meal.status == MealStatus.completed -> {
                Text(
                    if (meal.meal.status == MealStatus.completed) "이 함밥은 완료됐어요."
                    else "이 함밥은 취소됐어요.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = HbFgSoft),
                )
            }
            me == null -> {
                Button(
                    onClick = onRequireLogin,
                    shape = HbButtonShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HbAmber, contentColor = HbCreamCard,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("로그인하고 참여하기") }
            }
            isHost -> {
                OutlinedButton(
                    onClick = {
                        val r = MockStore.cancelMealAsHost(meal.meal.id, me.id)
                        toast = if (r.ok) "함밥을 취소했어요." else r.reason
                    },
                    shape = HbButtonShape,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("함밥 취소 (호스트)", color = HbDanger)
                }
            }
            joined -> {
                OutlinedButton(
                    onClick = {
                        MockStore.leaveMeal(meal.meal.id, me.id)
                        toast = "참여를 취소했어요."
                    },
                    shape = HbButtonShape,
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("참여 취소", color = HbFgSoft) }
            }
            else -> {
                Button(
                    onClick = {
                        val r = MockStore.joinMeal(meal.meal.id, me.id)
                        toast = if (r.ok) "참여 완료! 모임에서 만나요." else r.reason
                    },
                    enabled = !meal.isFull && meal.meal.status != MealStatus.full,
                    shape = HbButtonShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HbAmber,
                        contentColor = HbCreamCard,
                        disabledContainerColor = HbCreamSoft,
                        disabledContentColor = HbFgSoft,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(if (meal.isFull) "자리가 모두 찼어요" else "함밥 참여하기")
                }
            }
        }

        if (toast != null) {
            Text(
                toast.orEmpty(),
                style = MaterialTheme.typography.labelMedium.copy(color = HbBrown),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(HbCreamSoft)
                    .padding(12.dp),
            )
        }
    }
}

@Composable
private fun StatusBadge(status: MealStatus) {
    val (label, fg) = when (status) {
        MealStatus.open -> "모집 중" to HbAmberDeep
        MealStatus.full -> "만석" to HbWarn
        MealStatus.confirmed -> "확정" to HbAmberDeep
        MealStatus.in_progress -> "진행 중" to HbAmberDeep
        MealStatus.completed -> "완료" to HbOk
        MealStatus.cancelled -> "취소됨" to HbDanger
    }
    Text(
        label,
        style = MaterialTheme.typography.labelMedium.copy(color = fg, fontWeight = FontWeight.SemiBold),
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(HbCreamSoft)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
}

@Suppress("unused") private val unusedColor: Color = Color.Unspecified
@Suppress("unused") private val unusedPad = PaddingValues(0.dp)
