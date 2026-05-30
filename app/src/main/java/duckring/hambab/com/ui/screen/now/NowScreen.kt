package duckring.hambab.com.ui.screen.now

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import duckring.hambab.com.data.model.District
import duckring.hambab.com.data.model.MealMode
import duckring.hambab.com.data.model.MenuCategory
import duckring.hambab.com.data.store.MockStore
import duckring.hambab.com.ui.component.EmptyState
import duckring.hambab.com.ui.component.FilterBar
import duckring.hambab.com.ui.component.MealCard
import duckring.hambab.com.ui.theme.HbAmberDeep
import duckring.hambab.com.ui.theme.HbFg
import duckring.hambab.com.ui.theme.HbFgSoft
import duckring.hambab.com.util.isInstantWindow

@Composable
fun NowScreen(onMeal: (String) -> Unit) {
    val mealsFlow by MockStore.meals.collectAsState()
    val partsFlow by MockStore.participants.collectAsState()
    @Suppress("UNUSED_VARIABLE") val t = mealsFlow.size + partsFlow.size

    var district by remember { mutableStateOf<District?>(null) }
    var menu by remember { mutableStateOf<MenuCategory?>(null) }

    val list = MockStore.listMeals(mode = MealMode.instant, district = district, menu = menu)
        .filter { isInstantWindow(it.meal.meetAt) }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    "INSTANT",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = HbAmberDeep, fontWeight = FontWeight.SemiBold,
                    ),
                )
                Text("지금 함밥", style = MaterialTheme.typography.headlineLarge.copy(color = HbFg))
                Text(
                    "지금부터 3시간 이내 시작하는 식사. 빠르게 결정하고 바로 만나요.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = HbFgSoft),
                )
            }
        }
        item {
            FilterBar(
                selectedDistrict = district,
                selectedMenu = menu,
                onDistrict = { district = it },
                onMenu = { menu = it },
            )
        }
        if (list.isEmpty()) {
            item {
                EmptyState(
                    title = "조건에 맞는 함밥이 없어요",
                    subtitle = "필터를 풀거나 직접 함밥을 만들면 매칭 확률이 올라가요.",
                )
            }
        } else {
            items(list) { meal -> MealCard(meal = meal, onClick = { onMeal(meal.meal.id) }) }
        }
    }
}
