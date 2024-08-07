package com.app.imagetovideo.tracking.event

import com.alo.ringo.tracking.base_event.*


class OtherEvent private constructor(builder: Builder) : BaseEvent() {

    init {
        this.eventName = builder.eventName
        this.mobileId = builder.mobileId
        this.country = builder.country
        this.sessionId = builder.sessionId
        this.sessionOrder = builder.sessionOrder
        this.utmMedium = builder.utmMedium
        this.utmSource = builder.utmSource
    }

    class Builder : BaseBuilder<Builder>() {
        var eventName: String? = null
        var mobileId: String? = null
        var country: String? = null
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

        fun utmMedium(utmMedium: String): Builder {
            this.utmMedium = utmMedium
            return this
        }

        fun utmSource(utmSource: String): Builder {
            this.utmSource = utmSource
            return this
        }

        fun build(): OtherEvent {
            val event = OtherEvent(this)
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