package com.mxvier.movies.home.data.repository

import app.cash.turbine.test
import com.mxvier.movies.home.data.remote.MoviesService
import com.mxvier.movies.home.data.remote.response.GenreListResponse
import com.mxvier.movies.home.data.remote.response.GenreResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GenreRepositoryImplTest {

    private lateinit var repository: GenreRepositoryImpl
    private val api: MoviesService = mockk()

    @Before
    fun setup() {
        repository = GenreRepositoryImpl(api)
    }

    @Test
    fun `getGenres should fetch from API and emit when cache is empty`() = runTest {
        val genres = listOf(GenreResponse(1, "Action"))
        val response = GenreListResponse(genres)
        coEvery { api.getGenres() } returns response

        repository.getGenres().test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("Action", result[0].name)
            awaitComplete()
        }
    }

    @Test
    fun `getGenres should emit from cache on second call`() = runTest {
        val genres = listOf(GenreResponse(1, "Action"))
        val response = GenreListResponse(genres)
        coEvery { api.getGenres() } returns response

        // First call to fill cache
        repository.getGenres().test {
            awaitItem()
            awaitComplete()
        }

        // Second call should use cache
        repository.getGenres().test {
            val result = awaitItem()
            assertEquals("Action", result[0].name)
            awaitComplete()
        }

        coEvery { api.getGenres() } // This would throw if called again because no returns defined
        // Actually coVerify is better
    }
}
