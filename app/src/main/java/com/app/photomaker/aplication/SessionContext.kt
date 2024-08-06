package com.app.photomaker.aplication

import com.alo.ringo.tracking.base_event.BaseEvent

class SessionContext {
    var batchedEventList = mutableListOf<BaseEvent>()

    var isHandleGoToPreview = false
}