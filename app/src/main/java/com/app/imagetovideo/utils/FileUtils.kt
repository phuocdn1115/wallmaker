package com.app.imagetovideo.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.app.imagetovideo.BuildConfig
import com.app.imagetovideo.WallpaperMakerApp
import com.app.imagetovideo.ext.ContextExt.memoryByGB
import com.app.imagetovideo.utils.extension.lastName
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.Utils
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files.delete
import java.text.SimpleDateFormat
import java.util.*

object FileUtils {
    private val BUFFER_SIZE: Int = if (WallpaperMakerApp.instance.memoryByGB > 2)
        DEFAULT_BUFFER_SIZE
    else 1024 * 4
    const val FOLDER_APP = "wallpaper_maker"
    const val PLAYLIST_FOLDER = "playlist"
    const val firstFileName = "alo"
    private val internalDir by lazy {
        val file = File(WallpaperMakerApp.instance.filesDir, FOLDER_APP)
        if (!file.exists()) {
            file.mkdir()
        }
        file
    }

    private val playlistFolder by lazy {
        val file = File(internalDir, PLAYLIST_FOLDER)
        if(!file.exists()) {
            file.mkdir()
        }
        file
    }

    @Suppress("DEPRECATION")
    fun addImageToGallery(fileExport: File, url: String? = null) : Uri? {
        val name = url?.let { getFileNameFromURL(it) } ?: firstFileName.plus("_").plus(fileExport.lastName())
        val isVideo = name.contains(".mp4")
        val values = ContentValues().apply {
            put(MediaStore.Files.FileColumns.DATE_ADDED, System.currentTimeMillis() / 1000)
            put(MediaStore.Files.FileColumns.DATE_MODIFIED, System.currentTimeMillis() / 1000)
            put(MediaStore.Files.FileColumns.DISPLAY_NAME, name)
            put(MediaStore.Files.FileColumns.MIME_TYPE, if (isVideo) "video/mp4" else "image/jpeg")
        }
        val contentResolver = WallpaperMakerApp.instance.contentResolver
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            var file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), name)
            file.setReadable(true)
            file.setWritable(true)
            file = fileExport.copyTo(file, true, BUFFER_SIZE)
            values.put(MediaStore.Files.FileColumns.DATA, file.path)
            contentResolver.insert(
                if (isVideo) MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                else MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
            )
        } else {
            deleteFileUsingFileName(name)
            values.put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            values.put(MediaStore.Files.FileColumns.IS_PENDING, 1)
            val uri = contentResolver.insert(
                MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
                values
            )?.also { copyFileData(it, fileExport) }
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            uri?.also { contentResolver.update(it, values, null, null) }
        }
    }

    fun copyFileToPlaylistFolder(fileExport: File, url : String) : File {
        val file = getPlayListFile(url)
        file.setReadable(true)
        file.setWritable(true)
        fileExport.copyTo(file,true, BUFFER_SIZE)
        return file
    }

    fun getFileNameFromURL(url: String): String {
        return firstFileName.plus("_") + url.substring(url.lastIndexOf("/") + 1)
    }

    fun getPlayListFile(url: String) : File {
        return File(playlistFolder, getFileNameFromURL(url))
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun copyFileData(destination: Uri, fileExport: File) {
        try {
            WallpaperMakerApp.instance.contentResolver.openFileDescriptor(destination, "w")?.use { p ->
                ParcelFileDescriptor.AutoCloseOutputStream(p).use { parcel ->
                    val fileReader = ByteArray(BUFFER_SIZE)
                    fileExport.inputStream().use {inputStream ->
                        var read: Int
                        while (inputStream.read(fileReader).also { read = it } != -1) {
                            parcel.write(fileReader, 0, read)
                        }
                    }
                }
            }
            WallpaperMakerApp.instance.contentResolver.openFileDescriptor(destination, "rw")?.use { p ->
                ExifInterface(p.fileDescriptor)
                    .apply {
                        val dateTime = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.US).format(
                            Date()
                        )
                        setAttribute(ExifInterface.TAG_DATETIME_ORIGINAL, dateTime)
                        setAttribute(ExifInterface.TAG_DATETIME, dateTime)
                        setAttribute(ExifInterface.TAG_DATETIME_DIGITIZED, dateTime)
                        saveAttributes()
                    }
            }
        } catch (e: Exception) { }
    }

    fun deleteFileUsingFileName(fileName: String): Boolean {
        return getUriFromFileName(fileName)?.let {
            WallpaperMakerApp.instance.contentResolver.delete(it, MediaStore.Files.FileColumns.DISPLAY_NAME + "=?", arrayOf(fileName)) > 0
        } ?: false
    }

    @SuppressLint("Range")
    fun getUriFromFileName(fileName: String): Uri? {
        val projection = arrayOf(MediaStore.Files.FileColumns._ID)
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        return WallpaperMakerApp.instance.contentResolver.query(
            uri,
            projection,
            MediaStore.Files.FileColumns.DISPLAY_NAME + " LIKE ?",
            arrayOf(fileName),
            null
        )?.use {
            it.moveToFirst()
            if (it.count > 0) {
                val fileId = it.getLong(it.getColumnIndex(projection[0]))
                Uri.parse("$uri/$fileId")
            } else null
        }
    }

    /**
     * Create a file based on the file directory and file name
     */
    fun createFile(fileDirName: String?, fileName: String): File {
        return File(createDir(fileDirName), fileName)
    }

    /**
     * Create a folder that doesn't exist
     */
    fun createDir(fileDirName: String?, type: String? = null): File? {
        val fileDirPath = Utils.getApp().getExternalFilesDir(type)?.absolutePath
            ?: Utils.getApp().cacheDir.absolutePath
        var dir = "$fileDirPath/"
        val folder = fileDirName ?: ""
        if (folder.isNotBlank()) {
            dir += "$folder/"
        }
        return createDir(File(dir))
    }

    /**
     * Create a folder that doesn't exist
     */
    fun createDir(file: File?): File? {
        return if (FileUtils.createOrExistsDir(file)) file else null
    }

    /**
     * Return to cache folder
     * After the app is uninstalled, the data in the App-specific directory will be deleted.
     * Under the premise of Android Q, if you declare in AndroidManifest.xml: android:hasFragileUserData="true",
     * the user can choose whether to keep it.
     */
    fun getCacheFile(): File {
        var cacheFile = Utils.getApp().externalCacheDir
        cacheFile = cacheFile ?: createDir("glide")
        return cacheFile ?: Utils.getApp().cacheDir
    }

    /**
     * Get Glide cache directory file
     */
    fun getGlideCacheFile(): File? {
        return createDir(File(getCacheFile(), "glide"))
    }

    /**
     * Get Glide cache size
     */
    fun getGlideCacheSize(): Long {
        return FileUtils.getLength(getGlideCacheFile())
    }

    fun getFileName(url: String): String {
        val subString = url.split("/")
        if (subString.isNotEmpty()) return subString[subString.size - 1]
        return url
    }

    @SuppressLint("SimpleDateFormat")
    fun initVideoFile(activity: Activity, nameFile: String ?= null): File {
        var dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        if (!dir.exists()) {
            dir = activity.cacheDir
        }
        return File(
            dir,nameFile ?: String.format(
                "wallpaper_maker_%s.mp4",
               SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(System.currentTimeMillis())
            )
        )
    }

    fun getEndPathFile(pathFile: String?): String?{
        val substr = pathFile?.split("/")
        if(substr?.isNotEmpty() == true){
            substr.last().apply {
                return substring(0, this.length - 4)
            }
        }
        return null
    }

    fun shareFile(urlFileShare : String, context: Context) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        val fileShare = File(urlFileShare)
        Log.d("CHECK_FILE", "shareWallpaper: ${fileShare.length()}")
        val fileShareUri: Uri? = try {
            FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", fileShare)
        } catch (e: IllegalArgumentException) {
            Log.d("CHECK_FILE", "shareWallpaper error: ${e.message}")
            null
        }
        sharingIntent.type = "*/*"
        sharingIntent.putExtra(Intent.EXTRA_STREAM, fileShareUri)
        context.startActivity(Intent.createChooser(sharingIntent, "Share wallpaper maker"))
    }

    fun write(body: ResponseBody, outputFile: File): File {
        try {
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                if (outputFile.exists()) {
                    outputFile.delete()
                }
                val fileReader = ByteArray(BUFFER_SIZE)
                inputStream = body.byteStream()
                outputStream = FileOutputStream(outputFile)
                var read: Int
                while (inputStream.read(fileReader).also { read = it } != -1) {
                    outputStream.write(fileReader, 0, read)
                }
                outputStream.flush()
            } catch (e: Exception) {
                delete(outputFile.toPath())
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: OutOfMemoryError) {
            Log.d("SAVE_FILE", "error unzip")
        } catch (e: Exception) {
            Log.d("SAVE_FILE", "e.message")
        }
        return outputFile
    }
}