package com.ericwafula.rickandmorty.data.di

import com.ericwafula.rickandmorty.data.characters.CharacterRepository
import com.ericwafula.rickandmorty.data.characters.CharacterRepositoryImpl
import com.ericwafula.rickandmorty.data.characters.GetCharacterRepository
import com.ericwafula.rickandmorty.data.characters.GetCharacterRepositoryImpl
import com.ericwafula.rickandmorty.data.characters.GetCharactersPagingRepository
import com.ericwafula.rickandmorty.data.characters.GetCharactersPagingRepositoryImpl
import com.ericwafula.rickandmorty.data.characters.GetCharactersRepository
import com.ericwafula.rickandmorty.data.characters.GetCharactersRepositoryImpl
import org.koin.dsl.module

/**
 * Provides the data layer's repositories — and nothing else.
 *
 * Kept internal so consumers depend on [dataModule] rather than this directly.
 * Each impl pulls its child units / data source from the graph already exposed
 * by `remoteDatasourceModule`.
 */
internal val repositoryModule = module {
    single<GetCharactersRepository> { GetCharactersRepositoryImpl(remoteDatasource = get()) }
    single<GetCharactersPagingRepository> { GetCharactersPagingRepositoryImpl(remoteDatasource = get()) }
    single<GetCharacterRepository> { GetCharacterRepositoryImpl(remoteDatasource = get()) }
    single<CharacterRepository> {
        CharacterRepositoryImpl(
            getCharacters = get(),
            getCharactersPaging = get(),
            getCharacter = get(),
        )
    }
}
