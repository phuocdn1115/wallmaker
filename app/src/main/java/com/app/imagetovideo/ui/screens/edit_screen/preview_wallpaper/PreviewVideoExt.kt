package com.app.imagetovideo.ui.screens.edit_screen.preview_wallpaper

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.app.imagetovideo.data.model.Template
import com.app.imagetovideo.data.realm_model.WallpaperDownloaded
import com.app.imagetovideo.enums.WallpaperType
import com.app.imagetovideo.eventbus.MessageEvent
import com.app.imagetovideo.ui.dialog.DialogCancelPreview
import com.app.imagetovideo.utils.FileUtils
import com.app.imagetovideo.video.PhotoMovieFactoryUsingTemplate
import com.hw.photomovie.PhotoMovieFactory
import com.hw.photomovie.record.GLMovieRecorder
import com.hw.photomovie.record.GLMovieRecorder.OnRecordListener
import com.hw.photomovie.render.GLSurfaceMovieRenderer
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat

fun PreviewVideoFragment.showAdsRequestBottomSheet() {
    askViewAdsBottomSheet?.show(childFragmentManager, "requestViewAdsBottomSheet")
}

fun PreviewVideoFragment.showAdsRequestAgainBottomSheet() {
    askViewAdsAgainBottomSheet?.show(childFragmentManager, "requestViewAdsAgainBottomSheet")
}

fun PreviewVideoFragment.showRewardedAds() {
    rewardedAdsManager.show(requireActivity()) {
        if (wallpaperType == WallpaperType.VIDEO_USER_TYPE) {
            saveVideo(onSaveVideoProgressListener)
            progressSavingVideoFragment?.show(childFragmentManager, ProgressSavingVideoFragment::class.java.name)
        }
        else saveImage(onSaveImageProgressListener)
    }
}

fun PreviewVideoFragment.saveVideo(onSaveVideoProgressListener: SaveVideoProgressListener) {
    photoMoviePlayer?.pause()
    val startRecodTime = System.currentTimeMillis()
    val recorder = GLMovieRecorder(activity)
    val file = FileUtils.initVideoFile(requireActivity())
    val bitrate = if (binding.glTexture.width * binding.glTexture.height > 1000 * 1500) 8000000 else 4000000
    recorder.configOutput(
        binding.glTexture.width,
        binding.glTexture.height,
        bitrate,
        30,
        1,
        file.absolutePath
    )
    Log.d("getAbsolutePath", file.absolutePath)
    val newPhotoMovie = if (template == null) PhotoMovieFactory.generatePhotoMovie(
        photoMovie?.photoSource,
        templateVideo?.template
    )
    else PhotoMovieFactoryUsingTemplate.generatePhotoMovies(template, photoDataList)
    val newMovieRenderer = GLSurfaceMovieRenderer(movieRender)
    newMovieRenderer.photoMovie = newPhotoMovie
    Log.d("CHECK_PHOTO_MOVIE", "${newPhotoMovie == photoMovie}")
    recorder.setDataSource(newMovieRenderer)
    recorder.startRecord(object : OnRecordListener {
        override fun onRecordFinish(success: Boolean) {
            val recordEndTime = System.currentTimeMillis()
            Log.i("Record", "record:" + (recordEndTime - startRecodTime))
            if (success) {
                onSaveVideoProgressListener.onSuccess(
                    saveObjVideo(file, template)
                )
            } else {
                onSaveVideoProgressListener.onFailure("Error!!!")
            }
        }

        override fun onRecordProgress(recordedDuration: Int, totalDuration: Int) {
            previewVM.sendProgressSavingVideo(recordedDuration, totalDuration)
        }
    })
}

