package com.app.imagetovideo.repository

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.imagetovideo.PreferencesKey
import com.app.imagetovideo.PreferencesKey.COUNTRY_CODE
import com.app.imagetovideo.PreferencesManager
import com.app.imagetovideo.RealmManager
import com.app.imagetovideo.WallpaperMakerApp
import com.app.imagetovideo.base.Result
import com.app.imagetovideo.data.model.Wallpaper
import com.app.imagetovideo.data.realm_model.WallpaperDownloaded
import com.app.imagetovideo.data.realm_model.WallpaperSuggestionDownloaded
import com.app.imagetovideo.data.response.DataHomeResponse
import com.app.imagetovideo.enums.DataType
import com.app.imagetovideo.enums.WallpaperType
import com.app.imagetovideo.model.Data
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.realm.RealmResults
import io.realm.Sort
import java.io.IOException
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val realmManager: RealmManager,
    private val preferencesManager: PreferencesManager
) {

    fun getHomeDataInLocal(ownerItemCount: Int? = 0): DataHomeResponse {
        val pathGetFileDefault = "json/maker_default_us"
        val pathGetFile = "json/maker_default_" + (preferencesManager.getString(COUNTRY_CODE)?.lowercase() ?: "us") + ".json"
        val jsonSpecialDataString = try {
            WallpaperMakerApp.instance.assets.open(pathGetFile).bufferedReader().use { it.readText() }
        } catch (io: IOException) {
            WallpaperMakerApp.instance.assets.open(pathGetFileDefault).bufferedReader().use { it.readText() }
        }
        Log.i("CHECK_LOG", "jsonSpecialDataString: ${jsonSpecialDataString}")
        val dataResponse = object : TypeToken<DataHomeResponse>() {}.type
        val result = Gson().fromJson<DataHomeResponse?>(jsonSpecialDataString, dataResponse)
        Log.i("CHECK_LOG", "getHomeDataInLocal: ${result}")
        processDataToFillNativeADs(ownerItemCount!!, result)
        return result
    }

    fun getListVideoUser() : List<Data> {
        val imageVideoUser = realmManager.findAllSorted(
            WallpaperDownloaded::class.java, "createTime", Sort.DESCENDING
        ) as RealmResults<WallpaperDownloaded>
        val listVideoUser: ArrayList<Data> = arrayListOf()
        imageVideoUser.forEach {
            if (it.wallpaperType == WallpaperType.VIDEO_USER_TYPE.value) {
                listVideoUser.add(it.convertToVideoUser())
            } else if (it.wallpaperType == WallpaperType.IMAGE_USER_TYPE.value) {
                listVideoUser.add(it.convertToImageUser())
            }
        }
        return listVideoUser
    }

    private fun processDataToFillNativeADs(ownerItemCount: Int, dataResponse : DataHomeResponse){
        /**
         * Handle fill native ads, video template, video/image made by user
         */
        for(i in 0 until ownerItemCount){
            (dataResponse.data.data as MutableList).add(0, Wallpaper(type = DataType.VIDEO_MADE_BY_USER.type, name = "OwnerItem"))
        }
    }

    fun saveWallpaperVideoUrl(wallpaperVideoUrl: String?){
        preferencesManager.save(PreferencesKey.URL_WALLPAPER_LIVE_IF_PREVIEW,wallpaperVideoUrl)
    }

    fun saveURLWallpaperLiveSet(urlWallpaperLiveSet: String) = preferencesManager.save(
        PreferencesKey.URL_WALLPAPER_LIVE_IF_SET, urlWallpaperLiveSet)

}