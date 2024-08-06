package com.alo.ringo.tracking

object EventDefinitionManager {

    private val eventDefinitionList = mutableListOf<BaseEventDefinition>()

    init {
        val eventDefinition: BaseEventDefinition = DefaultEventDefinition()
        register(eventDefinition)
    }

    fun register(eventDefinition: BaseEventDefinition) {
        eventDefinitionList.add(eventDefinition)
    }

    fun getEventDefinitionList(): MutableList<BaseEventDefinition> = eventDefinitionList
}