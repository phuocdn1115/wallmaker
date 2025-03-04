package com.app.photomaker.utils.extension

import java.io.File

fun File.lastName() : String {
    return try {
        path.substring(path.lastIndexOf("/") + 1)
    }catch (e: IndexOutOfBoundsException) {
        ""
    }
}