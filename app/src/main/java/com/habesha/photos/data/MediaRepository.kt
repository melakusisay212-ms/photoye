package com.habesha.photos.data

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.habesha.photos.util.EthiopianCalendar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Loads photos & videos from the device via MediaStore.
 * Pure local-first — no network, no upload.
 */
class MediaRepository(private val context: Context) {

    private val dateFmt = SimpleDateFormat("MMM d, yyyy", Locale.US)
    private val timeFmt = SimpleDateFormat("h:mm a", Locale.US)

    suspend fun loadAllMedia(): List<MediaItem> = withContext(Dispatchers.IO) {
        val items = mutableListOf<MediaItem>()
        items += queryImages()
        items += queryVideos()
        items.sortedByDescending { it.dateTaken }
    }

    private fun queryImages(): List<MediaItem> {
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )

        val sort = "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        val result = mutableListOf<MediaItem>()

        context.contentResolver.query(collection, projection, null, null, sort)?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val takenCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
            val addedCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
            val wCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
            val hCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
            val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val bucketIdCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val bucketNameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val taken = cursor.getLong(takenCol).takeIf { it > 0 }
                    ?: (cursor.getLong(addedCol) * 1000L)
                val uri = ContentUris.withAppendedId(collection, id)
                val date = Date(taken)

                result += MediaItem(
                    id = id,
                    uri = uri,
                    displayName = cursor.getString(nameCol) ?: "",
                    dateTaken = taken,
                    gregorianDate = dateFmt.format(date),
                    time = timeFmt.format(date),
                    ethDate = EthiopianCalendar.fromEpochMillis(taken),
                    mimeType = cursor.getString(mimeCol) ?: "image/*",
                    isVideo = false,
                    width = cursor.getInt(wCol),
                    height = cursor.getInt(hCol),
                    bucketId = cursor.getString(bucketIdCol) ?: "",
                    bucketName = cursor.getString(bucketNameCol) ?: "Camera",
                    size = cursor.getLong(sizeCol)
                )
            }
        }
        return result
    }

    private fun queryVideos(): List<MediaItem> {
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATE_TAKEN,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.HEIGHT,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.BUCKET_ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME
        )

        val sort = "${MediaStore.Video.Media.DATE_TAKEN} DESC"
        val result = mutableListOf<MediaItem>()

        context.contentResolver.query(collection, projection, null, null, sort)?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val takenCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN)
            val addedCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
            val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)
            val durCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val wCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH)
            val hCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT)
            val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val bucketIdCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID)
            val bucketNameCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val taken = cursor.getLong(takenCol).takeIf { it > 0 }
                    ?: (cursor.getLong(addedCol) * 1000L)
                val uri = ContentUris.withAppendedId(collection, id)
                val date = Date(taken)

                result += MediaItem(
                    id = id,
                    uri = uri,
                    displayName = cursor.getString(nameCol) ?: "",
                    dateTaken = taken,
                    gregorianDate = dateFmt.format(date),
                    time = timeFmt.format(date),
                    ethDate = EthiopianCalendar.fromEpochMillis(taken),
                    mimeType = cursor.getString(mimeCol) ?: "video/*",
                    isVideo = true,
                    durationMs = cursor.getLong(durCol),
                    width = cursor.getInt(wCol),
                    height = cursor.getInt(hCol),
                    bucketId = cursor.getString(bucketIdCol) ?: "",
                    bucketName = cursor.getString(bucketNameCol) ?: "Videos",
                    size = cursor.getLong(sizeCol)
                )
            }
        }
        return result
    }

    fun groupByEthDate(items: List<MediaItem>): List<Pair<EthiopianCalendar.EthDate, List<MediaItem>>> {
        return items
            .groupBy { it.ethKey }
            .map { (_, list) -> list.first().ethDate to list.sortedByDescending { it.dateTaken } }
            .sortedWith(compareByDescending<Pair<EthiopianCalendar.EthDate, List<MediaItem>>> { it.first.year }
                .thenByDescending { it.first.month }
                .thenByDescending { it.first.day })
    }

    fun folders(items: List<MediaItem>): List<FolderInfo> {
        return items
            .groupBy { it.bucketName.ifBlank { "Other" } }
            .map { (name, list) ->
                FolderInfo(
                    name = name,
                    count = list.size,
                    coverUri = list.maxByOrNull { it.dateTaken }?.uri,
                    totalSize = list.sumOf { it.size }
                )
            }
            .sortedByDescending { it.count }
    }
}

data class FolderInfo(
    val name: String,
    val count: Int,
    val coverUri: Uri?,
    val totalSize: Long
) {
    val sizeLabel: String
        get() {
            val mb = totalSize / (1024.0 * 1024.0)
            return if (mb >= 1024) "%.1f GB".format(mb / 1024) else "%.0f MB".format(mb)
        }
}
