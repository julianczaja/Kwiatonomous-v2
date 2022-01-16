package com.corrot.kwiatonomousapp.common

import kotlinx.coroutines.flow.*

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true }
) = flow {
    val data = query().first()
    val flow =
        if (shouldFetch(data)) {
            emit(Result.Loading(data))

            try {
                saveFetchResult(fetch())
                query().map { Result.Success(it) }
            } catch (t: Throwable) {
                query().map { Result.Error(t) }
            }
        } else {
            query().map { Result.Success(it) }
        }

    emitAll(flow)
}