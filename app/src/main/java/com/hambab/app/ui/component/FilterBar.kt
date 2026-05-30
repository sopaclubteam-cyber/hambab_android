package com.hambab.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.hambab.app.data.model.District
import com.hambab.app.data.model.MenuCategory
import com.hambab.app.ui.theme.HbAmber
import com.hambab.app.ui.theme.HbBrown
import com.hambab.app.ui.theme.HbChipShape
import com.hambab.app.ui.theme.HbCreamCard
import com.hambab.app.ui.theme.HbCreamSoft
import com.hambab.app.util.DISTRICTS
import com.hambab.app.util.MENUS

@Composable
fun FilterBar(
    selectedDistrict: District?,
    selectedMenu: MenuCategory?,
    onDistrict: (District?) -> Unit,
    onMenu: (MenuCategory?) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            FilterPill("전체 지역", selectedDistrict == null) { onDistrict(null) }
            DISTRICTS.forEach { d ->
                FilterPill(d.label, selectedDistrict == d.key) { onDistrict(d.key) }
            }
        }
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            FilterPill("전체 메뉴", selectedMenu == null) { onMenu(null) }
            MENUS.forEach { m ->
                FilterPill("${m.emoji} ${m.label}", selectedMenu == m.key) { onMenu(m.key) }
            }
        }
    }
}

@Composable
private fun FilterPill(label: String, active: Boolean, onClick: () -> Unit) {
    val bg = if (active) HbAmber else HbCreamSoft
    val fg = if (active) HbCreamCard else HbBrown
    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium.copy(color = fg),
        modifier = Modifier
            .clip(HbChipShape)
            .background(bg)
            .clickable(onClick = onClick)
            .padding(PaddingValues(horizontal = 12.dp, vertical = 6.dp)),
    )
}
