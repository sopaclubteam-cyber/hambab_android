package duckring.hambab.com.ui.screen.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import duckring.hambab.com.data.auth.AuthRepository
import duckring.hambab.com.data.store.MockStore
import duckring.hambab.com.data.store.Seed
import duckring.hambab.com.ui.component.HbCard
import duckring.hambab.com.ui.theme.HbAmber
import duckring.hambab.com.ui.theme.HbAmberDeep
import duckring.hambab.com.ui.theme.HbBrown
import duckring.hambab.com.ui.theme.HbButtonShape
import duckring.hambab.com.ui.theme.HbCreamCard
import duckring.hambab.com.ui.theme.HbCreamSoft
import duckring.hambab.com.ui.theme.HbFg
import duckring.hambab.com.ui.theme.HbFgSoft
import duckring.hambab.com.util.label

@Composable
fun LoginScreen(onDone: () -> Unit) {
    var nickname by remember { mutableStateOf("") }
    MockStore.ensureSeeded()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                "함밥 시작하기",
                style = MaterialTheme.typography.headlineLarge.copy(color = HbFg),
            )
            Text(
                "닉네임 하나면 충분해요. 카카오/애플 로그인은 곧 들어와요.",
                style = MaterialTheme.typography.bodyMedium.copy(color = HbFgSoft),
            )
        }

        HbCard(padding = 16.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "닉네임",
                    style = MaterialTheme.typography.labelMedium.copy(color = HbFgSoft),
                )
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(HbCreamSoft)
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                ) {
                    BasicTextField(
                        value = nickname,
                        onValueChange = { nickname = it.take(12) },
                        singleLine = true,
                        textStyle = TextStyle(color = HbFg, fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            capitalization = KeyboardCapitalization.None,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done,
                        ),
                        decorationBox = { inner ->
                            if (nickname.isEmpty()) {
                                Text(
                                    "예: 곱창마니아",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = HbFgSoft),
                                )
                            }
                            inner()
                        },
                    )
                }
                Button(
                    onClick = {
                        AuthRepository.signIn(nickname)
                        onDone()
                    },
                    enabled = nickname.trim().isNotEmpty(),
                    shape = HbButtonShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HbAmber,
                        contentColor = HbCreamCard,
                        disabledContainerColor = HbCreamSoft,
                        disabledContentColor = HbFgSoft,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("함밥 시작하기") }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "시드 유저로 체험하기",
                style = MaterialTheme.typography.titleSmall.copy(color = HbBrown),
            )
            Text(
                "이미 만들어진 함밥/매너 점수가 있는 데모 계정으로 둘러볼 수 있어요.",
                style = MaterialTheme.typography.bodySmall.copy(color = HbFgSoft),
            )
            Seed.users.forEach { u ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(HbCreamCard)
                        .clickable {
                            AuthRepository.signInAsSeed(u.id)
                            onDone()
                        }
                        .padding(PaddingValues(horizontal = 14.dp, vertical = 12.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            u.nickname,
                            style = MaterialTheme.typography.titleSmall.copy(color = HbFg),
                        )
                        Text(
                            "${u.trustGrade.label()} · 매너 ${u.mannerScore} · 선호 ${u.favMenus.size}개",
                            style = MaterialTheme.typography.labelSmall.copy(color = HbFgSoft),
                        )
                    }
                    Text(
                        "체험 시작 →",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = HbAmberDeep, fontWeight = FontWeight.SemiBold,
                        ),
                    )
                }
            }
        }

        @Suppress("UNUSED_EXPRESSION") Color.Unspecified
    }
}
