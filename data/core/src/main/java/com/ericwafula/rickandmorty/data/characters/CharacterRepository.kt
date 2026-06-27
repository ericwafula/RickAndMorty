package com.ericwafula.rickandmorty.data.characters

/**
 * Parent contract for character reads — the surface domain/presentation depend
 * on. Exposes each single-unit repository as a `val` so callers invoke one
 * straight through (`characterRepository.getCharacter(id)`); the impl is just a
 * holder of those units.
 */
interface CharacterRepository {
    /** One-shot single page of characters (non-paged callers). */
    val getCharacters: GetCharactersRepository

    /** Infinite-scroll stream, optionally filtered by name (the list screen). */
    val getCharactersPaging: GetCharactersPagingRepository

    /** One character by id (the details screen). */
    val getCharacter: GetCharacterRepository
}

internal class CharacterRepositoryImpl(
    override val getCharacters: GetCharactersRepository,
    override val getCharactersPaging: GetCharactersPagingRepository,
    override val getCharacter: GetCharacterRepository,
) : CharacterRepository
