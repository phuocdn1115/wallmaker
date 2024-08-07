package com.app.imagetovideo.network

import android.util.Log
import com.app.imagetovideo.BuildConfig
import com.app.imagetovideo.aplication.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.Duration
import java.time.Instant
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLPeerUnverifiedException


object NetworkChecker {

    suspend fun check() {
        var bestUrl : String = BuildConfig.GLOBAL_API_URL
        var lastResponseTime : Long = 10_000

        val copyOfApiList: MutableList<String> = ApplicationContext.getNetworkContext().apiList.toMutableList()
        val client = OkHttpClient.Builder().callTimeout(5000, TimeUnit.MILLISECONDS).addGoogleDns().build()

        for (url in copyOfApiList) {

            val request = Request.Builder()
                .head()
                .url(url + "system/heartbeat")
                .build()

            Log.i("NetworkChecker", "Response body: ${url + "system/heartbeat"}")
            try {

                val start = Instant.now()
                val response = client.newCall(request).execute()
                val end = Instant.now()
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    Log.i("NetworkChecker", "Response body: $body")
                    val duration = Duration.between(start, end)
                    if(duration.toMillis() < lastResponseTime) {
                        bestUrl = url
                        lastResponseTime = duration.toMillis()
                    }

                    //
                    Log.i("NetworkChecker", "Request took ${duration.toMillis()} ms")
                } else {
                    println("Request failed")
                    Log.i("NetworkChecker", "Request failed")
                }

            } catch (e: SSLPeerUnverifiedException) {
                Log.i("NetworkChecker","SSL peer is not verified: ${e.message}")
            } catch (e: Exception) {
                // Handle other exceptions here
                Log.i("NetworkChecker","Exception occurred: ${e.message}")
            }
        }

        ApplicationContext.getNetworkContext().apply {
            assignBestUrl(bestUrl)
            Log.i("NetworkChecker", "Best URL = ${this.bestUrl}")
        }


        Log.i("NetworkChecker", "===============================================================================")
    }

}