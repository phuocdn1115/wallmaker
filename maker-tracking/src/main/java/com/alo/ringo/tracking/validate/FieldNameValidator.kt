package com.alo.ringo.tracking.validate

import com.alo.ringo.tracking.annotation.Key
import com.alo.ringo.tracking.base_event.BaseEvent
import java.lang.reflect.Field

class FieldNameValidator : BaseValidator() {

    override fun validate(event : BaseEvent, field: Field) {
        checkNotNull(event) { "event == null" }
        checkNotNull(field) { "field == null" }
        val keyAnnotation = field.getAnnotation(Key::class.java)
        checkNotNull(keyAnnotation) { "Field: ${field.name} with keyAnnotation == null" }
        val fieldName = if (keyAnnotation != null) keyAnnotation?.key.toString() else field.name
        val result = fieldName.indexOf("tp_") >=0
        require(result) {"'$fieldName' wrong name without prefix 'tp_', please add the prefix 'tp_'"}
    }
}