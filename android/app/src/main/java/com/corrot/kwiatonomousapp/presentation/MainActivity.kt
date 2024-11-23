package com.corrot.kwiatonomousapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import com.corrot.kwiatonomousapp.domain.NotificationsManager
import com.corrot.kwiatonomousapp.domain.model.AppTheme
import com.corrot.kwiatonomousapp.domain.model.rememberKwiatonomousAppState
import com.corrot.kwiatonomousapp.domain.repository.AppPreferencesRepository
import com.corrot.kwiatonomousapp.domain.workmanager.KwiatonomousWorkManager
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var appPreferencesRepository: AppPreferencesRepository

    @Inject
    lateinit var notificationsManager: NotificationsManager

    @Inject
    lateinit var kwiatonomousWorkManager: KwiatonomousWorkManager

    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // val locale = Locale("pl")
        // Locale.setDefault(locale)
        // resources.apply {
        //     configuration.setLocale(locale)
        //     updateConfiguration(configuration, displayMetrics)
        //     //  applicationContext.createConfigurationContext(configuration)
        // }

        notificationsManager.init(this)

        CoroutineScope(Dispatchers.Main).launch {
            appPreferencesRepository.getNotificationsSettings().collect { notificationsSettings ->
                kwiatonomousWorkManager.setupWorkManager(notificationsSettings)
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            appPreferencesRepository.getAppTheme().collect { currentAppTheme ->
                setContent {
                    val isDarkTheme = when (currentAppTheme) {
                        AppTheme.AUTO -> isSystemInDarkTheme()
                        AppTheme.LIGHT -> false
                        AppTheme.DARK -> true
                    }

                    KwiatonomousAppTheme(darkTheme = isDarkTheme) {
                        Surface(
                            color = MaterialTheme.colors.background
                        ) {
                            KwiatonomousNavHost(
                                kwiatonomousAppState = rememberKwiatonomousAppState(),
                                startDestination = Screen.Splash.route
                            )
                        }
                    }
                }
            }
        }
    }
}
