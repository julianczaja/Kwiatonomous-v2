package com.corrot.kwiatonomousapp.presentation.splashscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corrot.kwiatonomousapp.LoginManager
import com.corrot.kwiatonomousapp.common.Constants.SPLASH_SCREEN_TIME_MILLIS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val loginManager: LoginManager
) : ViewModel() {

    val eventFlow = MutableSharedFlow<Event>()

    enum class Event {
        NOT_LOGGED_IN,
        LOGGED_IN,
    }

    fun checkIfLoggedIn() {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            try {
                if (loginManager.checkIfLoggedIn()) {
                    waitSplash(startTime)
                    eventFlow.emit(Event.LOGGED_IN)
                } else {
                    waitSplash(startTime)
                    eventFlow.emit(Event.NOT_LOGGED_IN)
                }
            } catch (e: Exception) {
                waitSplash(startTime)
                eventFlow.emit(Event.NOT_LOGGED_IN)
            }
        }
    }

    private suspend fun waitSplash(startTime: Long) {
        val diff = SPLASH_SCREEN_TIME_MILLIS - (System.currentTimeMillis() - startTime)
        if (diff > 0) delay(diff)
    }
}
