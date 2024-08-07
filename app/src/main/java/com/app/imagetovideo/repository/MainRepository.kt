package com.app.imagetovideo.repository

import android.annotation.SuppressLint
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.imagetovideo.PreferencesKey
import com.app.imagetovideo.PreferencesKey.COUNTRY_CODE
import com.app.imagetovideo.PreferencesManager
import com.app.imagetovideo.RealmManager
import com.app.imagetovideo.WallpaperMakerApp
import com.app.imagetovideo.data.model.Wallpaper
import com.app.imagetovideo.data.realm_model.WallpaperDownloaded
import com.app.imagetovideo.data.realm_model.WallpaperSuggestionDownloaded
import com.app.imagetovideo.data.response.DataHomeResponse
import com.app.imagetovideo.download.DownloadWallpaperManager
import com.app.imagetovideo.enums.DataType
import com.app.imagetovideo.enums.WallpaperType
import com.app.imagetovideo.model.Data
import com.app.imagetovideo.network.Api
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.app.imagetovideo.base.Result
import com.app.imagetovideo.data.response.ErrorResponse
import com.app.imagetovideo.utils.FileUtils
import io.realm.RealmResults
import io.realm.Sort
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val api: Api,
    private val realmManager: RealmManager,
    private val preferencesManager: PreferencesManager,
    private val downloadWallpaperManager: DownloadWallpaperManager
) {

    fun getHomeDataInLocal(ownerItemCount: Int? = 0): DataHomeResponse {
        val pathGetFileDefault = "json/maker_default_us"
        val pathGetFile = "json/maker_default_" + (preferencesManager.getString(COUNTRY_CODE)?.lowercase() ?: "us") + ".json"
        val jsonSpecialDataString = try {
            WallpaperMakerApp.instance.assets.open(pathGetFile).bufferedReader().use { it.readText() }
        } catch (io: IOException) {
            WallpaperMakerApp.instance.assets.open(pathGetFileDefault).bufferedReader().use { it.readText() }
        }
        val dataResponse = object : TypeToken<DataHomeResponse>() {}.type
        val result = Gson().fromJson<DataHomeResponse?>(jsonSpecialDataString, dataResponse)
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
        val nativeADsCount = dataResponse.data.data.size / 4
        for(index in 1..nativeADsCount ){
            /**
             * Position of native ADs
             */
            val indexOfNativeADs = index * 4 +  index - 1
            (dataResponse.data.data as MutableList).add(indexOfNativeADs, Wallpaper(type = DataType.NATIVE_ADS.type, name = "NativeAdsItem"))
        }
    }

    @SuppressLint("Range")
    fun downloadVideo(dataModel: Wallpaper) : LiveData<Result<String>> {
        val videoFileLiveData = MutableLiveData<Result<String>>()
        downloadWallpaperManager.downloadFile(
            url = dataModel.originUrlString(),
            onStart = { videoFileLiveData.postValue(Result.InProgress()) },
            onSuccess = { pathFile ->
                val wallpaperSuggestionDownloaded = WallpaperSuggestionDownloaded()
                wallpaperSuggestionDownloaded.id = dataModel.id.toString()
                wallpaperSuggestionDownloaded.name = dataModel.name.toString()
                wallpaperSuggestionDownloaded.pathInStorage = pathFile
                videoFileLiveData.postValue(Result.Success(pathFile))
            }
        ) { videoFileLiveData.postValue(Result.Failure(400, "Error download")) }
        return videoFileLiveData
    }

    fun downloadThumbTemplate(fileUrl: String): LiveData<Result<String>>{
        val result = MutableLiveData<Result<String>>()
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            result.postValue(Result.Failure(400, throwable.message))
        }
        CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
            result.postValue(Result.InProgress())
            val request = api.downloadFile(fileUrl)
            withContext(Dispatchers.Main){
                if(request.isSuccessful){
                    val path = WallpaperMakerApp.getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    val newFile = File(path, System.currentTimeMillis().toString())
                    request.body()?.let { FileUtils.write(it, newFile) }
                    if(newFile.exists()){
                        result.postValue(Result.Success(newFile.absolutePath))
                    }
                }
                else{
                    val strErr = request.errorBody()?.string()
                    val status = Gson().fromJson(strErr, ErrorResponse::class.java)
                    result.postValue(Result.Failures(status,request.code(), request.message()))
                }
            }
        }
        return result
    }

    fun saveWallpaperVideoUrl(wallpaperVideoUrl: String?){
        preferencesManager.save(PreferencesKey.URL_WALLPAPER_LIVE_IF_PREVIEW,wallpaperVideoUrl)
    }

    fun saveURLWallpaperLiveSet(urlWallpaperLiveSet: String) = preferencesManager.save(
        PreferencesKey.URL_WALLPAPER_LIVE_IF_SET, urlWallpaperLiveSet)

}