package com.corrot.kwiatonomousapp.data.local.datastore

import androidx.datastore.preferences.core.stringPreferencesKey

object NetworkPreferencesDataStoreKeys {
    val LAST_NONCE_KEY = stringPreferencesKey("last_nonce_key")
}