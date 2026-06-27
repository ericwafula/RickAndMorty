package com.ericwafula.rickandmorty.datasources.remote.characters

/**
 * Parent contract for the character remote data source — the surface controllers
 * (repositories) depend on. It exposes each single unit as a `val` so callers
 * invoke one straight through (`characterRemoteDatasource.getCharacters(page)`),
 * while the impl is just a holder of those units.
 */
interface CharacterRemoteDatasource {
    val getCharacters: GetCharactersRemoteDatasource
}

internal class DefaultCharacterRemoteDatasource(
    override val getCharacters: GetCharactersRemoteDatasource,
) : CharacterRemoteDatasource
