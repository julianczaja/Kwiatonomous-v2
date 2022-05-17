package com.corrot.kwiatonomousapp.presentation.login

data class LoginScreenState(
    val login: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)