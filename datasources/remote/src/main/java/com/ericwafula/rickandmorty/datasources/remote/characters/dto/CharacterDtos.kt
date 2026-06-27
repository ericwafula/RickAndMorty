package com.ericwafula.rickandmorty.datasources.remote.characters.dto

import kotlinx.serialization.Serializable

/**
 * Raw `/character` list response from the Rick and Morty API: paging [info]
 * plus the page of [results]. DTOs mirror the wire shape exactly and decide
 * nothing — mapping to domain models happens in the data layer.
 */
@Serializable
data class CharactersResponseDto(
    val info: PageInfoDto,
    val results: List<CharacterDto>,
)

/** Paging metadata returned alongside every list endpoint. */
@Serializable
data class PageInfoDto(
    val count: Int,
    val pages: Int,
    val next: String? = null,
    val prev: String? = null,
)

/** A single character as returned by the API. */
@Serializable
data class CharacterDto(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val origin: CharacterLocationDto,
    val location: CharacterLocationDto,
    val image: String,
    val episode: List<String>,
    val url: String,
    val created: String,
)

/** A character's origin/last-known location: a [name] and its resource [url]. */
@Serializable
data class CharacterLocationDto(
    val name: String,
    val url: String,
)
