package com.corrot.kwiatonomousapp.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.corrot.kwiatonomousapp.KwiatonomousAppState
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

val channelName = "Low battery notification"
val channelDescriptionText = "Battery channel description" // FIXME
val channelId = "BATTERY_CHANNEL_ID"
val importance = NotificationManager.IMPORTANCE_DEFAULT
const val BATTERY_WORK_MANAGER_TAG = "BATTERY_WORK_MANAGER_TAG"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var appPreferencesRepository: AppPreferencesRepository

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

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(channelId, channelName, importance)
//            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        val workManager = WorkManager.getInstance(application.applicationContext)
//        val constraints = Constraints.Builder()
//            .setRequiredNetworkType(NetworkType.CONNECTED)
//            .setRequiresBatteryNotLow(false)
//            .build()
////
//        val work = PeriodicWorkRequestBuilder<BatteryInfoWorker>(1, TimeUnit.DAYS)
//            .setConstraints(constraints)
//            .addTag(BATTERY_WORK_MANAGER_TAG)
//            .setInitialDelay(5, TimeUnit.SECONDS)
//            .build()
////
//
//        workManager.cancelAllWork()
//        workManager.enqueue(work)




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
}