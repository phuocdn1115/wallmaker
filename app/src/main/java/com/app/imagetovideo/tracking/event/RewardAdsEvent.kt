package com.app.imagetovideo.tracking.event

import com.alo.ringo.tracking.annotation.Key
import com.alo.ringo.tracking.annotation.Mandatory
import com.alo.ringo.tracking.base_event.AdsRewardType
import com.alo.ringo.tracking.base_event.BaseBuilder
import com.alo.ringo.tracking.base_event.BaseEvent
import com.alo.ringo.tracking.base_event.StatusType


class RewardAdsEvent private constructor(builder: Builder) : BaseEvent() {

    @Mandatory
    @Key("tp_content_id")
    private val contentId: String?

    @Mandatory
    @Key("tp_content_type")
    private val contentType: String?

    @Mandatory
    @Key("tp_inpopup")
    private val inPopup: String?

    @Mandatory
    @Key("tp_approve")
    private val approve: Int?

    @Mandatory
    @Key("tp_status")
    private val status: Int?

    init {
        this.eventName = builder.eventName
        this.contentId = builder.contentId
        this.contentType = builder.contentType
        this.inPopup = builder.inPopup
        this.approve = builder.approve
        this.status = builder.status
        this.mobileId = builder.mobileId
        this.country = builder.country
        this.sessionId = builder.sessionId
        this.sessionOrder = builder.sessionOrder
        this.utmMedium = builder.utmMedium
        this.utmSource = builder.utmSource
    }

    class Builder : BaseBuilder<Builder>() {
        var eventName: String? = null
        var contentId: String? = null
        var contentType: String? = null
        var inPopup: String? = AdsRewardType.EMPTY.value
        var approve: Int? = StatusType.EMPTY.value
        var status: Int? = StatusType.EMPTY.value
        var mobileId: String? = null
        var country: String? = null
        var utmMedium: String? = null
        var utmSource: String? = null

        fun eventName(name: String): Builder {
            this.eventName = name
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

        fun inPopup(inPopup: String): Builder {
            this.inPopup = inPopup
            return this
        }

        fun approve(approve: Int?): Builder {
            this.approve = approve
            return this
        }

        fun status(status: Int?): Builder {
            this.status = status
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

        fun utmMedium(utmMedium: String): Builder {
            this.utmMedium = utmMedium
            return this
        }

        fun utmSource(utmSource: String): Builder {
            this.utmSource = utmSource
            return this
        }

        fun build(): RewardAdsEvent {
            val event = RewardAdsEvent(this)
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