package com.app.imagetovideo.data.model

import java.util.*

data class UrlsRetry(var urlsRetry: Queue<ApiUrl>) {
    fun nextUrl() : String? {
        return urlsRetry.poll()?.fullApiUrl
    }
}

data class ApiUrl(var baseUrl: String, var fullApiUrl: String)