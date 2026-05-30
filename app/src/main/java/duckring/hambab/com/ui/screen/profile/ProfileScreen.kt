package duckring.hambab.com.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import duckring.hambab.com.data.auth.AuthRepository
import duckring.hambab.com.data.model.ParticipantStatus
import duckring.hambab.com.data.store.MockStore
import duckring.hambab.com.ui.component.HbCard
import duckring.hambab.com.ui.component.MannerBadge
import duckring.hambab.com.ui.component.MealCard
import duckring.hambab.com.ui.theme.HbAmberDeep
import duckring.hambab.com.ui.theme.HbBrown
import duckring.hambab.com.ui.theme.HbCreamSoft
import duckring.hambab.com.ui.theme.HbDanger
import duckring.hambab.com.ui.theme.HbFg
import duckring.hambab.com.ui.theme.HbFgSoft

@Composable
fun ProfileScreen(onSignOut: () -> Unit, onLogin: () -> Unit) {
    val mealsFlow by MockStore.meals.collectAsState()
    val partsFlow by MockStore.participants.collectAsState()
    @Suppress("UNUSED_VARIABLE") val t = mealsFlow.size + partsFlow.size

    val me = AuthRepository.currentUser()
    if (me == null) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("로그인이 필요해요.", style = MaterialTheme.typography.titleMedium.copy(color = HbFg))
            Text(
                "함밥을 만들거나 참여하려면 닉네임 하나 정도는 필요해요.",
                style = MaterialTheme.typography.bodyMedium.copy(color = HbFgSoft),
            )
            Text(
                "로그인 →",
                style = MaterialTheme.typography.labelLarge.copy(color = HbAmberDeep, fontWeight = FontWeight.SemiBold),
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(HbCreamSoft)
                    .clickable(onClick = onLogin)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            )
        }
        return
    }

    val myMeals = MockStore.listMeals().filter { it.meal.hostId == me.id }
    val joinedMeals = MockStore.listMeals().filter { mm ->
        mm.participants.any { p ->
            p.user.id == me.id && p.participant.status == ParticipantStatus.joined && mm.meal.hostId != me.id
        }
    }

    LazyColumn(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp),
    ) {
        item {
            HbCard {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            me.nickname,
                            style = MaterialTheme.typography.headlineMedium.copy(color = HbBrown),
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            MannerBadge(me.trustGrade)
                            Text(
                                "매너 ${me.mannerScore} · 노쇼 ${me.noShowCount}회",
                                style = MaterialTheme.typography.labelMedium.copy(color = HbFgSoft),
                            )
                        }
                    }
                    Text(
                        "로그아웃",
                        style = MaterialTheme.typography.labelMedium.copy(color = HbDanger),
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.medium)
                            .clickable(onClick = onSignOut)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                    )
                }
            }
        }

        item {
            Text("내가 만든 함밥", style = MaterialTheme.typography.titleMedium.copy(color = HbFg))
        }
        if (myMeals.isEmpty()) item { EmptyLine("아직 만든 함밥이 없어요.") }
        else items(myMeals) { meal -> MealCard(meal = meal, onClick = {}) }

        item {
            Text("내가 참여한 함밥", style = MaterialTheme.typography.titleMedium.copy(color = HbFg))
        }
        if (joinedMeals.isEmpty()) item { EmptyLine("아직 참여한 함밥이 없어요.") }
        else items(joinedMeals) { meal -> MealCard(meal = meal, onClick = {}) }

        item {
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(HbCreamSoft)
                    .clickable {
                        MockStore.resetAll()
                        AuthRepository.signOut()
                        onSignOut()
                    }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
            ) {
                Text(
                    "데모 데이터 리셋 (개발용)",
                    style = MaterialTheme.typography.labelMedium.copy(color = HbBrown, fontWeight = FontWeight.SemiBold),
                )
            }
        }
    }
}

@Composable
private fun EmptyLine(text: String) {
    HbCard {
        Text(text, style = MaterialTheme.typography.bodySmall.copy(color = HbFgSoft))
    }
}
