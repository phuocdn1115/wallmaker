package com.alo.ringo.tracking

abstract class BaseEventDefinition {

    abstract fun getEventNameList() : List<String>

    companion object {
        const val EVENT_NAME = "name"
    }
}