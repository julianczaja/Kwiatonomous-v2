package com.corrot.kwiatonomousapp.common

object Constants {
    const val DEBUG_MODE = false
    const val BASE_URL = "http://maluch.mikr.us:20188/"
    const val BASE_URL_DEBUG = "http://10.0.2.2:8080/"
    // const val BASE_URL_DEBUG = "http://192.168.1.11:8015/"
    const val NAV_ARG_DEVICE_ID = "deviceId"
    const val PREFERENCES_DATA_STORE_NAME = "preferences_data_store"
    const val API_REALM = "KwiatonomousRealm"

    const val DEVICE_INACTIVE_TIME_SECONDS = 1L * 3600L
    const val SPLASH_SCREEN_TIME_MILLIS = 1000L

    const val LOW_BATTERY_VOLTAGE_THRESHOLD = 3.3f
    const val MAX_TIME_DIFF_HOURS = 12

    const val LOCAL_DATE_TIME_MIN_STRING = "0"

    val REGEX_ALPHANUMERIC_WITH_SPACE = "^[a-zA-Z0-9 ]*\$".toRegex()
    val REGEX_ALPHANUMERIC_WITHOUT_SPACE = "^[a-zA-Z0-9]*\$".toRegex()
}