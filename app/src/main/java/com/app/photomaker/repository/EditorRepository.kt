package com.app.photomaker.repository

import com.app.photomaker.PreferencesKey.URL_WALLPAPER_LIVE_IF_PREVIEW
import com.app.photomaker.PreferencesKey.URL_WALLPAPER_LIVE_IF_SET
import com.app.photomaker.PreferencesManager
import com.app.photomaker.RealmManager
import com.app.photomaker.data.realm_model.WallpaperDownloaded
import com.app.photomaker.download.DownloadWallpaperManager
import io.realm.Realm
import javax.inject.Inject

class EditorRepository @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val downloadWallpaperManager: DownloadWallpaperManager,
    private val realmManager: RealmManager
) {
    fun saveWallpaperVideoUrl(wallpaperVideoUrl: String?){
        preferencesManager.save(URL_WALLPAPER_LIVE_IF_PREVIEW,wallpaperVideoUrl)
    }

    fun saveURLWallpaperLiveSet(urlWallpaperLiveSet: String) = preferencesManager.save(
        URL_WALLPAPER_LIVE_IF_SET, urlWallpaperLiveSet)

    fun renameImageVideoUserCreated(wallpaperDownloaded: WallpaperDownloaded?){
        Realm.getDefaultInstance().beginTransaction()
        Realm.getDefaultInstance().commitTransaction()
        realmManager.save(wallpaperDownloaded)
    }
}