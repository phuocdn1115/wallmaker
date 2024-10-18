package com.app.imagetovideo.ext

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object CoroutineExt {
    fun runOnIO(onRun: suspend () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            onRun()
        }
    }
    fun runOnMainAfterDelay(timeMs: Long = 200, onRun: suspend () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            delay(timeMs)
            CoroutineScope(Dispatchers.Main).launch {
                onRun()
            }
        }
    }

    fun runOnMain(onRun: suspend () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            onRun()
        }
    }

}