package com.app.imagetovideo.enums

enum class RequestCode (val requestCode : Int) {
    PERMISSION_READ_EXTERNAL_STORAGE(requestCode = 1115),
    PERMISSION_WRITE_EXTERNAL_STORAGE(requestCode = 1111),
    SET_WALLPAPER_LIVE(requestCode = 7474)
}