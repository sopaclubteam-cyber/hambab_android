package com.hambab.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hambab.app.ui.theme.HbAmber
import com.hambab.app.ui.theme.HbAmberDeep
import com.hambab.app.ui.theme.HbBorder
import com.hambab.app.ui.theme.HbBrown
import com.hambab.app.ui.theme.HbButtonShape
import com.hambab.app.ui.theme.HbCream
import com.hambab.app.ui.theme.HbCreamCard
import com.hambab.app.ui.theme.HbFgSoft
import com.hambab.app.ui.theme.HbShadowStrong

@Composable
fun HambabTopBar(
    onCreateMeal: () -> Unit,
    onProfile: () -> Unit,
    nicknameOrCta: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(HbCream)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = "🍚", fontSize = 22.sp)
        Text(
            text = "함밥",
            style = MaterialTheme.typography.titleLarge.copy(
                color = HbBrown, fontWeight = FontWeight.ExtraBold,
            ),
        )
        Box(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier
                .shadow(2.dp, MaterialTheme.shapes.large)
                .background(HbAmber, MaterialTheme.shapes.large)
                .clickable { onCreateMeal() }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(Icons.Outlined.Add, null, tint = HbCreamCard, modifier = Modifier.size(16.dp))
            Text(
                "함밥 만들기",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = HbCreamCard, fontWeight = FontWeight.SemiBold,
                ),
            )
        }
        Text(
            text = nicknameOrCta,
            style = MaterialTheme.typography.labelMedium.copy(color = HbFgSoft),
            modifier = Modifier
                .background(HbCreamCard, MaterialTheme.shapes.large)
                .clickable { onProfile() }
                .padding(horizontal = 12.dp, vertical = 8.dp),
        )
    }
}

// 5탭 구조: 홈 / 피드 / [+발행 amber 중앙] / 지금·예약 / 내 함밥
@Composable
fun HambabBottomBar(
    current: String,
    onSelect: (String) -> Unit,
    onCreateFeed: () -> Unit = {},
) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(HbBorder),
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(HbCreamCard)
            .padding(top = 8.dp, bottom = 12.dp)
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        BarItem("home", "홈", Icons.Outlined.Home, current) { onSelect("home") }
        BarItem("feed", "피드", Icons.Outlined.GridView, current) { onSelect("feed") }
        // 중앙 + 발행 버튼 (amber, 인스타 + 버튼처럼)
        Box(
            modifier = Modifier
                .weight(1f)
                .clickable { onCreateFeed() },
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .shadow(4.dp, HbButtonShape, ambientColor = HbShadowStrong, spotColor = HbShadowStrong)
                    .clip(HbButtonShape)
                    .background(HbAmber),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Outlined.Add,
                    contentDescription = "피드 올리기",
                    tint = HbCreamCard,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
        BarItem("now", "지금", Icons.Outlined.Bolt, current) { onSelect("now") }
        BarItem("profile", "내 함밥", Icons.Outlined.Person, current) { onSelect("profile") }
    }
}

@Composable
private fun RowScope.BarItem(
    key: String,
    label: String,
    icon: ImageVector,
    current: String,
    onClick: () -> Unit,
) {
    val active = current == key
    val tint = if (active) HbAmberDeep else HbFgSoft
    Box(
        modifier = Modifier
            .weight(1f)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(18.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = tint, fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
                ),
            )
        }
    }
}
