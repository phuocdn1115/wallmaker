package com.app.imagetovideo.ui.screens.preview_home

import androidx.lifecycle.ViewModel
import com.app.imagetovideo.aplication.ApplicationContext
import com.app.imagetovideo.base.SingleLiveEvent
import com.app.imagetovideo.data.model.Wallpaper
import com.app.imagetovideo.repository.MainRepository
import com.app.imagetovideo.base.Result
import com.app.imagetovideo.data.model.Template
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PreviewActivityVM @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    val downloadWallpaperVideoResult = SingleLiveEvent<Result<String>>()
    fun downloadVideo(dataModel: Wallpaper) {
//        val request = mainRepository.downloadVideo(dataModel)
//        downloadWallpaperVideoResult.addSource(request) {
//            downloadWallpaperVideoResult.postValue(it)
//        }
    }

    val downloadThumbTemplateResult = SingleLiveEvent<Result<String>>()
    fun downloadThumbTemplateReview(template: Template){

    }
}