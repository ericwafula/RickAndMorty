package com.ericwafula.rickandmorty.data.model

/**
 * The data layer's own character type — free of any wire/serialization concerns.
 * Mapped from `CharacterDto` via `toData()`; past that boundary nothing knows
 * about the DTO shape.
 */
data class Character(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val image: String,
    val origin: String,
    val location: String,
)
