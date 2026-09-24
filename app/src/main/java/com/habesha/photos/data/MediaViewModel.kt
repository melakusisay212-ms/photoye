package com.habesha.photos.data

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.habesha.photos.util.EthiopianCalendar
import kotlinx.coroutines.launch

class MediaViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = MediaRepository(app)

    var allMedia by mutableStateOf<List<MediaItem>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var hasPermission by mutableStateOf(false)

    var selectedItem by mutableStateOf<MediaItem?>(null)

    var currentFolder by mutableStateOf<String?>(null)

    var filterEthDate by mutableStateOf<EthiopianCalendar.EthDate?>(null)

    var searchQuery by mutableStateOf("")

    fun loadMedia() {
        viewModelScope.launch {
            isLoading = true
            try {
                allMedia = repo.loadAllMedia()
            } finally {
                isLoading = false
            }
        }
    }

    fun groupedTimeline(items: List<MediaItem> = displayedMedia()): List<Pair<EthiopianCalendar.EthDate, List<MediaItem>>> {
        return repo.groupByEthDate(items)
    }

    fun folders(): List<FolderInfo> = repo.folders(allMedia)

    fun displayedMedia(): List<MediaItem> {
        var list = allMedia
        filterEthDate?.let { eth ->
            list = list.filter { it.ethDate.year == eth.year && it.ethDate.month == eth.month && it.ethDate.day == eth.day }
        }
        currentFolder?.let { folder ->
            list = if (folder == "Videos") list.filter { it.isVideo }
            else list.filter { it.bucketName.equals(folder, ignoreCase = true) }
        }
        val q = searchQuery.trim()
        if (q.isNotEmpty()) {
            list = list.filter {
                it.ethDate.displayAmharic().contains(q, true) ||
                it.gregorianDate.contains(q, true) ||
                it.bucketName.contains(q, true) ||
                it.displayName.contains(q, true)
            }
        }
        return list
    }

    fun clearFilters() {
        filterEthDate = null
        currentFolder = null
        searchQuery = ""
    }

    fun toggleFavorite(item: MediaItem) {
        val updated = allMedia.map {
            if (it.id == item.id && it.isVideo == item.isVideo) it.copy(isFavorite = !it.isFavorite) else it
        }
        allMedia = updated
        selectedItem = updated.find { it.id == item.id && it.isVideo == item.isVideo }
    }
}
