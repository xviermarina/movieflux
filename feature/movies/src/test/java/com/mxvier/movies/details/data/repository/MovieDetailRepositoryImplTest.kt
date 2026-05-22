package com.mxvier.movies.details.data.repository

import com.mxvier.movies.details.data.remote.response.MovieDetailResponse
import com.mxvier.movies.details.data.remote.service.MovieDetailApiService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MovieDetailRepositoryImplTest {

    private lateinit var repository: MovieDetailRepositoryImpl
    private val apiService: MovieDetailApiService = mockk()

    @Before
    fun setup() {
        repository = MovieDetailRepositoryImpl(apiService)
    }

    @Test
    fun `getMovieDetails should return data from API`() = runTest {
        val detail = MovieDetailResponse(
            id = 1,
            title = "Title",
            overview = "Overview",
            posterPath = null,
            voteAverage = 8.0,
            genres = emptyList()
        )
        coEvery { apiService.getMovieDetails(1) } returns detail

        val result = repository.getMovieDetails(1)

        assertEquals(detail, result)
    }
}
