package com.ericwafula.rickandmorty.data.characters

/**
 * Parent contract for character reads — the surface domain/presentation depend
 * on. Exposes each single-unit repository as a `val` so callers invoke one
 * straight through (`characterRepository.getCharacters(page)`); the impl is just
 * a holder of those units.
 */
interface CharacterRepository {
    val getCharacters: GetCharactersRepository
}

internal class CharacterRepositoryImpl(
    override val getCharacters: GetCharactersRepository,
) : CharacterRepository
