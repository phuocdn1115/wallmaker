package com.app.imagetovideo.eventbus

import com.app.imagetovideo.data.model.FilterBrightness
import com.app.imagetovideo.data.model.ImageFilter
import com.app.imagetovideo.model.ImageSelected

class HandleImageEvent {
    companion object {
        const val ROTATE_IMAGE_EVENT = "ROTATE_IMAGE_EVENT"
        const val CROP_SAVE_IMAGE_EVENT = "CROP_SAVE_IMAGE_EVENT"
        const val UPDATE_IMAGE_LIST_EVENT = "UPDATE_IMAGE_LIST_EVENT"
        const val UPDATE_FILTER_BRIGHTNESS_EVENT = "UPDATE_FILTER_BRIGHTNESS_EVENT"
        const val SAVE_FILTER_BRIGHTNESS_EVENT = "SAVE_FILTER_BRIGHTNESS_EVENT"
        const val REMOVE_IMAGE_SELECTED_FROM_HANDLE_IMAGE_SCREEN = "REMOVE_IMAGE_SELECTED_FROM_HANDLE_IMAGE_SCREEN"
        const val UPDATE_IMAGE_FILTER_BRIGHTNESS_IN_LIST_IMAGE_SELECTED = "UPDATE_IMAGE_FILTER_BRIGHTNESS_IN_LIST_IMAGE_SELECTED"
        const val RETRY_CROP_IMAGE_TO_PREVIEW_SCREEN = "RETRY_CROP_IMAGE_TO_PREVIEW_SCREEN"
    }
    var message: String
    var imageSelected: ImageSelected? = null
    var filterBrightness: FilterBrightness? = null
    var position: Int? = null
    var isGotoFilterMode: Boolean?= true
    var imageFilter: ImageFilter? = null
    constructor(message: String, imageSelected: ImageSelected?, isGotoFilterMode: Boolean ?= true, position: Int?= null) {
        this.message = message
        this.imageSelected = imageSelected
        this.isGotoFilterMode = isGotoFilterMode
        this.position = position
    }

    constructor(message: String, filterBrightness: FilterBrightness?, imageSelected: ImageSelected?= null) {
        this.message = message
        this.filterBrightness = filterBrightness
        this.imageSelected = imageSelected
    }

    constructor(message: String, imageFilter: ImageFilter?, imageSelected: ImageSelected?= null) {
        this.message = message
        this.imageFilter = imageFilter
        this.imageSelected = imageSelected
    }

    constructor(message: String, position: Int?) {
        this.message = message
        this.position = position
    }

    constructor(message: String) {
        this.message = message
    }
}