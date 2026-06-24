package com.ericwafula.rickandmorty.data.helpers

import com.ericwafula.rickandmorty.datasources.remote.helpers.RemoteResult

/**
 * Outcome of a data-layer operation: either [Success] carrying [data] of type
 * [D], or [Error] carrying a human-readable failure [message].
 *
 * The data layer's own result type so callers (repositories, the app) never
 * depend on a data-source type such as [RemoteResult].
 */
sealed interface DataResult<out D> {
    data class Success<out D>(val data: D) : DataResult<D>
    data class Error(val message: String) : DataResult<Nothing>
}

/** Runs [action] with the data when this is a [DataResult.Success]. */
inline fun <D> DataResult<D>.onSuccess(action: (D) -> Unit): DataResult<D> {
    if (this is DataResult.Success) action(data)
    return this
}

/** Runs [action] with the error message when this is a [DataResult.Error]. */
inline fun <D> DataResult<D>.onError(action: (String) -> Unit): DataResult<D> {
    if (this is DataResult.Error) action(message)
    return this
}

/** Transforms the success data, leaving a [DataResult.Error] untouched. */
inline fun <D, R> DataResult<D>.map(transform: (D) -> R): DataResult<R> =
    when (this) {
        is DataResult.Success -> DataResult.Success(transform(data))
        is DataResult.Error -> this
    }

/**
 * Maps a [RemoteResult] from a data source into the data layer's [DataResult].
 *
 * Pass [transform] to convert the success payload (e.g. a DTO into a domain
 * model); errors carry their [message] across unchanged.
 */
inline fun <D, R> RemoteResult<D>.toDataResult(transform: (D) -> R): DataResult<R> =
    when (this) {
        is RemoteResult.Success -> DataResult.Success(transform(data))
        is RemoteResult.Error -> DataResult.Error(message)
    }
