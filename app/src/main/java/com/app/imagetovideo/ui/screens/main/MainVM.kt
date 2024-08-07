package com.app.imagetovideo.ui.screens.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.imagetovideo.base.SingleLiveEvent
import com.app.imagetovideo.data.response.DataHomeResponse
import com.app.imagetovideo.repository.AdvertiseRepository
import com.app.imagetovideo.repository.MainRepository
import com.app.imagetovideo.repository.SystemRepository
import com.app.imagetovideo.base.Result
import com.app.imagetovideo.model.Data
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    private val mainRepository: MainRepository,
    private val advertiseRepository: AdvertiseRepository,
    private val systemRepository: SystemRepository
    ) : ViewModel() {

    fun loadAds() = advertiseRepository.loadAds()
    val dataHomeResult = SingleLiveEvent<Result<DataHomeResponse>>()
    private fun getDataHomeScreen(ownerItemCount : Int ?= 0) {
        /**
         * Get data offline mode
         */
        dataHomeResult.postValue(Result.InProgress())
        val responseOfflineData: DataHomeResponse? = mainRepository.getHomeDataInLocal(ownerItemCount)
        responseOfflineData?.let {
            dataHomeResult.postValue(Result.Success(it))
        }
    }

    private val listVideoUser = MutableLiveData<List<Data>>()
    val listVideoUserResult : LiveData<List<Data>> = listVideoUser
    fun getListVideoUser() {
        val listVideoUserData = mainRepository.getListVideoUser()
        listVideoUser.postValue(listVideoUserData)
        getDataHomeScreen(ownerItemCount = listVideoUserData.size)
    }

    fun checkIsFirstOpenApp(): Boolean = systemRepository.checkIsFirstOpenApp()
    fun saveFirstOpenApp() = systemRepository.saveFirstOpenApp()
    fun deleteVideoUser(data : Data) = systemRepository.deleteVideoUser(data)
}