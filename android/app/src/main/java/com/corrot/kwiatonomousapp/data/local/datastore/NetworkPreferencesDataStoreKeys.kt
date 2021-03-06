package com.corrot.kwiatonomousapp.data.local.datastore

import androidx.datastore.preferences.core.stringPreferencesKey

object NetworkPreferencesDataStoreKeys {
    val LOGIN_KEY = stringPreferencesKey("login_key")
    val HA1_KEY = stringPreferencesKey("ha1_key")
    val LAST_NONCE_KEY = stringPreferencesKey("last_nonce_key")
}