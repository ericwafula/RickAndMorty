package com.ericwafula.rickandmorty.datasources.remote.di

import com.ericwafula.rickandmorty.datasources.remote.characters.CharacterRemoteDatasource
import com.ericwafula.rickandmorty.datasources.remote.characters.DefaultCharacterRemoteDatasource
import com.ericwafula.rickandmorty.datasources.remote.characters.DefaultGetCharacterRemoteDatasource
import com.ericwafula.rickandmorty.datasources.remote.characters.DefaultGetCharactersRemoteDatasource
import com.ericwafula.rickandmorty.datasources.remote.characters.GetCharacterRemoteDatasource
import com.ericwafula.rickandmorty.datasources.remote.characters.GetCharactersRemoteDatasource
import com.ericwafula.rickandmorty.datasources.remote.helpers.qualifiers.IODispatcher
import org.koin.dsl.module

/**
 * Provides the character remote data sources. Each single unit is bound to its
 * `fun interface` so consumers depend on the contract, not the impl; the Ktor
 * client and IO dispatcher are pulled from [clientModule] and [dispatcherModule].
 * Kept internal so consumers wire only [remoteDatasourceModule].
 */
internal val characterDatasourceModule = module {
    single<GetCharactersRemoteDatasource> {
        DefaultGetCharactersRemoteDatasource(
            httpClient = get(),
            ioDispatcher = get(IODispatcher),
        )
    }
    single<GetCharacterRemoteDatasource> {
        DefaultGetCharacterRemoteDatasource(
            httpClient = get(),
            ioDispatcher = get(IODispatcher),
        )
    }
    single<CharacterRemoteDatasource> {
        DefaultCharacterRemoteDatasource(
            getCharacters = get(),
            getCharacter = get(),
        )
    }
}
