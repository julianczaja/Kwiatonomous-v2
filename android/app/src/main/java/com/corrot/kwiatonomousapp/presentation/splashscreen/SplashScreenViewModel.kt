package com.corrot.kwiatonomousapp.presentation.splashscreen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corrot.kwiatonomousapp.domain.AuthManager
import com.corrot.kwiatonomousapp.common.Constants.SPLASH_SCREEN_TIME_MILLIS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val authManager: AuthManager
) : ViewModel() {

    val error: MutableState<String?> = mutableStateOf(null)
    val eventFlow = MutableSharedFlow<Event>()

    enum class Event {
        NOT_LOGGED_IN,
        LOGGED_IN,
    }

    fun checkIfLoggedIn() {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            try {
                if (authManager.checkIfLoggedIn()) {
                    waitSplash(startTime)
                    eventFlow.emit(Event.LOGGED_IN)
                } else {
                    waitSplash(startTime)
                    eventFlow.emit(Event.NOT_LOGGED_IN)
                }
            } catch (e: Exception) {
                if (e is HttpException && e.code() == 401) {
                    waitSplash(startTime)
                    eventFlow.emit(Event.NOT_LOGGED_IN)
                } else {
                    error.value = e.message ?: "Unknown error"
                }
            }
        }
    }

    private suspend fun waitSplash(startTime: Long) {
        val diff = SPLASH_SCREEN_TIME_MILLIS - (System.currentTimeMillis() - startTime)
        if (diff > 0) delay(diff)
    }
}
