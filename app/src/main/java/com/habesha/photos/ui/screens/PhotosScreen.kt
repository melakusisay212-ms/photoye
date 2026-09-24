package com.habesha.photos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habesha.photos.data.MediaViewModel
import com.habesha.photos.ui.components.PhotoThumb
import com.habesha.photos.ui.theme.Accent
import com.habesha.photos.ui.theme.AccentStrong
import com.habesha.photos.ui.theme.Bg
import com.habesha.photos.ui.theme.TextMuted
import com.habesha.photos.ui.theme.TextPrimary

@Composable
fun PhotosScreen(vm: MediaViewModel) {
    Column(Modifier.fillMaxSize().background(Bg)) {
        // Top bar
        Row(
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(52.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Photos Habesha",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                modifier = Modifier.padding(start = 10.dp).weight(1f)
            )
            IconButton(onClick = { /* search handled by tab */ }) {
                Icon(Icons.Default.Search, "Search", tint = TextPrimary)
            }
            IconButton(onClick = { }) {
                Icon(Icons.Default.MoreVert, "Menu", tint = TextPrimary)
            }
        }

        // Active filter chip
        val hasFilter = vm.filterEthDate != null || vm.currentFolder != null
        if (hasFilter) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    when {
                        vm.filterEthDate != null -> vm.filterEthDate!!.displayAmharic()
                        else -> vm.currentFolder ?: ""
                    },
                    color = AccentStrong,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { vm.clearFilters() }) {
                    Icon(Icons.Default.Close, null, tint = TextMuted)
                    Text("Clear", color = TextMuted, fontSize = 12.sp)
                }
            }
        }

        when {
            vm.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Accent)
                }
            }
            vm.displayedMedia().isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No photos found", color = TextMuted, fontSize = 14.sp)
                }
            }
            else -> {
                val groups = vm.groupedTimeline()
                LazyColumn(Modifier.fillMaxSize()) {
                    items(groups, key = { it.first.key() }) { (eth, photos) ->
                        // Date header
                        Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 8.dp)) {
                            Text(
                                eth.displayFull(),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                photos.first().gregorianDate,
                                fontSize = 12.sp,
                                color = TextMuted,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        // 3-column dense grid for this day
                        val rows = photos.chunked(3)
                        rows.forEach { row ->
                            Row(Modifier.fillMaxWidth()) {
                                row.forEach { item ->
                                    PhotoThumb(
                                        item = item,
                                        onClick = { vm.selectedItem = item },
                                        modifier = Modifier.weight(1f).padding(1.dp)
                                    )
                                }
                                // fill remaining cells
                                repeat(3 - row.size) {
                                    Spacer(Modifier.weight(1f))
                                }
                            }
                        }
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}
