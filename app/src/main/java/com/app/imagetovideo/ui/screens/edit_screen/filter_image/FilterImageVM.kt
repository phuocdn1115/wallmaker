package com.app.imagetovideo.ui.screens.edit_screen.filter_image

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.imagetovideo.base.Result
import com.app.imagetovideo.data.model.FilterBrightness
import com.app.imagetovideo.data.model.ImageFilter
import com.app.imagetovideo.model.ImageSelected
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FilterImageVM @Inject constructor(private val repository: FilterImageRepository): ViewModel() {

    private val filterImage = MutableLiveData<Result<List<ImageFilter>>>()
    val filterImageLiveData: LiveData<Result<List<ImageFilter>>> = filterImage

    fun loadImageFilter(imageFilterSelected: ImageSelected?) {
        Log.i("GO_TO_FILTER_MODE", "onMessageEvent: 5 ")

        filterImage.postValue(Result.InProgress())

        CoroutineScope(Dispatchers.IO).launch {
            val result = repository.getImageFilter(imageFilterSelected)
            withContext(Dispatchers.Main){
                Log.i("GO_TO_FILTER_MODE", "onMessageEvent: 6 ")
                filterImage.postValue(Result.Success(result))
            }
        }
    }

}