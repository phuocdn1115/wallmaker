package com.app.imagetovideo.ui.screens.set_home

import androidx.lifecycle.ViewModel
import com.app.imagetovideo.data.realm_model.WallpaperDownloaded
import com.app.imagetovideo.repository.EditorRepository
import com.app.imagetovideo.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SetWallpaperActivityVM @Inject constructor(
    private val mainRepository: MainRepository,
    private val editorRepository: EditorRepository
) : ViewModel() {

    fun saveWallpaperVideoUrl(wallpaperVideoUrl : String?){
        mainRepository.saveWallpaperVideoUrl(wallpaperVideoUrl)
    }

    fun saveURLWallpaperLiveSet(urlWallpaperLiveSet: String) = mainRepository.saveURLWallpaperLiveSet(urlWallpaperLiveSet)

    fun renameImageVideoUserCreated(wallpaperDownloaded: WallpaperDownloaded?) = editorRepository.renameImageVideoUserCreated(wallpaperDownloaded)
}