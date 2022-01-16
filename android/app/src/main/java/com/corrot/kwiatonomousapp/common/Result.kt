package com.corrot.kwiatonomousapp.common

sealed class Result<out T> {
    data class Loading<T>(val data: T? = null) : Result<T>()
    data class Error(val throwable: Throwable) : Result<Nothing>()
    data class Success<T>(val data: T) : Result<T>()
}