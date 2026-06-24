package com.ericwafula.rickandmorty.datasources.remote.helpers

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Sanity-checks the test HTTP helpers used to unit-test data sources. */
class MockHttpClientTest {

    @Test
    fun `mock client returns the canned response body`() = runTest {
        val client = mockHttpClient("""{"message":"ok"}""")

        val body = client.get("https://example.com/anything").bodyAsText()

        assertEquals("""{"message":"ok"}""", body)
    }

    @Test
    fun `expectSuccess makes a non-2xx response throw`() = runTest {
        val client = mockHttpClient("Not Found", HttpStatusCode.NotFound)

        val error = runCatching {
            client.get("https://example.com/missing").bodyAsText()
        }.exceptionOrNull()

        assertTrue(error is ClientRequestException)
    }
}
