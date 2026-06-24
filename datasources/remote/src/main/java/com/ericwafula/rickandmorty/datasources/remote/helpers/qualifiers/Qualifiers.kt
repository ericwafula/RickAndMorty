package com.ericwafula.rickandmorty.datasources.remote.helpers.qualifiers

import org.koin.core.qualifier.named

/**
 * Koin qualifier for the IO coroutine dispatcher.
 *
 * Use it directly when providing and resolving, e.g.
 * `single(IODispatcher) { Dispatchers.IO }` and `get(IODispatcher)`.
 */
internal val IODispatcher = named("IODispatcher")
