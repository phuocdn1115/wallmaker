package com.app.imagetovideo.tracking

import android.content.Context
import android.util.Log
import com.alo.ringo.tracking.base_event.AdsRewardType
import com.alo.ringo.tracking.base_event.AdsType
import com.alo.ringo.tracking.base_event.BaseEvent
import com.alo.ringo.tracking.base_event.StatusType
import com.alo.ringo.tracking.base_event.*
import com.app.imagetovideo.aplication.ApplicationContext.sessionContext
import com.app.imagetovideo.tracking.event.*
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject

class EventTrackingManager @Inject constructor(@ApplicationContext context: Context) {
//    private val firebaseAnalytics by lazy {
//        FirebaseAnalytics.getInstance(context)
//    }

    private var mobileId: String = ""
    private var age: String = ""
    private var country: String = ""
    private val sessionId = UUID.randomUUID().toString()
    private var sessionOrder = 0
    private var utmMedium = ""
    private var utmSource = ""
    private val batchSize = 10

    var callApiLogToServer: ((evt: Any) -> Unit?)? = null

    init {
//        FirebaseApp.initializeApp(context)
    }

    fun setInstallationTracking(utmMedium: String, utmSource: String, mobileId: String, country: String) {
        if (utmMedium.isNotEmpty()) this.utmMedium = utmMedium
        if (utmSource.isNotEmpty()) this.utmSource = utmSource
        if (mobileId.isNotEmpty()) this.mobileId = mobileId
        if (country.isNotEmpty()) this.country = country
    }

    fun setInstallationDeviceTracking(typeAge: String) {
        if (typeAge.isNotEmpty()) this.age = typeAge
    }

    private fun sendEvent(event: BaseEvent) {
        Log.d(this.javaClass.simpleName, "sendEvent: ${event.eventName} send:\n ${event.toBundle()}")
        sessionContext.batchedEventList.add(event)
        if (sessionContext.batchedEventList.size >= batchSize) {
            logEventBatch()
        }
    }

    private fun logEventBatch() {
        Log.d("LOG_EVENT_BATCH", "size: ${sessionContext.batchedEventList.size}")
        for (item in sessionContext.batchedEventList) {
            Log.d("LOG_EVENT_BATCH", "nameEvent: ${item.eventName}")
//            firebaseAnalytics.logEvent(
//                item.eventName!!,
//                item.toBundle()
//            )
        }
        sessionContext.batchedEventList.clear()
    }

    fun sendConfigsEvent(
        eventName: String,
        loadConfigDone: StatusType,
        keyRemoteConfig: String,
        waitTime: Int,
        status: String? = StateDownloadType.EMPTY.value,
        mobileId: String,
        country: String
    ) {
        val event: BaseEvent = ConfigsEvent.Builder()
            .eventName(eventName)
            .loadConfigDone(loadConfigDone)
            .keyRemoteConfig(keyRemoteConfig)
            .waitTime(waitTime)
            .status(status)
            .mobileId(mobileId)
            .country(country)
            .sessionId(sessionId)
            .sessionOrder(++sessionOrder)
            .utmMedium(utmMedium)
            .utmSource(utmSource)
            .build()
        sendEvent(event)
    }

    fun sendAdsEvent(
        eventName: String,
        contentId: String = "",
        adsType: String = AdsType.EMPTY.value,
        isLoad: Int? = StatusType.EMPTY.value,
        show: Int? = StatusType.EMPTY.value
    ) {
        val event: BaseEvent = AdsEvent.Builder()
            .eventName(eventName)
            .mobileId(mobileId)
            .country(country)
            .contentId(contentId)
            .contentType("Ads")
            .adsType(adsType)
            .isLoad(isLoad)
            .show(show)
            .sessionId(sessionId)
            .sessionOrder(++sessionOrder)
            .utmMedium(utmMedium)
            .utmSource(utmSource)
            .build()
        sendEvent(event)
    }

    fun sendRewardAdsEvent(
        eventName: String,
        contentId: String = "",
        inPopup: String = AdsRewardType.EMPTY.value,
        approve: Int? = StatusType.EMPTY.value,
        status: Int? = StatusType.EMPTY.value
    ) {
        val event: BaseEvent = RewardAdsEvent.Builder()
            .eventName(eventName)
            .contentId(contentId)
            .contentType("Ads")
            .inPopup(inPopup)
            .approve(approve)
            .status(status)
            .mobileId(mobileId)
            .country(country)
            .sessionId(sessionId)
            .sessionOrder(++sessionOrder)
            .utmMedium(utmMedium)
            .utmSource(utmSource)
            .build()
        sendEvent(event)
    }

    fun sendContentEvent(
        eventName: String,
        contentType: String = "",
        contentId: String = "",
        status: String? = StateDownloadType.EMPTY.value,
        comment: String = ""
    ) {
        val event: BaseEvent = ContentEvent.Builder()
            .eventName(eventName)
            .contentType(contentType)
            .contentId(contentId)
            .status(status)
            .comment(comment)
            .mobileId(mobileId)
            .country(country)
            .sessionId(sessionId)
            .sessionOrder(++sessionOrder)
            .utmMedium(utmMedium)
            .utmSource(utmSource)
            .build()
        sendEvent(event)
    }

    fun sendOtherEvent(
        eventName: String
    ) {
        val event: BaseEvent = OtherEvent.Builder()
            .eventName(eventName)
            .mobileId(mobileId)
            .country(country)
            .sessionId(sessionId)
            .sessionOrder(++sessionOrder)
            .utmMedium(utmMedium)
            .utmSource(utmSource)
            .build()
        sendEvent(event)
    }
}