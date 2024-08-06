package com.app.photomaker.tracking.event

import com.alo.ringo.tracking.annotation.Key
import com.alo.ringo.tracking.annotation.Mandatory
import com.alo.ringo.tracking.base_event.AdsType
import com.alo.ringo.tracking.base_event.BaseBuilder
import com.alo.ringo.tracking.base_event.BaseEvent
import com.alo.ringo.tracking.base_event.StatusType


class AdsEvent private constructor(builder: Builder) : BaseEvent() {

    @Mandatory
    @Key("tp_content_id")
    private val contentId: String?

    @Mandatory
    @Key("tp_content_type")
    private val contentType: String?

    @Mandatory
    @Key("tp_ads_type")
    private val adsType: String?

    @Mandatory
    @Key("tp_isload")
    private val isLoad: Int?

    @Mandatory
    @Key("tp_show")
    private val show: Int?

    init {
        this.eventName = builder.eventName
        this.mobileId = builder.mobileId
        this.country = builder.country
        this.contentId = builder.contentId
        this.contentType = builder.contentType
        this.adsType = builder.adsType
        this.isLoad = builder.isLoad
        this.show = builder.show
        this.sessionId = builder.sessionId
        this.sessionOrder = builder.sessionOrder
        this.utmMedium = builder.utmMedium
        this.utmSource = builder.utmSource
    }

    class Builder : BaseBuilder<Builder>() {
        var eventName: String? = null
        var mobileId: String? = null
        var country: String? = null
        var contentId: String? = null
        var contentType: String? = null
        var adsType: String? = AdsType.EMPTY.value
        var isLoad: Int? = StatusType.EMPTY.value
        var show: Int? = StatusType.EMPTY.value
        var utmMedium: String? = null
        var utmSource: String? = null

        fun eventName(name: String): Builder {
            this.eventName = name
            return this
        }

        fun mobileId(mobileId: String): Builder {
            this.mobileId = mobileId
            return this
        }

        fun country(country: String): Builder {
            this.country = country
            return this
        }

        fun contentId(contentId: String): Builder {
            this.contentId = contentId
            return this
        }

        fun contentType(contentType: String): Builder {
            this.contentType = contentType
            return this
        }

        fun adsType(adsType: String): Builder {
            this.adsType = adsType
            return this
        }

        fun isLoad(isLoad: Int?): Builder {
            this.isLoad = isLoad
            return this
        }

        fun show(show: Int?): Builder {
            this.show = show
            return this
        }

        fun utmMedium(utmMedium: String): Builder {
            this.utmMedium = utmMedium
            return this
        }

        fun utmSource(utmSource: String): Builder {
            this.utmSource = utmSource
            return this
        }

        fun build(): AdsEvent {
            val event = AdsEvent(this)
            event.validate()
            return event
        }
    }

    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }
}