package com.hambab.app.ui.screen.newmeal

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hambab.app.data.auth.AuthRepository
import com.hambab.app.data.model.District
import com.hambab.app.data.model.Meal
import com.hambab.app.data.model.MealMode
import com.hambab.app.data.model.MenuCategory
import com.hambab.app.data.model.VibeTag
import com.hambab.app.data.store.MockStore
import com.hambab.app.ui.component.HbCard
import com.hambab.app.ui.theme.HbAmber
import com.hambab.app.ui.theme.HbAmberDeep
import com.hambab.app.ui.theme.HbBorderStrong
import com.hambab.app.ui.theme.HbBrown
import com.hambab.app.ui.theme.HbButtonShape
import com.hambab.app.ui.theme.HbChipShape
import com.hambab.app.ui.theme.HbCreamCard
import com.hambab.app.ui.theme.HbCreamSoft
import com.hambab.app.ui.theme.HbFg
import com.hambab.app.ui.theme.HbFgSoft
import com.hambab.app.util.DISTRICTS
import com.hambab.app.util.MENUS
import com.hambab.app.util.VIBE_TAGS

@Composable
fun NewMealScreen(onCreated: (String) -> Unit, onCancel: () -> Unit) {
    val me = AuthRepository.currentUser()
    if (me == null) {
        // 안전장치 — 호출 측에서 LOGIN 라우팅 보장하지만 fallback
        Box(Modifier.fillMaxWidth().padding(24.dp)) {
            Text(
                "로그인 후 함밥을 만들 수 있어요.",
                style = MaterialTheme.typography.bodyMedium.copy(color = HbFgSoft),
            )
        }
        return
    }

    var mode by remember { mutableStateOf(MealMode.instant) }
    var menu by remember { mutableStateOf(MenuCategory.gogi) }
    var district by remember { mutableStateOf(District.gangnam) }
    var title by remember { mutableStateOf("") }
    var spot by remember { mutableStateOf("") }
    var maxPeople by remember { mutableStateOf(3) }
    var hoursLater by remember { mutableStateOf(2) }   // instant: 시간 / scheduled: 일
    var tags by remember { mutableStateOf(setOf<VibeTag>()) }
    var notes by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("함밥 만들기", style = MaterialTheme.typography.headlineLarge.copy(color = HbFg))
            Text(
                "메뉴 · 지역 · 시간만 정하면 끝.",
                style = MaterialTheme.typography.bodyMedium.copy(color = HbFgSoft),
            )
        }

        HbCard {
            FieldLabel("모드")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ModeChip("지금 함밥", mode == MealMode.instant) { mode = MealMode.instant; hoursLater = 2 }
                ModeChip("예약 함밥", mode == MealMode.scheduled) { mode = MealMode.scheduled; hoursLater = 24 }
            }
        }

        HbCard {
            FieldLabel("메뉴")
            Row(
                Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                MENUS.forEach { m ->
                    Pill(
                        label = "${m.emoji} ${m.label}",
                        selected = menu == m.key,
                        onClick = { menu = m.key },
                    )
                }
            }
        }

        HbCard {
            FieldLabel("지역")
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                DISTRICTS.forEach { d ->
                    Pill(label = d.label, selected = district == d.key) { district = d.key }
                }
            }
        }

        HbCard {
            FieldLabel("제목")
            OutlinedInput(
                value = title,
                onValueChange = { title = it.take(40) },
                placeholder = "예: 강남 곱창 같이 먹어요",
            )
            FieldLabel("식당명 / 위치 (선택)", topPadding = 14.dp)
            OutlinedInput(
                value = spot,
                onValueChange = { spot = it.take(40) },
                placeholder = "예: 강남역 11번 출구",
            )
        }

        HbCard {
            FieldLabel(if (mode == MealMode.instant) "몇 시간 뒤?" else "며칠 뒤?")
            val options = if (mode == MealMode.instant) listOf(1, 2, 3) else listOf(1, 2, 3, 7)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                options.forEach { v ->
                    Pill(
                        label = if (mode == MealMode.instant) "${v}시간" else "${v}일",
                        selected = hoursLater == v,
                        onClick = { hoursLater = v },
                    )
                }
            }
            FieldLabel("자리 (호스트 포함)", topPadding = 14.dp)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(2, 3, 4, 5, 6).forEach { v ->
                    Pill(
                        label = "${v}명",
                        selected = maxPeople == v,
                        onClick = { maxPeople = v },
                    )
                }
            }
        }

        HbCard {
            FieldLabel("분위기 태그 (복수)")
            Row(
                Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                VIBE_TAGS.forEach { v ->
                    val on = tags.contains(v.key)
                    Pill(label = v.label, selected = on) {
                        tags = if (on) tags - v.key else tags + v.key
                    }
                }
            }
            FieldLabel("메모 (선택)", topPadding = 14.dp)
            OutlinedInput(
                value = notes,
                onValueChange = { notes = it.take(120) },
                placeholder = "예: 1시간 안에 끝낼 수 있는 분",
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            OutlinedButton(
                onClick = onCancel,
                shape = HbButtonShape,
                modifier = Modifier.weight(1f),
            ) { Text("취소", color = HbFgSoft) }
            Button(
                onClick = {
                    val meal = Meal(
                        id = "m_${System.currentTimeMillis()}",
                        hostId = me.id,
                        mode = mode,
                        menu = menu,
                        title = title.ifBlank { "${district.name} ${menu.name} 함밥" },
                        district = district,
                        spotLabel = spot.ifBlank { null },
                        meetAt = System.currentTimeMillis() + when (mode) {
                            MealMode.instant -> hoursLater * 60L * 60_000L
                            MealMode.scheduled -> hoursLater * 24L * 60L * 60_000L
                        },
                        maxPeople = maxPeople,
                        tags = tags.toList(),
                        notes = notes.ifBlank { null },
                    )
                    MockStore.createMeal(meal)
                    onCreated(meal.id)
                },
                enabled = title.trim().isNotEmpty(),
                shape = HbButtonShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = HbAmber,
                    contentColor = HbCreamCard,
                    disabledContainerColor = HbCreamSoft,
                    disabledContentColor = HbFgSoft,
                ),
                modifier = Modifier.weight(2f),
            ) { Text("함밥 만들기") }
        }
    }
}

