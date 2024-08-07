package com.app.imagetovideo.base

import android.app.Activity
import android.content.Context
import com.app.imagetovideo.data.model.Wallpaper

class FirebaseManager(private val context: Context) {
    private val TAG = FirebaseManager::class.simpleName
    fun createShareLinkRingtone(wallpaper: Wallpaper, onSuccess: (link: String) -> Unit, onFailure: () -> Unit) {
//        Firebase.dynamicLinks.shortLinkAsync {
//            link = Uri.parse("https://www.wallpapermaker.com/?wallpaperId=${wallpaper.id}")
//            domainUriPrefix = DOMAIN_DYNAMIC_LINK
//            androidParameters(context.applicationContext.packageName) {}
//            socialMetaTagParameters { imageUrl = Uri.parse(wallpaper.minThumbURLString()) }
//            iosParameters(context.applicationContext.packageName) {}
//        }.addOnSuccessListener {
//            onSuccess.invoke(it.shortLink.toString())
//            Log.d(
//                TAG,
//                "LINK_PROFILE::createLinkProfile:LINK_SUCCESS " + it.previewLink + " " + it.shortLink
//            )
//
//        }.addOnFailureListener {
//            onFailure.invoke()
//            Log.d(TAG, "LINK_PROFILE::createLinkProfile:LINK_FAIL $it")
//
//        }.addOnCanceledListener {
//            Log.d(TAG, "LINK_PROFILE::createLinkProfile: CANCEL")
//        }
    }

    fun checkDataDynamicLink(
        activityReceiver: Activity,
        callBackGetDynamicLink: (wallpaperId: Long?) -> Unit
    ) {
//        FirebaseDynamicLinks.getInstance()
//            .getDynamicLink(activityReceiver.intent)
//            .addOnSuccessListener(activityReceiver) { pendingDynamicLinkData ->
//                // Get deep link from result (may be null if no link is found)
//                if (pendingDynamicLinkData != null) {
//                    val deepLink = pendingDynamicLinkData.link
//                    if (deepLink?.getQueryParameter("wallpaperId") != null) {
//                        callBackGetDynamicLink.invoke(
//                            deepLink.getQueryParameter("wallpaperId")
//                                ?.toLong()
//                        )
//                    } else {
//                        callBackGetDynamicLink.invoke(null)
//                    }
//                } else {
//                    callBackGetDynamicLink.invoke(null)
//                }
//            }
//            .addOnFailureListener(activityReceiver) {}
    }
}