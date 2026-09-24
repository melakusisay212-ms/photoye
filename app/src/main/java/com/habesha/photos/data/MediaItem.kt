package com.habesha.photos.data

import android.net.Uri
import com.habesha.photos.util.EthiopianCalendar

/**
 * Local index entry for a single photo or video.
 * The actual bytes stay on device; we only store a content URI + metadata.
 */
data class MediaItem(
    val id: Long,
    val uri: Uri,
    val displayName: String,
    val dateTaken: Long,          // epoch millis (DATE_TAKEN or DATE_ADDED)
    val gregorianDate: String,    // e.g. "Sep 22, 2026"
    val time: String,             // e.g. "2:35 PM"
    val ethDate: EthiopianCalendar.EthDate,
    val mimeType: String,
    val isVideo: Boolean,
    val durationMs: Long = 0,
    val width: Int = 0,
    val height: Int = 0,
    val bucketId: String = "",
    val bucketName: String = "",  // folder / album name from MediaStore
    val size: Long = 0,
    var isFavorite: Boolean = false
) {
    val durationLabel: String
        get() {
            if (!isVideo || durationMs <= 0) return ""
            val totalSec = durationMs / 1000
            val m = totalSec / 60
            val s = totalSec % 60
            return "%d:%02d".format(m, s)
        }

    val ethKey: String get() = ethDate.key()
}
