package com.app.photomaker.data.model

import com.hw.photomovie.PhotoMovieFactory

class TemplateVideo(
    var template: PhotoMovieFactory.PhotoMovieType? = null,
    var isSelected: Boolean? = false,
    var image: Int? = null,
    var name: String?= null
) {

}