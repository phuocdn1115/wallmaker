package com.app.imagetovideo.model

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import com.app.imagetovideo.R
import com.app.imagetovideo.WallpaperMakerApp
import java.io.Serializable

class ImageSelected() :Parcelable, Serializable {
    var id: Int = 0
    var isSelected: Boolean = false
    var uriInput: String? = null
    var uriResultCutImageInCache: String? = null
    var uriResultFilterImageInCache: String? = null
    var bitmap: Bitmap? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        isSelected = parcel.readByte() != 0.toByte()
        uriInput = parcel.readString()
        uriResultCutImageInCache = parcel.readString()
        uriResultFilterImageInCache = parcel.readString()
        bitmap = parcel.readParcelable(Bitmap::class.java.classLoader)
    }

    fun getChildNameCacheFile() : String {
        return "${WallpaperMakerApp.getContext().getString(R.string.file_name)}_selected_at_${System.currentTimeMillis()}.jpg"
    }

    class KeyData {
        companion object {
            const val IMAGE_SELECTED = "IMAGE_SELECTED"
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeByte(if (isSelected) 1 else 0)
        parcel.writeString(uriInput)
        parcel.writeString(uriResultCutImageInCache)
        parcel.writeString(uriResultFilterImageInCache)
        //parcel.writeParcelable(bitmap, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ImageSelected> {
        override fun createFromParcel(parcel: Parcel): ImageSelected {
            return ImageSelected(parcel)
        }

        override fun newArray(size: Int): Array<ImageSelected?> {
            return arrayOfNulls(size)
        }
    }
}