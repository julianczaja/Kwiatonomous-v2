package com.corrot.kwiatonomousapp.data.local.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey

object PreferencesDataStoreKeys {
    val FIRST_TIME_USER_KEY = booleanPreferencesKey("first_time_user_key")
    val DARK_MODE_KEY = booleanPreferencesKey("dark_mode_key")
}