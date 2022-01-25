package com.corrot.kwiatonomousapp.common

import kotlinx.coroutines.flow.*


inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline onFetchFailed: (Throwable) -> Unit = { },
    crossinline shouldFetch: (ResultType) -> Boolean = { true }
) = flow {

    val data = query().firstOrNull()

    val flow = if (data == null || shouldFetch(data)) {
        emit(Result.Loading(data))
        try {
            saveFetchResult(fetch())
            query().map { Result.Success(it) }
        } catch (t: Throwable) {
            onFetchFailed(t)
            query().map { Result.Error(t) }
        }
    } else {
        query().map { Result.Success(it) }
    }

    emitAll(flow)

}.catch { t ->
    emit(Result.Error(t))
}
