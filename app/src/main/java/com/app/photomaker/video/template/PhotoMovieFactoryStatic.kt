package com.app.photomaker.video.template

import com.app.photomaker.data.model.ObjectSegmentData
import com.app.photomaker.enums.SegmentType

class PhotoMovieFactoryStatic {
    companion object{
        val endSegmentDefault = listOf(
            ObjectSegmentData(1500,4, SegmentType.FIT_CENTER_SEGMENT.type),
            ObjectSegmentData(2000,4,SegmentType.MOVE_TRANSITION_HORIZONTAL_SEGMENT.type),
            ObjectSegmentData(0,4,SegmentType.FIT_CENTER_SEGMENT.type)
        )

        val endSegmentWithGradientTransferSegment = listOf(
            ObjectSegmentData(100,4,SegmentType.FIT_CENTER_SCALE_SEGMENT.type),
            ObjectSegmentData(2000,4,SegmentType.MOVE_TRANSITION_HORIZONTAL_SEGMENT.type),
            ObjectSegmentData(0,4,SegmentType.FIT_CENTER_SCALE_SEGMENT.type)
        )

        val endSegmentWithoutAnimation = listOf(
            ObjectSegmentData(0,4,SegmentType.FIT_CENTER_SCALE_SEGMENT.type),
            ObjectSegmentData(0,4,SegmentType.MOVE_TRANSITION_HORIZONTAL_SEGMENT.type),
            ObjectSegmentData(0,4,SegmentType.FIT_CENTER_SCALE_SEGMENT.type)
        )
    }
}