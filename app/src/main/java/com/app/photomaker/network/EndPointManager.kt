package com.app.photomaker.network

import com.app.photomaker.aplication.ApplicationContext
import com.app.photomaker.data.model.ApiUrl
import com.app.photomaker.data.model.UrlsRetry
import java.util.*

const val END_POINT_GET_DATA_HOME_SCREEN = "data/templates"
const val END_POINT_CHECKING_INSTALLATION =  "system/install"

@Deprecated("It is deprecated using generateAPIUrl()  , using bestURL solution with NetworkChecker")
fun generateUrlRetry(endPoint: String) : UrlsRetry {
    val urlsRetry: Queue<ApiUrl> = LinkedList()
    urlsRetry.add(ApiUrl(ApplicationContext.getNetworkContext().bestUrl, endPoint))
    return UrlsRetry(urlsRetry)
}

fun generateAPIUrl(endPoint: String) = "${ApplicationContext.getNetworkContext().bestUrl}$endPoint"