package com.app.imagetovideo.video.template

import com.app.imagetovideo.data.model.Template
import com.app.imagetovideo.enums.SegmentType
import com.app.imagetovideo.video.PhotoMovieFactoryUsingTemplate
import com.hw.photomovie.PhotoMovie
import com.hw.photomovie.model.PhotoData
import com.hw.photomovie.model.PhotoSource
import com.hw.photomovie.segment.MovieSegment
import com.app.imagetovideo.video.BaseTemplate

class TemplateDefault(template: Template, private val photoList: MutableList<PhotoData>) : BaseTemplate(template, photoList) {

    override fun process(): PhotoMovie<*>? {
            val segmentList : MutableList<MovieSegment<Nothing>> = arrayListOf()
            val modifyPhotoList = clonePhotoList()

            template.script?.forEach { segmentData ->
                val movieSegment = PhotoMovieFactoryUsingTemplate.generateSegment(segmentData.segmentType, segmentData.duration)
                segmentList.add(movieSegment as MovieSegment<Nothing>)
                if(segmentData.segmentType == SegmentType.SCALE_SEGMENT.type && segmentData.index != 0) {
                    cloneNextPhoto(segmentData, modifyPhotoList)
                }
                else if(segmentData.segmentType == SegmentType.THAW_SEGMENT.type || segmentData.segmentType == SegmentType.GRADIENT_TRANSFER_SEGMENT.type) {
                    cloneCurrentPhoto(segmentData, modifyPhotoList)
                }
            }
            val photoSource = PhotoSource(modifyPhotoList)
            return PhotoMovie(photoSource, segmentList)
    }



}