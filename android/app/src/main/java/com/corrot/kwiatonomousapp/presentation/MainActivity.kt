package com.corrot.kwiatonomousapp.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.datastore.preferences.core.edit
import androidx.navigation.compose.rememberNavController
import com.corrot.kwiatonomousapp.data.remote.DigestAuthInterceptor
import com.corrot.kwiatonomousapp.data.remote.api.CheckAccessResponse
import com.corrot.kwiatonomousapp.data.remote.api.KwiatonomousApi
import com.corrot.kwiatonomousapp.domain.repository.AppPreferencesRepository
import com.corrot.kwiatonomousapp.domain.repository.NetworkPreferencesRepository
import com.corrot.kwiatonomousapp.presentation.app_settings.AppTheme
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var appPreferencesRepository: AppPreferencesRepository

    @Inject
    lateinit var networkPreferencesRepository: NetworkPreferencesRepository

    @Inject
    lateinit var kwiatonomousApi: KwiatonomousApi

    @ExperimentalPagerApi
    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // val locale = Locale("pl")
        // Locale.setDefault(locale)
        // resources.apply {
        //     configuration.setLocale(locale)
        //     updateConfiguration(configuration, displayMetrics)
        //     //  applicationContext.createConfigurationContext(configuration)
        // }

        CoroutineScope(Dispatchers.Main).launch {
            login()

            appPreferencesRepository.getAppTheme().collect { currentAppTheme ->
                setContent {
                    val isDarkTheme = when (currentAppTheme) {
                        AppTheme.AUTO -> isSystemInDarkTheme()
                        AppTheme.LIGHT -> false
                        AppTheme.DARK -> true
                    }

                    KwiatonomousAppTheme(darkTheme = isDarkTheme) {
                        Surface(color = MaterialTheme.colors.background) {
                            val navController = rememberNavController()
                            KwiatonomousNavHost(navController = navController)
                        }
                    }
                }
            }
        }
    }

    // FIXME
    private suspend fun login(login: String = "test", password: String = "test") {
        var response = kwiatonomousApi.checkAccess()

        if (response.code() == 401) {
            val authHeader = response.headers()["WWW-Authenticate"]
            if (authHeader?.startsWith("Digest") == true) {
                val authHeaderObj = parseAuthHeader(authHeader)
                    ?: throw Exception("Error while parsing authentication header")

                networkPreferencesRepository.updateLastNonce(authHeaderObj.nonce)
            }
        }
    }
}

// FIXME
data class AuthHeader(
    val realm: String,
    val nonce: String,
    val algorithm: String
)

// FIXME
private fun parseAuthHeader(header: String): AuthHeader? {
    val parts = header.removePrefix("Digest").split(",")
    val mapped = parts.map {
        val split = it.split("=")
        return@map Pair(split.first().trim(), split.last().removeSurrounding("\""))
    }

    val realm = mapped.find { it.first == "realm" }?.second
    val nonce = mapped.find { it.first == "nonce" }?.second
    val algorithm = mapped.find { it.first == "algorithm" }?.second

    if (realm.isNullOrEmpty() || nonce.isNullOrEmpty() || algorithm.isNullOrEmpty()) {
        return null
    }

    return AuthHeader(realm, nonce, algorithm)
}
