package com.ericwafula.rickandmorty.datasources.remote.characters

import com.ericwafula.rickandmorty.datasources.remote.helpers.RemoteResult
import com.ericwafula.rickandmorty.datasources.remote.helpers.createHttpClient
import com.ericwafula.rickandmorty.datasources.remote.helpers.mockHttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.channels.UnresolvedAddressException

@OptIn(ExperimentalCoroutinesApi::class)
class GetCharactersRemoteDatasourceTest {

    @Test
    fun `returns Success with the parsed page of characters`() = runTest {
        val datasource = datasourceReturning(CHARACTERS_JSON)

        val result = datasource(page = null, name = null)

        assertTrue(result is RemoteResult.Success)
        val page = (result as RemoteResult.Success).data
        assertEquals(826, page.info.count)
        assertEquals(1, page.results.size)
        assertEquals("Rick Sanchez", page.results.first().name)
        assertEquals("Earth (C-137)", page.results.first().origin.name)
    }

    @Test
    fun `sends page and name as query parameters when searching`() = runTest {
        var requestedUrl: String? = null
        val datasource = DefaultGetCharactersRemoteDatasource(
            httpClient = createHttpClient(
                MockEngine { request ->
                    requestedUrl = request.url.toString()
                    respond(
                        content = CHARACTERS_JSON,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                },
            ),
            ioDispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        datasource(page = 2, name = "rick")

        assertTrue(requestedUrl!!.contains("page=2"))
        assertTrue(requestedUrl!!.contains("name=rick"))
    }

    @Test
    fun `maps 400 Bad Request to a request-failed error carrying the code`() = runTest {
        val result = datasourceReturning("bad request", HttpStatusCode.BadRequest)
            .invoke(page = null, name = null)

        assertEquals(RemoteResult.Error("Request failed (400)."), result)
    }

    @Test
    fun `maps 401 Unauthorized to a request-failed error carrying the code`() = runTest {
        val result = datasourceReturning("nope", HttpStatusCode.Unauthorized)
            .invoke(page = null, name = null)

        assertEquals(RemoteResult.Error("Request failed (401)."), result)
    }

    @Test
    fun `maps 404 (no matches) to an empty success page`() = runTest {
        // The Rick & Morty API returns 404 when a ?name= filter matches nothing;
        // the datasource turns that into an empty page so the UI shows the empty
        // state rather than an error.
        val result = datasourceReturning("""{ "error": "There is nothing here" }""", HttpStatusCode.NotFound)
            .invoke(page = null, name = "zzzznomatch")

        assertTrue(result is RemoteResult.Success)
        assertTrue((result as RemoteResult.Success).data.results.isEmpty())
    }

    @Test
    fun `maps 500 Internal Server Error to a server error carrying the code`() = runTest {
        val result = datasourceReturning("boom", HttpStatusCode.InternalServerError)
            .invoke(page = null, name = null)

        assertEquals(RemoteResult.Error("Server error (500)."), result)
    }

    @Test
    fun `maps 503 Service Unavailable to a server error carrying the code`() = runTest {
        val result = datasourceReturning("down", HttpStatusCode.ServiceUnavailable)
            .invoke(page = null, name = null)

        assertEquals(RemoteResult.Error("Server error (503)."), result)
    }

    @Test
    fun `maps a malformed JSON body to a parse error`() = runTest {
        val result = datasourceReturning("{ this is not valid json", HttpStatusCode.OK)
            .invoke(page = null, name = null)

        assertEquals(RemoteResult.Error("Failed to parse the response."), result)
    }

    @Test
    fun `maps a body missing a required field to a parse error`() = runTest {
        // Well-formed JSON, but "results" (a required field) is absent.
        val result = datasourceReturning("""{ "info": { "count": 0, "pages": 0 } }""", HttpStatusCode.OK)
            .invoke(page = null, name = null)

        assertEquals(RemoteResult.Error("Failed to parse the response."), result)
    }

    @Test
    fun `maps an unresolved host to a no-internet error`() = runTest {
        val datasource = DefaultGetCharactersRemoteDatasource(
            httpClient = createHttpClient(MockEngine { throw UnresolvedAddressException() }),
            ioDispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        val result = datasource(page = null, name = null)

        assertEquals(RemoteResult.Error("No internet connection."), result)
    }

    private fun TestScope.datasourceReturning(
        body: String,
        status: HttpStatusCode = HttpStatusCode.OK,
    ) = DefaultGetCharactersRemoteDatasource(
        httpClient = mockHttpClient(body, status),
        ioDispatcher = UnconfinedTestDispatcher(testScheduler),
    )

    private companion object {
        val CHARACTERS_JSON = """
            {
              "info": { "count": 826, "pages": 42, "next": "https://rickandmortyapi.com/api/character?page=2", "prev": null },
              "results": [
                {
                  "id": 1,
                  "name": "Rick Sanchez",
                  "status": "Alive",
                  "species": "Human",
                  "type": "",
                  "gender": "Male",
                  "origin": { "name": "Earth (C-137)", "url": "https://rickandmortyapi.com/api/location/1" },
                  "location": { "name": "Citadel of Ricks", "url": "https://rickandmortyapi.com/api/location/3" },
                  "image": "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
                  "episode": [ "https://rickandmortyapi.com/api/episode/1" ],
                  "url": "https://rickandmortyapi.com/api/character/1",
                  "created": "2017-11-04T18:48:46.250Z"
                }
              ]
            }
        """.trimIndent()
    }
}
