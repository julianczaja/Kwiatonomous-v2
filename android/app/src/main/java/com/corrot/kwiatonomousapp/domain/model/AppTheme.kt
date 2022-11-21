package com.corrot.kwiatonomousapp.domain.model

enum class AppTheme {
    AUTO,
    LIGHT,
    DARK
}

fun AppTheme.isDarkMode(isSystemInDarkTheme: Boolean) = when (this) {
    AppTheme.AUTO -> isSystemInDarkTheme
    AppTheme.LIGHT -> false
    AppTheme.DARK -> true
}