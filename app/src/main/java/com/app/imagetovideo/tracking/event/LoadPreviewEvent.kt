package com.app.imagetovideo.tracking.event

import com.alo.ringo.tracking.annotation.Key
import com.alo.ringo.tracking.annotation.Mandatory
import com.alo.ringo.tracking.base_event.BaseBuilder
import com.alo.ringo.tracking.base_event.BaseEvent
import com.alo.ringo.tracking.base_event.StatusType


class LoadPreviewEvent private constructor(builder: Builder) : BaseEvent() {
    @Mandatory
    @Key("tp_wait_time")
    private val waitTime: Int?

    @Mandatory
    @Key("tp_status")
    private val status: Int?

    init {
        this.eventName = builder.eventName
        this.mobileId = builder.mobileId
        this.country = builder.country
        this.waitTime = builder.waitTime
        this.status = builder.status
        this.sessionId = builder.sessionId
        this.sessionOrder = builder.sessionOrder
        this.utmMedium = builder.utmMedium
        this.utmSource = builder.utmSource
    }

    class Builder : BaseBuilder<Builder>() {
        var eventName: String? = null
        var mobileId: String? = null
        var country: String? = null
        var waitTime: Int? = null
        var status: Int? = StatusType.EMPTY.value
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

        fun waitTime(waitTime: Int): Builder {
            this.waitTime = waitTime
            return this
        }

        fun status(statusType: StatusType): Builder {
            this.status = statusType.value
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

        fun build(): LoadPreviewEvent {
            val event = LoadPreviewEvent(this)
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