package com.app.imagetovideo.eventbus

import com.app.imagetovideo.data.realm_model.WallpaperDownloaded

class MessageEvent(var message: String, var objRealm: WallpaperDownloaded? = null) {
    companion object {
        const val SAVED_VIDEO_USER = "SAVED_VIDEO_USER"
        const val RENAME_IMAGE_VIDEO = "RENAME_IMAGE_VIDEO"
        const val FINISH_TEMPLATE_ACTIVITY = "FINISH_TEMPLATE_ACTIVITY"
    }
}