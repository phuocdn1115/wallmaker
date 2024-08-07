package com.app.imagetovideo.enums

enum class DataType(val type: String) {
    NATIVE_ADS(type = "nativeAds"),
    VIDEO_FROM_SERVER(type = ""),
    VIDEO_MADE_BY_USER(type = "ownerItem"),
    VIDEO_TEMPLATE_TYPE(type = "templateItem")
}