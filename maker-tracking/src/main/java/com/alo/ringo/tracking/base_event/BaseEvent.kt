package com.alo.ringo.tracking.base_event

import android.os.Bundle
import android.util.Log

import com.alo.ringo.tracking.annotation.Key
import com.alo.ringo.tracking.annotation.Mandatory
import com.alo.ringo.tracking.utils.TrackingReflection
import com.alo.ringo.tracking.validate.BaseValidator
import java.lang.reflect.Field
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.isAccessible

/**
 * Define Base Event
 */
abstract class BaseEvent {
    @Key("tp_event_name")
    var eventName: String? = null

    @Mandatory
    @Key("tp_mobileId")
    var mobileId: String? = null

    @Mandatory
    @Key("tp_country")
    var country: String? = null

    @Mandatory
    @Key("tp_session_id")
    var sessionId: String? = null

    @Mandatory
    @Key("tp_session_order")
    var sessionOrder: Int? = 0

    @Mandatory
    @Key("tp_utm_medium")
    var utmMedium: String? = null

    @Mandatory
    @Key("tp_utm_source")
    var utmSource: String? = null

    fun validate(): BaseEvent {
        //val fields = TrackingReflection.getFields(this::class.java)
        val fields =  this::class.java.declaredFields
        for (field in fields) {
            if ("Companion".equals(field.name)) continue;

            BaseValidator.FIELD_NAME_VALIDATOR.validate(this, field)
            BaseValidator.FIELD_VALUE_VALIDATOR.validate(this, field)
            BaseValidator.EVENT_NAME_VALIDATOR.validate(this, field)
        }
        return this
    }

    fun toBundle(): Bundle {
        val bundle = Bundle()
        //val fields = TrackingReflection.getFields(this::class.java)
        if (this::class.java.superclass != null) {
            val fields =this::class.java.superclass.declaredFields
            addBundle(bundle, fields)
        }

        val fields =  this::class.java.declaredFields
        addBundle(bundle, fields)

        return bundle
    }

    private fun addBundle(bundle: Bundle, fields: Array<Field>) {
        for (field in fields) {
            Log.i("BaseEvent", "${field.name}")
            if ("Companion".equals(field.name)) continue;

            val fieldName = getName(field)
            val fieldValue = getValue(field)

            if (fieldValue is Int || fieldValue is Integer) {
                bundle.putInt(fieldName, fieldValue as Int)
            } else {
                if (fieldValue != null)
                    bundle.putString(fieldName, fieldValue as String)
                //bundle.putString(fieldName, fieldValue as String)
            }
        }
    }

    private fun <T : Any> getFieldName(field: KProperty1<T, *>): String {
        val keyAnnotation = TrackingReflection.getKeyAnnotation(field)
        checkNotNull(keyAnnotation) { "Field: ${field.name} with keyAnnotation == null" }
        return if (keyAnnotation != null) keyAnnotation?.key.toString() else field.name
    }

    private fun getName(field: Field) : String {
        val keyAnnotation = field.getAnnotation(Key::class.java)
        checkNotNull(keyAnnotation) { "Field: ${field.name} with keyAnnotation == null" }
        return if (keyAnnotation != null) keyAnnotation?.key.toString() else field.name

//        val keyAnnotations = field.getAnnotation(Key::class.java)
//
//        for(annotation in keyAnnotations) {
//            if(annotation is Key) {
//                val keyAnnotation = annotation as Key
//                checkNotNull(keyAnnotation) { "event:${field.name} keyAnnotation == null" }
//                return if (keyAnnotation != null) keyAnnotation?.key.toString() else field.name
//            }
//
//        }
//
//        return ""
    }

    private fun getValue(field: Field): Any? {
        field.isAccessible = true
        var value = field.get(this)

        if (value?.javaClass != null && value?.javaClass!!.isEnum) {
            val method = value.javaClass.getMethod("getValue")
            value = method.invoke(value)
        }

        return value
    }

    private fun <T : Any> getFieldValue(field: KProperty1<T, *>): Any? {
        field.isAccessible = true
        var value = field.call(this)

        if (value?.javaClass != null && value?.javaClass!!.isEnum) {
            val method = value.javaClass.getMethod("getValue")
            value = method.invoke(value)
        }

        return value
    }
}

abstract class BaseBuilder<T : BaseBuilder<T>> {
    var name: String? = null
    var isConversionEvent = false
    var sessionId: String? = null
    var sessionOrder: Int? = null

    fun name(name: String): T {
        this.name = name
        return this as T
    }

    fun conversionEvent(isConversionEvent: Boolean): T {
        this.isConversionEvent = isConversionEvent
        return this as T
    }

    fun sessionId(sessionId: String): T {
        this.sessionId = sessionId
        return this as T
    }

    fun sessionOrder(sessionOrder: Int): T {
        this.sessionOrder = sessionOrder
        return this as T
    }
}

/**
 * Define AdsType
 */
enum class AdsType(val value: String) {
    EMPTY(""),
    REWARDED("rewarded"),
    INTER("inter"),
    NATIVE("native"),
    BANNER("banner"),
    OPEN("open");

    companion object {
        fun getValue(adsType: AdsType) = adsType.value
    }
}

/**
 * Define AdsReward
 */
enum class AdsRewardType(val value: String) {
    EMPTY(""),
    REQUIRE_ADS("require_ads"),
    EXPLAIN_ADS("explain_ads");

    companion object {
        fun getValue(adsRewardType: AdsRewardType) = adsRewardType.value
    }
}

/**
 * Define Status STATE Download Type
 */
enum class StateDownloadType(val value: String?) {
    EMPTY(""),
    OK("OK"),
    NOK("NOK");

    companion object {
        fun getValue(statusType: StatusType) = statusType.value
    }
}

/**
 * Define Status Type
 */
enum class StatusType(val value: Int?) {
    EMPTY(-1),
    SUCCESS(1),
    FAIL(0);

    companion object {
        fun getValue(statusType: StatusType) = statusType.value
    }
}

/**
 * Define Download State Type
 */
enum class DownloadStateType(val value: String) {
    EMPTY(""),
    LOST_INTERNET("lost_internet"),
    DOWN_FAIL("down_fail"),
    PERMISSION_DENY("permission_deny"),
    REWARD_DEC_LINE("reward_dec_line"),
    REWARD_NO_EARN("reward_no_earn"),
    REWARD_SHOW_FAIL("reward_show_fail"),
    SUCCESS("success");

    companion object {
        fun getValue(downloadStateType: DownloadStateType) = downloadStateType.value
    }
}

