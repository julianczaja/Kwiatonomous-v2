package com.corrot.kwiatonomousapp.domain.model

data class AppPreferences(
    val isFirstTimeUser: Boolean = false,
    val isDarkMode: Boolean = false
)