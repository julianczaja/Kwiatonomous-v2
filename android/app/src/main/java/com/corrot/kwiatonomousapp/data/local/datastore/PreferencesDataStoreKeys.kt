package com.corrot.kwiatonomousapp.data.local.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey

object PreferencesDataStoreKeys {
    val FIRST_TIME_USER_KEY = booleanPreferencesKey("first_time_user_key")
    val APP_THEME_KEY = intPreferencesKey("app_theme_key")
}