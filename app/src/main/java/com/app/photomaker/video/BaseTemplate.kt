package com.app.photomaker.video

import com.app.photomaker.data.model.ObjectSegmentData
import com.app.photomaker.data.model.Template
import com.app.photomaker.enums.SegmentType
import com.app.photomaker.video.template.PhotoMovieFactoryStatic
import com.hw.photomovie.PhotoMovie
import com.hw.photomovie.model.PhotoData

abstract class BaseTemplate(val template: Template, private val photoList: MutableList<PhotoData>) {
    /**
     * Photo offset increase when a photo added into modifyPhotoList => next time add photo to position with offset changed
     */
    var photoOffset = 0
    fun handleEndSegmentObjectData(){
        val lastObjectSegmentData = template.script?.last()
        if(photoList.size < 6){
            (template.script as ArrayList).addAll(PhotoMovieFactoryStatic.endSegmentWithoutAnimation)
        }
        else{
            if(lastObjectSegmentData?.segmentType.equals(SegmentType.GRADIENT_TRANSFER_SEGMENT.type))
                (template.script as ArrayList).addAll(PhotoMovieFactoryStatic.endSegmentWithGradientTransferSegment)
            else
                (template.script as ArrayList).addAll(PhotoMovieFactoryStatic.endSegmentDefault)
        }
    }

    fun clonePhotoList() : MutableList<PhotoData> = photoList.toMutableList()

    /**
     * Handle for THAW_SEGMENT or GRADIENT_TRANSFER_SEGMENT
     */
    protected fun cloneCurrentPhoto(segmentData: ObjectSegmentData, modifyPhotoList: MutableList<PhotoData>) {
        val nextPhotoIndex = segmentData.index
        val photoData = photoList[nextPhotoIndex]
        modifyPhotoList.add(nextPhotoIndex + photoOffset, photoData)
        photoOffset++
    }

    /**
     * Handle case has SCALE_SEGMENT in script
     */
    protected fun cloneNextPhoto(segmentData: ObjectSegmentData, modifyPhotoList: MutableList<PhotoData>) {
        val nextPhotoIndex = segmentData.index + 1
        val photoData = photoList[nextPhotoIndex]
        modifyPhotoList.add(nextPhotoIndex + photoOffset, photoData)
        photoOffset++
    }

    abstract fun process(): PhotoMovie<*>?
}