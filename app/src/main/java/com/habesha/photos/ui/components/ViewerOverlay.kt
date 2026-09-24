package com.habesha.photos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.habesha.photos.data.MediaItem
import com.habesha.photos.ui.theme.FavPink
import com.habesha.photos.ui.theme.TextMuted
import com.habesha.photos.ui.theme.TextPrimary
import com.habesha.photos.ui.theme.TextSecondary

@Composable
fun ViewerOverlay(
    item: MediaItem,
    onClose: () -> Unit,
    onToggleFav: () -> Unit
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AsyncImage(
            model = item.uri,
            contentDescription = item.displayName,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )

        // Top bar
        Row(
            Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)
                    )
                )
                .statusBarsPadding()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Close", tint = Color.White)
            }
            Row {
                IconButton(onClick = onToggleFav) {
                    Icon(
                        if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        "Favorite",
                        tint = if (item.isFavorite) FavPink else Color.White
                    )
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Default.MoreVert, "More", tint = Color.White)
                }
            }
        }

        // Bottom metadata
        Column(
            Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
                    )
                )
                .padding(18.dp)
                .padding(bottom = 12.dp)
        ) {
            Text(
                item.ethDate.displayFull(),
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "${item.gregorianDate} · ${item.time}",
                color = TextSecondary,
                fontSize = 13.sp
            )
            Row(
                Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("📱 ${item.bucketName}", color = TextMuted, fontSize = 12.sp)
                if (item.isVideo) {
                    Text("▶ ${item.durationLabel}", color = TextMuted, fontSize = 12.sp)
                }
            }
        }
    }
}
