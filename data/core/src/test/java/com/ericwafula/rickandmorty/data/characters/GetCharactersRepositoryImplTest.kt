package com.ericwafula.rickandmorty.data.characters

import com.ericwafula.rickandmorty.data.helpers.DataResult
import com.ericwafula.rickandmorty.datasources.remote.characters.CharacterRemoteDatasource
import com.ericwafula.rickandmorty.datasources.remote.characters.GetCharacterRemoteDatasource
import com.ericwafula.rickandmorty.datasources.remote.characters.GetCharactersRemoteDatasource
import com.ericwafula.rickandmorty.datasources.remote.characters.dto.CharacterDto
import com.ericwafula.rickandmorty.datasources.remote.characters.dto.CharacterLocationDto
import com.ericwafula.rickandmorty.datasources.remote.characters.dto.CharactersResponseDto
import com.ericwafula.rickandmorty.datasources.remote.characters.dto.PageInfoDto
import com.ericwafula.rickandmorty.datasources.remote.helpers.RemoteResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetCharactersRepositoryImplTest {

    @Test
    fun `maps a Success response into a DataResult Success of mapped models`() = runTest {
        val datasource = FakeCharacterRemoteDatasource(
            getCharacters = GetCharactersRemoteDatasource { _, _ -> RemoteResult.Success(responseOf(rickDto)) },
        )
        val getCharacters = GetCharactersRepositoryImpl(datasource)

        val result = getCharacters(page = null)

        assertTrue(result is DataResult.Success)
        val characters = (result as DataResult.Success).data
        assertEquals(1, characters.size)
        val rick = characters.first()
        assertEquals(1, rick.id)
        assertEquals("Rick Sanchez", rick.name)
        assertEquals("Alive", rick.status)
        assertEquals("Human", rick.species)
        // Nested origin/location DTOs are flattened to their names.
        assertEquals("Earth (C-137)", rick.origin)
        assertEquals("Citadel of Ricks", rick.location)
    }

    @Test
    fun `maps an empty page into a DataResult Success of no models`() = runTest {
        val datasource = FakeCharacterRemoteDatasource(
            getCharacters = GetCharactersRemoteDatasource { _, _ -> RemoteResult.Success(responseOf()) },
        )
        val getCharacters = GetCharactersRepositoryImpl(datasource)

        val result = getCharacters(page = null)

        assertTrue(result is DataResult.Success)
        assertEquals(emptyList<Any>(), (result as DataResult.Success).data)
    }

    @Test
    fun `passes a data-source Error through as a DataResult Error with the same message`() = runTest {
        val datasource = FakeCharacterRemoteDatasource(
            getCharacters = GetCharactersRemoteDatasource { _, _ -> RemoteResult.Error("No internet connection.") },
        )
        val getCharacters = GetCharactersRepositoryImpl(datasource)

        val result = getCharacters(page = null)

        assertEquals(DataResult.Error("No internet connection."), result)
    }

    private class FakeCharacterRemoteDatasource(
        override val getCharacters: GetCharactersRemoteDatasource =
            GetCharactersRemoteDatasource { _, _ -> RemoteResult.Error("not stubbed") },
        override val getCharacter: GetCharacterRemoteDatasource =
            GetCharacterRemoteDatasource { RemoteResult.Error("not stubbed") },
    ) : CharacterRemoteDatasource

    private companion object {
        val rickDto = CharacterDto(
            id = 1,
            name = "Rick Sanchez",
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Male",
            origin = CharacterLocationDto(name = "Earth (C-137)", url = "https://rickandmortyapi.com/api/location/1"),
            location = CharacterLocationDto(name = "Citadel of Ricks", url = "https://rickandmortyapi.com/api/location/3"),
            image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
            episode = listOf("https://rickandmortyapi.com/api/episode/1"),
            url = "https://rickandmortyapi.com/api/character/1",
            created = "2017-11-04T18:48:46.250Z",
        )

        fun responseOf(vararg characters: CharacterDto) = CharactersResponseDto(
            info = PageInfoDto(count = characters.size, pages = 1, next = null, prev = null),
            results = characters.toList(),
        )
    }
}
