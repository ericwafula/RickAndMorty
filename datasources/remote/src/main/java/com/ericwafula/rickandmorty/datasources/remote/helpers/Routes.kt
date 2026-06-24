package com.ericwafula.rickandmorty.datasources.remote.helpers

private const val BASE_URL = "https://rickandmortyapi.com/api/"

/**
 * Rick and Morty API endpoints. Each route carries its [path] (private); data
 * sources use the public [route] — the base URL joined with that path — when
 * issuing a request, so no raw URL string is ever hand-written in a data source.
 */
internal sealed class Routes(private val path: String) {

    val route: String = "$BASE_URL$path"

    data object Characters : Routes(path = "character")

    data class Character(val id: Int) : Routes(path = "character/$id")
}
