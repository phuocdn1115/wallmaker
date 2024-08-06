package com.alo.ringo.tracking.validate

import com.alo.ringo.tracking.BaseEventDefinition
import com.alo.ringo.tracking.EventDefinitionManager
import com.alo.ringo.tracking.base_event.BaseEvent
import java.lang.reflect.Field

class EventNameValidator : BaseValidator() {

    override fun validate(event : BaseEvent, field: Field) {
        if (field.name == BaseEventDefinition.EVENT_NAME) {
            val value = field.get(event)
            val result = EventDefinitionManager.getEventDefinitionList().contains(value)
            require(result) {"event name: $value must be defined in WallEventDefinition object"}
        }
    }
}