@SuppressLint("SimpleDateFormat")
fun PreviewVideoFragment.saveImage(onSaveImageProgressListener: SaveImageProgressListener) {
    val saved: Boolean
    val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, Uri.parse(uriImagePrepareToSaveIfWallpaperIsImage))
    val name = String.format(
        "wall_style_%s",
        SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(System.currentTimeMillis())
    )
    val fos: OutputStream?
    onSaveImageProgressListener.onProgress()
    var filePath: String? = null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val resolver: ContentResolver = requireContext().contentResolver
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        val imageUri: Uri? =
            resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        fos = imageUri?.let { resolver.openOutputStream(it) }

        val cursor = imageUri?.let { context?.contentResolver?.query(it, arrayOf(MediaStore.Images.Media.DATA), null, null, null) }
        if(cursor != null){
            if(cursor.moveToFirst()){
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                filePath = cursor.getString(columnIndex)
                val wallpaperDownloaded = WallpaperDownloaded()
                wallpaperDownloaded.id = System.currentTimeMillis().toString()
                wallpaperDownloaded.createTime = System.currentTimeMillis()
                wallpaperDownloaded.name = name
                wallpaperDownloaded.wallpaperType = wallpaperType?.value.toString()
                wallpaperDownloaded.pathInStorage = filePath
                wallpaperDownloaded.imageThumb = filePath
                scannerFileToAddMediaContent(filePath)
                realmManager.save(wallpaperDownloaded)
                onSaveImageProgressListener.onSuccess(wallpaperDownloaded)
            }
        }

    } else {
        val imagesDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        ).toString() + File.separator + name
        val file = File(imagesDir)
        if (!file.exists()) {
            file.mkdir()
        }
        val image = File(imagesDir, "$name.png")
        fos = FileOutputStream(image)

        val wallpaperDownloaded = WallpaperDownloaded()
        wallpaperDownloaded.id = System.currentTimeMillis().toString()
        wallpaperDownloaded.createTime = System.currentTimeMillis()
        wallpaperDownloaded.name = name
        wallpaperDownloaded.wallpaperType = wallpaperType?.value.toString()
        wallpaperDownloaded.pathInStorage = image.absolutePath
        wallpaperDownloaded.imageThumb = uriImagePrepareToSaveIfWallpaperIsImage
        scannerFileToAddMediaContent(image.absolutePath)
        realmManager.save(wallpaperDownloaded)
        onSaveImageProgressListener.onSuccess(wallpaperDownloaded)

    }
    saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos) == true
    if (!saved) onSaveImageProgressListener.onFailure("Save image failure!!")
    Log.d("SAVE_IMAGE", "$saved")
    fos?.flush()
    fos?.close()
}

fun PreviewVideoFragment.saveObjVideo(file : File, template: Template?) : WallpaperDownloaded{
    scannerFileToAddMediaContent(file.absolutePath)
    val wallpaperDownloaded = WallpaperDownloaded()
    wallpaperDownloaded.id = System.currentTimeMillis().toString()
    wallpaperDownloaded.createTime = System.currentTimeMillis()
    wallpaperDownloaded.name = file.name
    wallpaperDownloaded.wallpaperType = wallpaperType?.value.toString()
    wallpaperDownloaded.pathInStorage = file.absolutePath
    wallpaperDownloaded.isTemplate = template != null
    wallpaperDownloaded.idTemplate = template?.id.toString()
    wallpaperDownloaded.imageThumb = dataTemplateVideo?.listImageSelected?.first()?.uriResultCutImageInCache
    Log.d("SAVE_OBJ_REALM", "${wallpaperDownloaded.imageThumb}")
    realmManager.save(wallpaperDownloaded)
    return wallpaperDownloaded
}

fun PreviewVideoFragment.showCancelPreviewDialog(){
    cancelPreviewDialog = DialogCancelPreview(onClickExit = {
        EventBus.getDefault().post(MessageEvent(MessageEvent.FINISH_TEMPLATE_ACTIVITY))
        activity?.finish()
    })
    cancelPreviewDialog?.show(childFragmentManager,DialogCancelPreview::class.java.name)
}

fun PreviewVideoFragment.scannerFileToAddMediaContent(filePath: String) {
    /**
     *  MediaScannerConnection provides a way for applications to pass a newly created or downloaded media file
     *  to the media scanner service. The media scanner service will read metadata from the file and add the file to the media content provider.
     *  The MediaScannerConnectionClient provides an interface for the media scanner service to return the Uri
     *  for a newly scanned file to the client of the MediaScannerConnection class.
     *  ---> show video file in device
     */
    MediaScannerConnection.scanFile(
        requireContext(), arrayOf(filePath), null
    ) { _, _ ->
    }
}