@Composable
private fun FieldLabel(text: String, topPadding: androidx.compose.ui.unit.Dp = 0.dp) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium.copy(color = HbFgSoft, fontWeight = FontWeight.SemiBold),
        modifier = Modifier.padding(top = topPadding, bottom = 8.dp),
    )
}

@Composable
private fun ModeChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) HbAmber else HbCreamSoft
    val fg = if (selected) HbCreamCard else HbBrown
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge.copy(color = fg, fontWeight = FontWeight.SemiBold),
        modifier = Modifier
            .clip(HbChipShape)
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    )
}

@Composable
private fun Pill(label: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) HbAmber else HbCreamSoft
    val fg = if (selected) HbCreamCard else HbBrown
    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium.copy(color = fg, fontWeight = FontWeight.SemiBold),
        modifier = Modifier
            .clip(HbChipShape)
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
}

@Composable
private fun OutlinedInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(HbCreamCard)
            .padding(horizontal = 12.dp, vertical = 12.dp),
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(color = HbFg, fontSize = 15.sp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            decorationBox = { inner ->
                if (value.isEmpty()) {
                    Text(
                        placeholder,
                        style = MaterialTheme.typography.bodyMedium.copy(color = HbFgSoft),
                    )
                }
                inner()
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Suppress("unused") private val unused = HbBorderStrong
@Suppress("unused") private val unused2 = HbAmberDeep
@Suppress("unused") private val unused3 = PaddingValues(0.dp)
