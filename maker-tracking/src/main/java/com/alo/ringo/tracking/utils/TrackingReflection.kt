package com.alo.ringo.tracking.utils

import com.alo.ringo.tracking.annotation.Key
import com.alo.ringo.tracking.annotation.Mandatory
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.KProperty1

object TrackingReflection {

    /**
     * Get field list
     */
    fun <O : Any> getFields(clazz : Class<O>) : Collection<KProperty1<O,*>>{
        val klass = clazz.kotlin
        return klass.memberProperties
    }


    /**
     * Get Key annotation
     */
    fun <O : Any> getKeyAnnotation(field : KProperty1<O, *>) = field.findAnnotation<Key>()

    /**
     * Get Mandatory annotation
     */
    fun <O : Any> getMandatoryAnnotation(field : KProperty1<O, *>) = field.findAnnotation<Mandatory>()

}