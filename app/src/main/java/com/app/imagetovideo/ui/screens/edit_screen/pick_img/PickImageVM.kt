package com.app.imagetovideo.ui.screens.edit_screen.pick_img

import android.annotation.SuppressLint
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PickImageVM @Inject constructor(@ApplicationContext val context: Context) : ViewModel() {

    private val currentImageFromDevice = MutableLiveData<List<String>>()
    val myImageFromDevice: LiveData<List<String>> = currentImageFromDevice
    @SuppressLint("Recycle")
    fun getImageFromDevice(){
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        val orderBy = MediaStore.Video.Media.DATE_TAKEN
        val cursor = context.contentResolver.query(uri, projection,null,null, "$orderBy DESC")
        val columnIndexData = cursor?.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)

        CoroutineScope(Dispatchers.IO).launch {
            val urlImages = ArrayList<String>()
            while(cursor?.moveToNext() == true ){
                if (columnIndexData != null) {
                    Log.d("COLUMN_INDEX", "Index: $columnIndexData")
                    val absolutePathOfImage = cursor.getString(columnIndexData)
                    if (absolutePathOfImage != null){
                        urlImages.add(absolutePathOfImage)
                    }
                }
            }
            withContext(Dispatchers.Main){
                currentImageFromDevice.postValue(urlImages)
            }
        }
    }
}