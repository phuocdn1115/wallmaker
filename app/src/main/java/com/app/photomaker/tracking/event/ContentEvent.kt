package com.app.photomaker.tracking.event

import com.alo.ringo.tracking.annotation.Key
import com.alo.ringo.tracking.annotation.Mandatory
import com.alo.ringo.tracking.base_event.*


class ContentEvent private constructor(builder: Builder) : BaseEvent() {

    @Mandatory
    @Key("tp_content_id")
    private val contentId: String?

    @Mandatory
    @Key("tp_content_type")
    private val contentType: String?

    @Mandatory
    @Key("tp_status")
    private val status: String?

    @Mandatory
    @Key("tp_comment")
    private val comment: String?

    init {
        this.eventName = builder.eventName
        this.contentId = builder.contentId
        this.contentType = builder.contentType
        this.status = builder.status
        this.comment = builder.comment
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
        var status: String? = StateDownloadType.EMPTY.value
        var comment: String? = null
        var mobileId: String? = null
        var country: String? = null
        var utmMedium: String? = null
        var utmSource: String? = null

        fun eventName(eventName: String): Builder {
            this.eventName = eventName
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

        fun status(status: String?): Builder {
            this.status = status
            return this
        }

        fun comment(comment: String): Builder {
            this.comment = comment
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

        fun build(): ContentEvent {
            val event = ContentEvent(this)
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