package com.habesha.photos.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habesha.photos.ui.theme.AccentStrong
import com.habesha.photos.ui.theme.BgBar
import com.habesha.photos.ui.theme.TextMuted

@Composable
fun BottomNav(selected: Int, onSelect: (Int) -> Unit) {
    val items = listOf(
        Triple("Photos", Icons.Default.PhotoLibrary, 0),
        Triple("Calendar", Icons.Default.CalendarMonth, 1),
        Triple("Folders", Icons.Default.Folder, 2),
        Triple("Search", Icons.Default.Search, 3)
    )

    NavigationBar(
        containerColor = BgBar,
        contentColor = TextMuted,
        tonalElevation = 0.dp
    ) {
        items.forEach { (label, icon, index) ->
            val isSelected = selected == index
            NavigationBarItem(
                selected = isSelected,
                onClick = { onSelect(index) },
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            icon,
                            contentDescription = label,
                            modifier = Modifier.size(22.dp),
                            tint = if (isSelected) AccentStrong else TextMuted
                        )
                    }
                },
                label = {
                    Text(
                        label,
                        fontSize = 10.sp,
                        color = if (isSelected) AccentStrong else TextMuted
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                    selectedIconColor = AccentStrong,
                    unselectedIconColor = TextMuted
                )
            )
        }
    }
}
