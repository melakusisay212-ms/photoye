package com.habesha.photos

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat

import com.habesha.photos.data.MediaViewModel
import com.habesha.photos.ui.components.BottomNav
import com.habesha.photos.ui.components.PermissionScreen
import com.habesha.photos.ui.components.ViewerOverlay
import com.habesha.photos.ui.screens.CalendarScreen
import com.habesha.photos.ui.screens.FoldersScreen
import com.habesha.photos.ui.screens.PhotosScreen
import com.habesha.photos.ui.screens.SearchScreen
import com.habesha.photos.ui.theme.Bg
import com.habesha.photos.ui.theme.PhotosHabeshaTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MediaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PhotosHabeshaTheme {
                val permissions = requiredPermissions()
                val allGranted = permissions.all {
                    ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
                }

                var granted by remember { mutableStateOf(allGranted) }

                val launcher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { result ->
                    granted = result.values.all { it }
                    if (granted) {
                        viewModel.hasPermission = true
                        viewModel.loadMedia()
                    }
                }

                LaunchedEffect(Unit) {
                    if (allGranted) {
                        viewModel.hasPermission = true
                        viewModel.loadMedia()
                    }
                }

                if (!granted) {
                    PermissionScreen(onRequest = { launcher.launch(permissions) })
                } else {
                    MainScaffold(viewModel)
                }
            }
        }
    }

    private fun requiredPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
}

@Composable
private fun MainScaffold(vm: MediaViewModel) {
    var tab by remember { mutableIntStateOf(0) }

    Box(Modifier.fillMaxSize().background(Bg)) {
        Scaffold(
            containerColor = Bg,
            bottomBar = {
                BottomNav(selected = tab, onSelect = { tab = it })
            }
        ) { padding ->
            Box(Modifier.padding(padding).fillMaxSize()) {
                when (tab) {
                    0 -> PhotosScreen(vm)
                    1 -> CalendarScreen(vm, onDaySelected = { tab = 0 })
                    2 -> FoldersScreen(vm, onOpenFolder = { tab = 0 })
                    3 -> SearchScreen(vm, onResult = { tab = 0 })
                }
            }
        }

        // Full-screen viewer
        vm.selectedItem?.let { item ->
            ViewerOverlay(
                item = item,
                onClose = { vm.selectedItem = null },
                onToggleFav = { vm.toggleFavorite(item) }
            )
        }
    }
}
