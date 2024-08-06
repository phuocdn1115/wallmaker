package com.app.photomaker.download

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.app.photomaker.R
import com.app.photomaker.WallpaperMakerApp
import com.app.photomaker.utils.FileUtils

class DownloadWallpaperManager(private val context: Context) {
    private val downloadManager: DownloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    @SuppressLint("Range")
    fun downloadFile(
        url: String,
        onStart: () -> Unit,
        onSuccess: (pathFile: String) -> Unit,
        onError: () -> Unit
    ) {
        val request = DownloadManager.Request(Uri.parse(url))
        request.apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            setAllowedOverRoaming(false)
            setTitle(context.getString(R.string.app_name))
            setDescription("Wallpaper of walla")
            setDestinationInExternalFilesDir(WallpaperMakerApp.instance, Environment.DIRECTORY_DOWNLOADS, FileUtils.getFileName(url))
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION)
        }
        val downloadId = downloadManager.enqueue(request)
        val query = DownloadManager.Query().setFilterById(downloadId)
        Thread {
            var downloading = true
            while (downloading) {
                val cursor = downloadManager.query(query)
                cursor.moveToFirst()
                if (cursor != null) {
                    when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                        DownloadManager.STATUS_FAILED -> {
                            onError.invoke()
                            downloading = false
                        }
                        DownloadManager.STATUS_PENDING, DownloadManager.STATUS_PAUSED -> {}
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            val pathFile = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                            onSuccess.invoke(pathFile)
                            downloading = false
                        }
                        DownloadManager.STATUS_RUNNING -> {
                            onStart.invoke()
                        }
                    }
                    cursor.close()
                }
            }
        }.start()
    }
}