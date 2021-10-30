package com.corrot.kwiatonomousapp.common

sealed class Result<out T> {
    object Loading : Result<Nothing>()
    data class Error(val throwable: Throwable) : Result<Nothing>()
    data class Success<T>(val data: T) : Result<T>()
}