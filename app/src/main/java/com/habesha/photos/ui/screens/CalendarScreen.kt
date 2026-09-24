package com.habesha.photos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.habesha.photos.data.MediaViewModel
import com.habesha.photos.util.EthiopianCalendar
import com.habesha.photos.ui.theme.AccentSoft
import com.habesha.photos.ui.theme.AccentStrong
import com.habesha.photos.ui.theme.Bg
import com.habesha.photos.ui.theme.BgElevated
import com.habesha.photos.ui.theme.TextMuted
import com.habesha.photos.ui.theme.TextPrimary
import com.habesha.photos.ui.theme.TextSecondary

@Composable
fun CalendarScreen(vm: MediaViewModel, onDaySelected: () -> Unit) {
    // Determine a sensible default month from the newest photo, else Meskerem
    val defaultMonth = vm.allMedia.firstOrNull()?.ethDate?.month ?: 1
    val defaultYear = vm.allMedia.firstOrNull()?.ethDate?.year ?: 2019
    var month by remember { mutableIntStateOf(defaultMonth.coerceIn(1, 13)) }
    var year by remember { mutableIntStateOf(defaultYear) }

    // Map day → first photo for thumbnail
    val dayPhotos = remember(vm.allMedia, month, year) {
        vm.allMedia
            .filter { it.ethDate.month == month && it.ethDate.year == year }
            .groupBy { it.ethDate.day }
            .mapValues { it.value.maxByOrNull { p -> p.dateTaken } }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Bg)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                EthiopianCalendar.MONTH_NAMES.getOrElse(month - 1) { "?" },
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                "$year ዓ.ም.",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Month chips
        Row(
            Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EthiopianCalendar.MONTH_NAMES.take(12).forEachIndexed { idx, name ->
                val selected = (idx + 1) == month
                Box(
                    Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selected) AccentSoft else BgElevated)
                        .then(
                            if (selected) Modifier.border(1.dp, AccentStrong.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                            else Modifier
                        )
                        .clickable { month = idx + 1 }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (selected) AccentStrong else TextSecondary
                    )
                }
            }
        }

        // Weekday headers
        Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp)) {
            listOf("ሰ", "ማ", "ረ", "ሐ", "ዓ", "ዓ", "ቅ").forEach { d ->
                Text(
                    d,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextMuted
                )
            }
        }

        // Day grid (30 days for months 1–12)
        val daysInMonth = EthiopianCalendar.daysInMonth(year, month)
        val startOffset = (month * 2 + 1) % 7 // approximate; real weekday calc can be added later

        Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            var day = 1
            // leading empties
            var cells = List(startOffset) { null as Int? } + (1..daysInMonth).toList()
            while (cells.size % 7 != 0) cells = cells + null

            cells.chunked(7).forEach { week ->
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 2.5.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    week.forEach { d ->
                        if (d == null) {
                            Box(Modifier.weight(1f).aspectRatio(1f))
                        } else {
                            val photo = dayPhotos[d]
                            val isHoliday = EthiopianCalendar.holidayName(
                                EthiopianCalendar.EthDate(year, month, d)
                            ) != null
                            Box(
                                Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (photo != null) Color.Transparent else Color.White.copy(alpha = 0.03f))
                                    .then(
                                        if (photo == null) Modifier.border(1.dp, Color.White.copy(alpha = 0.04f), RoundedCornerShape(12.dp))
                                        else Modifier
                                    )
                                    .clickable(enabled = photo != null) {
                                        if (photo != null) {
                                            vm.filterEthDate = EthiopianCalendar.EthDate(year, month, d)
                                            vm.currentFolder = null
                                            onDaySelected()
                                        }
                                    }
                            ) {
                                if (photo != null) {
                                    AsyncImage(
                                        model = photo.uri,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Text(
                                        "$d",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(4.dp)
                                    )
                                } else {
                                    Text(
                                        "$d",
                                        color = TextMuted,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                                if (isHoliday) {
                                    Box(
                                        Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(4.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(AccentStrong)
                                            .padding(2.5.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Text(
            "Days with photos show a thumbnail · Tap to open that day",
            fontSize = 12.sp,
            color = TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}
