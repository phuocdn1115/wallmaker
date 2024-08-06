package com.app.photomaker.video.template

import com.app.photomaker.data.model.Template
import com.app.photomaker.enums.SegmentType
import com.app.photomaker.video.BaseTemplate
import com.app.photomaker.video.PhotoMovieFactoryUsingTemplate
import com.hw.photomovie.PhotoMovie
import com.hw.photomovie.model.PhotoData
import com.hw.photomovie.model.PhotoSource
import com.hw.photomovie.segment.MovieSegment

class TemplateTen(template: Template, private val photoList: MutableList<PhotoData>) :
    BaseTemplate(template, photoList) {
    override fun process(): PhotoMovie<*>? {
        val segmentList: MutableList<MovieSegment<Nothing>> = arrayListOf()

        var valuePositionIncreaseChanged = 0

        template.script?.forEach { segmentData ->
            val movieSegment = PhotoMovieFactoryUsingTemplate.generateSegment(
                segmentData.segmentType,
                segmentData.duration
            )
            segmentList.add(movieSegment as MovieSegment<Nothing>)

            if (segmentData.segmentType == SegmentType.SCALE_SEGMENT.type && segmentData.index != 0) {
                val positionSegmentDataPreparedAdd = segmentData.index + 1 + valuePositionIncreaseChanged
                val photoData = photoList[positionSegmentDataPreparedAdd]
                photoList.add(positionSegmentDataPreparedAdd, photoData)
                valuePositionIncreaseChanged++
            } else if (segmentData.segmentType == SegmentType.THAW_SEGMENT.type) {
                val positionSegmentDataPreparedAdd = segmentData.index + valuePositionIncreaseChanged
                val photoData = photoList[positionSegmentDataPreparedAdd]
                photoList.add(positionSegmentDataPreparedAdd, photoData)
                valuePositionIncreaseChanged++
            }
        }
        val photoSource = PhotoSource(photoList)
        return PhotoMovie(photoSource, segmentList)
    }

}