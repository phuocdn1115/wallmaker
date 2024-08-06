package com.app.photomaker.config

abstract class RegionConfig(val key : String = "GLOBAL") {
    val API_URL_SUFFIX = "_API_URL"
    val EU_SET = linkedSetOf("AU","FR","IT","NL","RU","GB")
    val ASIA_SET = linkedSetOf("JP","KR","TH","TW","IN","TR","MY")
    val AMERICA_SET = linkedSetOf("US","MX")

    fun apiKey() = key + API_URL_SUFFIX

    abstract fun hasCountry(country : String) : Boolean;

    companion object {
        val AMERICA_CONFIG = object : RegionConfig("US") {
            override fun hasCountry(country: String) = AMERICA_SET.contains(country)
        }
        val VN_CONFIG = object : RegionConfig("VN") {
            override fun hasCountry(country: String) = key == country
        }
        val EU_CONFIG = object : RegionConfig("EU") {
            override fun hasCountry(country: String) = EU_SET.contains(country)
        }
        val GLOBAL_CONFIG = object : RegionConfig("GLOBAL") {
            override fun hasCountry(country: String) = true
        }
        val ASIA_CONFIG = object : RegionConfig("ASIA") {
            override fun hasCountry(country: String) = ASIA_SET.contains(country)
        }
    }
}