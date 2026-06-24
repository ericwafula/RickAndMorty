package com.ericwafula.rickandmorty.datasources.remote.helpers

/**
 * Outcome of a remote call: either [Success] carrying decoded [data] of type
 * [D], or [Error] carrying a human-readable failure [message].
 */
sealed interface RemoteResult<out D> {
    data class Success<out D>(val data: D) : RemoteResult<D>
    data class Error(val message: String) : RemoteResult<Nothing>
}

/** Runs [action] with the data when this is a [RemoteResult.Success]. */
inline fun <D> RemoteResult<D>.onSuccess(action: (D) -> Unit): RemoteResult<D> {
    if (this is RemoteResult.Success) action(data)
    return this
}

/** Runs [action] with the error message when this is a [RemoteResult.Error]. */
inline fun <D> RemoteResult<D>.onError(action: (String) -> Unit): RemoteResult<D> {
    if (this is RemoteResult.Error) action(message)
    return this
}

/** Transforms the success data, leaving an [RemoteResult.Error] untouched. */
inline fun <D, R> RemoteResult<D>.map(transform: (D) -> R): RemoteResult<R> =
    when (this) {
        is RemoteResult.Success -> RemoteResult.Success(transform(data))
        is RemoteResult.Error -> this
    }
