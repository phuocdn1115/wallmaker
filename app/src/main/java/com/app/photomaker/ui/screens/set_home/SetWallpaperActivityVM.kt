package com.app.photomaker.ui.screens.set_home

import androidx.lifecycle.ViewModel
import com.app.photomaker.data.realm_model.WallpaperDownloaded
import com.app.photomaker.repository.EditorRepository
import com.app.photomaker.repository.MainRepository
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