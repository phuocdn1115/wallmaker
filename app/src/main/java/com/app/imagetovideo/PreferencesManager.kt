package com.app.imagetovideo

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class PreferencesManager(context: Context) {
    private val defaultValueLong: Long = -1
    private val defaultValueInteger = -1
    private val defaultValueFloat = -1

    private var mPrefs: SharedPreferences

    init {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun save(key: String?, value: Boolean) {
        mPrefs.edit().putBoolean(key, value).apply()
    }

    fun save(key: String?, value: String?) {
        mPrefs.edit().putString(key, value).apply()
    }

    fun save(key: String?, value: Float) {
        mPrefs.edit().putFloat(key, value).apply()
    }

    fun save(key: String?, value: Int) {
        mPrefs.edit().putInt(key, value).apply()
    }

    fun save(key: String?, value: Long) {
        mPrefs.edit().putLong(key, value).apply()
    }

    fun getBoolean(key: String?): Boolean {
        return mPrefs.getBoolean(key, false)
    }

    fun getString(key: String?): String? {
        return mPrefs.getString(key, null)
    }

    fun getLong(key: String?): Long {
        return mPrefs.getLong(
            key, defaultValueLong
        )
    }

    fun getInt(key: String?): Int {
        return mPrefs.getInt(
            key, defaultValueInteger
        )
    }

    fun getFloat(key: String?): Float {
        return mPrefs.getFloat(
            key, defaultValueFloat.toFloat()
        )
    }

    fun remove(key: String?) {
        mPrefs.edit().remove(key).apply()
    }
}