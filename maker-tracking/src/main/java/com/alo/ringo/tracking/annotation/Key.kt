package com.alo.ringo.tracking.annotation

@MustBeDocumented
@kotlin.annotation.Target(AnnotationTarget.FIELD)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class Key(val key: String) {
}