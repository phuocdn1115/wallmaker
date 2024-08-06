package com.alo.ringo.tracking.validate

import com.alo.ringo.tracking.annotation.Mandatory
import com.alo.ringo.tracking.base_event.BaseEvent
import java.lang.reflect.Field

class FieldValueValidator : BaseValidator() {

    override fun validate(event : BaseEvent, field: Field) {
        val mandatoryAnnotation = field.getAnnotation(Mandatory::class.java)
        field.isAccessible = true
        val value = field.get(event)
        if (value?.javaClass != null && value?.javaClass!!.isEnum) {
            val method = value.javaClass.getMethod("getValue")
            val enumValue = method.invoke(value)
            checkNotNull(enumValue) {"$field.name value is NULL. The value must not be null"}
        } else {
            checkNotNull(value) {"$field.name value is NULL. The value must not be null"}
        }
    }
}