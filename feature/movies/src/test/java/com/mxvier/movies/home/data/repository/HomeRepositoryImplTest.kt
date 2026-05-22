package com.mxvier.movies.home.data.repository

import com.mxvier.movies.home.data.remote.MoviesService
import com.mxvier.movies.home.data.remote.response.MovieResponse
import com.mxvier.movies.home.data.remote.response.TMDBHomeResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class HomeRepositoryImplTest {

    private lateinit var repository: HomeRepositoryImpl
    private val moviesService: MoviesService = mockk()

    @Before
    fun setup() {
        repository = HomeRepositoryImpl(moviesService)
    }

    @Test
    fun `fetchPopularMovies should return results from service`() = runTest {
        val movies = listOf(
            MovieResponse(id = 1, title = "Title", overview = "Overview", posterPath = null, voteAverage = 7.0)
        )
        val response = TMDBHomeResponse(results = movies)
        coEvery { moviesService.getPopularMovies(1) } returns response

        val result = repository.fetchPopularMovies(1)

        assertEquals(movies, result)
    }
}
