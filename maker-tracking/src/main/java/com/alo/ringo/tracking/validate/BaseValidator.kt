package com.alo.ringo.tracking.validate

import com.alo.ringo.tracking.base_event.BaseEvent
import java.lang.reflect.Field

abstract class BaseValidator {

    /**
     * Process validate
     * Return True/False
     */
    abstract fun validate(event : BaseEvent, field: Field)

    companion object {
        val FIELD_NAME_VALIDATOR : BaseValidator = FieldNameValidator()
        val FIELD_VALUE_VALIDATOR : BaseValidator = FieldValueValidator()
        val EVENT_NAME_VALIDATOR : BaseValidator = EventNameValidator()
    }
}