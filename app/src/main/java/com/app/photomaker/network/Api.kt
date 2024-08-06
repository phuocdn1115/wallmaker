package com.app.photomaker.network

import androidx.annotation.StringRes
import com.app.photomaker.data.response.DataHomeResponse
import com.app.photomaker.data.response.DetailWallpaperResponse
import com.app.photomaker.data.response.InstallationResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Streaming
import retrofit2.http.Url

interface Api {

    @POST
    suspend fun checkingInstall(
        @Url url: String?,
        @Query("utm_source") utmSource: String?,
        @Query("utm_campaign") utmCampaign: String?,
        @Query("utm_content") utmContent: String?,
        @Query("utm_medium") utmMedium: String?,
        @Query("utm_term") utmTerm: String?,
    ): Response<InstallationResponse?>

    @GET
    suspend fun downloadFile(@Url fileUrl: String) : Response<ResponseBody>
}