package com.app.photomaker.ui.screens.preview_home

import androidx.lifecycle.ViewModel
import com.app.photomaker.aplication.ApplicationContext
import com.app.photomaker.base.SingleLiveEvent
import com.app.photomaker.data.model.Wallpaper
import com.app.photomaker.repository.MainRepository
import com.app.photomaker.base.Result
import com.app.photomaker.data.model.Template
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PreviewActivityVM @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    val downloadWallpaperVideoResult = SingleLiveEvent<Result<String>>()
    fun downloadVideo(dataModel: Wallpaper) {
        val request = mainRepository.downloadVideo(dataModel)
        downloadWallpaperVideoResult.addSource(request) {
            downloadWallpaperVideoResult.postValue(it)
        }
    }

    val downloadThumbTemplateResult = SingleLiveEvent<Result<String>>()
    fun downloadThumbTemplateReview(template: Template){
        val request = mainRepository.downloadThumbTemplate("${ApplicationContext.getNetworkContext().videoURL}${template.thumbUrlImageString()}")
        downloadThumbTemplateResult.addSource(request){
            downloadThumbTemplateResult.postValue(it)
        }
    }
}