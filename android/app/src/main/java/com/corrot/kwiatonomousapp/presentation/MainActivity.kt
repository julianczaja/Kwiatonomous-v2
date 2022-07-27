package com.corrot.kwiatonomousapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.rememberNavController
import androidx.work.*
import com.corrot.kwiatonomousapp.DeviceBatteryInfoWorker
import com.corrot.kwiatonomousapp.DeviceBatteryInfoWorker.Companion.DEVICE_BATTERY_WORK_NAME
import com.corrot.kwiatonomousapp.DeviceBatteryInfoWorker.Companion.DEVICE_BATTERY_WORK_TAG
import com.corrot.kwiatonomousapp.KwiatonomousAppState
import com.corrot.kwiatonomousapp.NotificationsManager
import com.corrot.kwiatonomousapp.domain.model.AppTheme
import com.corrot.kwiatonomousapp.domain.repository.AppPreferencesRepository
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var appPreferencesRepository: AppPreferencesRepository

    @Inject
    lateinit var notificationsManager: NotificationsManager

    @ExperimentalPagerApi
    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
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

        notificationsManager.init(this)

        CoroutineScope(Dispatchers.Main).launch {
            appPreferencesRepository.getNotificationsSettings().collect() {
                setupWorkManager(it.notificationsOn)
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
                        Surface(color = MaterialTheme.colors.background) {
                            KwiatonomousNavHost(
                                KwiatonomousAppState(
                                    navController = rememberNavController(),
                                    scaffoldState = rememberScaffoldState(),
                                    snackbarScope = rememberCoroutineScope()
                                ),
                                startDestination = Screen.Splash.route
                            )
                        }
                    }
                }
            }
        }
    }

    private fun setupWorkManager(notificationsOn: Boolean) {
        val workManager = WorkManager.getInstance(applicationContext)
        if (notificationsOn) {
            setupBatteryWork(workManager)
        } else {
            workManager.cancelAllWork()
        }
    }

    private fun setupBatteryWork(workManager: WorkManager) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(false)
            .build()

        val work = PeriodicWorkRequestBuilder<DeviceBatteryInfoWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .addTag(DEVICE_BATTERY_WORK_TAG)
            .setInitialDelay(5, TimeUnit.SECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            DEVICE_BATTERY_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            work
        )
    }
}