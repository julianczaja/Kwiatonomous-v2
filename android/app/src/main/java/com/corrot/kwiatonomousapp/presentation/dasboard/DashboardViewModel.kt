package com.corrot.kwiatonomousapp.presentation.dasboard

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corrot.kwiatonomousapp.LoginManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    val loginManager: LoginManager
) : ViewModel() {

    val state = mutableStateOf(DashboardState())
    val eventFlow = MutableSharedFlow<Event>()

    enum class Event {
        LOGGED_OUT
    }

    fun logOut() {
        loginManager.logOut()
        viewModelScope.launch {
            eventFlow.emit(Event.LOGGED_OUT)
        }
    }
}
