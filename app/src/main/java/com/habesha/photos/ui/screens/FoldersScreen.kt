package com.habesha.photos.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.habesha.photos.data.FolderInfo
import com.habesha.photos.data.MediaViewModel
import com.habesha.photos.ui.theme.Bg
import com.habesha.photos.ui.theme.BgElevated
import com.habesha.photos.ui.theme.TextMuted
import com.habesha.photos.ui.theme.TextPrimary
import com.habesha.photos.ui.theme.TextSecondary

@Composable
fun FoldersScreen(vm: MediaViewModel, onOpenFolder: () -> Unit) {
    val folders = vm.folders()

    Column(
        Modifier
            .fillMaxSize()
            .background(Bg)
            .statusBarsPadding()
    ) {
        Text(
            "Folders",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.padding(start = 18.dp, top = 14.dp, bottom = 8.dp)
        )

        // Quick chips
        Text(
            "QUICK ACCESS",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextMuted,
            letterSpacing = 0.8.sp,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 10.dp)
        )
        Row(
            Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            QuickChip("Videos", Icons.Default.Videocam) {
                vm.currentFolder = "Videos"
                vm.filterEthDate = null
                onOpenFolder()
            }
            QuickChip("Favourites", Icons.Default.Favorite) {
                // filter favorites via search-like path — for now open all and user can see ♥
                vm.currentFolder = null
                vm.filterEthDate = null
                onOpenFolder()
            }
            QuickChip("Screenshots", Icons.Default.PhoneAndroid) {
                vm.currentFolder = "Screenshots"
                vm.filterEthDate = null
                onOpenFolder()
            }
        }

        Text(
            "DEVICE",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextMuted,
            letterSpacing = 0.8.sp,
            modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 10.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(folders) { folder ->
                FolderCard(folder) {
                    vm.currentFolder = folder.name
                    vm.filterEthDate = null
                    onOpenFolder()
                }
            }
        }
    }
}

@Composable
private fun QuickChip(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(BgElevated),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, label, tint = TextSecondary, modifier = Modifier.size(20.dp))
        }
        Text(
            label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

@Composable
private fun FolderCard(folder: FolderInfo, onClick: () -> Unit) {
    Box(
        Modifier
            .aspectRatio(1f / 1.08f)
            .clip(RoundedCornerShape(14.dp))
            .background(BgElevated)
            .clickable(onClick = onClick)
    ) {
        folder.coverUri?.let { uri ->
            AsyncImage(
                model = uri,
                contentDescription = folder.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.88f)),
                        startY = 80f
                    )
                )
        )
        Column(
            Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Text(
                folder.name,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "${folder.count} items · ${folder.sizeLabel}",
                color = Color.White.copy(alpha = 0.65f),
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 3.dp)
            )
        }
    }
}
