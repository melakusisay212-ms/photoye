package com.habesha.photos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habesha.photos.data.MediaViewModel
import com.habesha.photos.util.EthiopianCalendar
import com.habesha.photos.ui.theme.Accent
import com.habesha.photos.ui.theme.Bg
import com.habesha.photos.ui.theme.BgElevated
import com.habesha.photos.ui.theme.TextMuted
import com.habesha.photos.ui.theme.TextPrimary
import com.habesha.photos.ui.theme.TextSecondary

@Composable
fun SearchScreen(vm: MediaViewModel, onResult: () -> Unit) {
    val suggestions = remember(vm.allMedia, vm.searchQuery) {
        buildSuggestions(vm)
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Bg)
            .statusBarsPadding()
    ) {
        OutlinedTextField(
            value = vm.searchQuery,
            onValueChange = { vm.searchQuery = it },
            placeholder = { Text("Search dates, places, folders…", color = TextMuted) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = TextMuted) },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BgElevated,
                unfocusedContainerColor = BgElevated,
                focusedBorderColor = Accent.copy(alpha = 0.45f),
                unfocusedBorderColor = TextMuted.copy(alpha = 0.2f),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = Accent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        Text(
            "SUGGESTIONS",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextMuted,
            letterSpacing = 0.6.sp,
            modifier = Modifier.padding(start = 20.dp, top = 4.dp, bottom = 8.dp)
        )

        LazyColumn {
            items(suggestions) { s ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            when (s.type) {
                                "date" -> {
                                    vm.filterEthDate = s.eth
                                    vm.currentFolder = null
                                    vm.searchQuery = ""
                                }
                                "folder" -> {
                                    vm.currentFolder = s.label
                                    vm.filterEthDate = null
                                    vm.searchQuery = ""
                                }
                                "videos" -> {
                                    vm.currentFolder = "Videos"
                                    vm.filterEthDate = null
                                    vm.searchQuery = ""
                                }
                            }
                            onResult()
                        }
                        .padding(horizontal = 20.dp, vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        s.label,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "${s.count}",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

private data class Suggestion(
    val type: String,
    val label: String,
    val count: Int,
    val eth: EthiopianCalendar.EthDate? = null
)

private fun buildSuggestions(vm: MediaViewModel): List<Suggestion> {
    val q = vm.searchQuery.trim().lowercase()
    val items = mutableListOf<Suggestion>()

    // Ethiopian date groups
    vm.groupedTimeline(vm.allMedia).forEach { (eth, list) ->
        val label = eth.displayAmharic()
        if (q.isEmpty() || label.contains(q, true) || list.first().gregorianDate.lowercase().contains(q)) {
            items += Suggestion("date", label, list.size, eth)
        }
    }

    // Folders
    vm.folders().forEach { f ->
        if (q.isEmpty() || f.name.lowercase().contains(q)) {
            items += Suggestion("folder", f.name, f.count)
        }
    }

    // Videos
    val vidCount = vm.allMedia.count { it.isVideo }
    if (q.isEmpty() || "video".contains(q) || "videos".contains(q)) {
        items += Suggestion("videos", "Videos", vidCount)
    }

    return items.take(20)
}
