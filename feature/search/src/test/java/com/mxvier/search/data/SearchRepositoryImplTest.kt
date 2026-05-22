package com.mxvier.search.data

import com.mxvier.search.data.remote.MovieSearchApiService
import com.mxvier.search.data.remote.model.MovieSearchResponse
import com.mxvier.search.domain.model.Movie
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SearchRepositoryImplTest {

    private lateinit var repository: SearchRepositoryImpl
    private val api: MovieSearchApiService = mockk()

    @Before
    fun setup() {
        repository = SearchRepositoryImpl(api)
    }

    @Test
    fun `searchMovies should return list of movies from API`() = runTest {
        val movies = listOf(
            Movie(id = 1, title = "The Matrix", posterPath = "", voteAverage = 8.8, overview = "", releaseDate = "1999")
        )
        val response = MovieSearchResponse(page = 1, results = movies, totalResults = 1, totalPages = 1)
        
        coEvery { api.searchMovies("Matrix", 1) } returns response
        
        val result = repository.searchMovies("Matrix", 1)
        
        assertEquals(movies, result)
    }
}
