package com.app.photomaker.video.template

import com.app.photomaker.data.model.Template
import com.app.photomaker.enums.SegmentType
import com.app.photomaker.video.BaseTemplate
import com.app.photomaker.video.PhotoMovieFactoryUsingTemplate
import com.hw.photomovie.PhotoMovie
import com.hw.photomovie.model.PhotoData
import com.hw.photomovie.model.PhotoSource
import com.hw.photomovie.segment.FitCenterSegment
import com.hw.photomovie.segment.MovieSegment

class TemplateFour (template: Template, private val photoList: MutableList<PhotoData>) : BaseTemplate(template, photoList) {
    override fun process(): PhotoMovie<*>? {
        val segmentList : MutableList<MovieSegment<Nothing>> = arrayListOf()
        val modifyPhotoList = clonePhotoList()
        template.script?.forEach { segmentData ->
            val movieSegment = PhotoMovieFactoryUsingTemplate.generateSegment(segmentData.segmentType, segmentData.duration)
            segmentList.add(movieSegment as MovieSegment<Nothing>)
            /**
             * Start with SCALE_SEGMENT
             */
            if(segmentData.segmentType == SegmentType.SCALE_SEGMENT.type && segmentData.index == 0){
                //Step 1 add photo
                //[1,2,3,4,5,6]  => [1,2,2,3,4,5,6]
                cloneNextPhoto(segmentData, modifyPhotoList)
                //Step 2 add segment
                //[0,1,2,3] => [fit,0,1,2,3]
                val fitCenterSegment = FitCenterSegment(1500)
                //add fitCenterSegment at the beginning of segment list
                segmentList.add(0, fitCenterSegment as MovieSegment<Nothing>)
                //clone photo at 3 position
                //Step 3: add photo
                //[1,2,2,3,4,5,6] => [1,2,2,3,3,4,5,6]
                modifyPhotoList.add(segmentData.index + 3, photoList[segmentData.index + 2])
            }
        }
        val photoSource = PhotoSource(modifyPhotoList)
        return PhotoMovie(photoSource, segmentList)
    }

}