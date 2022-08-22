package com.corrot.kwiatonomousapp.presentation.register

data class RegisterScreenState(
    val userName: String = "",
    val login: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)