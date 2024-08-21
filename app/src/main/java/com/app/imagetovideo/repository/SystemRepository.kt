package com.app.imagetovideo.repository

import com.app.imagetovideo.PreferencesKey.IS_FIRST_TIME_OPEN_APP
import com.app.imagetovideo.PreferencesKey.TIME_GENERATE_VIDEO_PREVIEW
import com.app.imagetovideo.PreferencesManager
import com.app.imagetovideo.RealmManager
import com.app.imagetovideo.data.model.ImageMadeByUser
import com.app.imagetovideo.data.model.VideoMadeByUser
import com.app.imagetovideo.data.realm_model.WallpaperDownloaded
import com.app.imagetovideo.model.Data
import javax.inject.Inject

class SystemRepository @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val realmManager: RealmManager
) {

    fun checkIsFirstOpenApp(): Boolean = !preferencesManager.getBoolean(IS_FIRST_TIME_OPEN_APP)

    fun saveFirstOpenApp() {
        preferencesManager.save(IS_FIRST_TIME_OPEN_APP, true)
    }

    fun deleteVideoUser(data: Data) {
        var itemDelete : WallpaperDownloaded?= null
        when(data){
            is VideoMadeByUser ->{
                itemDelete = getVideoOrImageUser(data.id)
            }
            is ImageMadeByUser ->{
                itemDelete = getVideoOrImageUser(data.id)
            }
        }
        if(itemDelete != null){
            realmManager.delete(itemDelete)
        }
    }

    fun getVideoOrImageUser(id: String) : WallpaperDownloaded? = realmManager.findFirst(WallpaperDownloaded::class.java, "id", id)


    fun saveTimeGeneratePreviewVideo(time: Long) = preferencesManager.save(TIME_GENERATE_VIDEO_PREVIEW, time)

    fun getTimeGeneratePreviewVideo(): Long = preferencesManager.getLong(TIME_GENERATE_VIDEO_PREVIEW)
}