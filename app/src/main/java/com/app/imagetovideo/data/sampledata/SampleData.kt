package com.app.imagetovideo.data.sampledata

import android.content.Context
import android.provider.ContactsContract.Contacts.Photo
import com.app.imagetovideo.R
import com.app.imagetovideo.aplication.ApplicationContext
import com.hw.photomovie.model.PhotoData
import com.hw.photomovie.model.SimplePhotoData

object SampleData {
    fun samplePhotoDataList(context: Context) : ArrayList<PhotoData> {
        val result = ArrayList<PhotoData>()
        result.add(SimplePhotoData(context,"drawable://${R.drawable.template01}", PhotoData.STATE_LOCAL))
        result.add(SimplePhotoData(context,"drawable://${R.drawable.template02}", PhotoData.STATE_LOCAL))
        result.add(SimplePhotoData(context,"drawable://${R.drawable.template03}", PhotoData.STATE_LOCAL))
        result.add(SimplePhotoData(context,"drawable://${R.drawable.template04}", PhotoData.STATE_LOCAL))
        result.add(SimplePhotoData(context,"drawable://${R.drawable.template05}", PhotoData.STATE_LOCAL))
        return result
    }
}