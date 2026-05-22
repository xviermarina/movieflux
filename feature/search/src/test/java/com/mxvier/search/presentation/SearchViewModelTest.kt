package com.mxvier.search.presentation

import app.cash.turbine.test
import com.mxvier.data.movies.domain.model.FavoriteMovie
import com.mxvier.data.movies.domain.repository.FavoriteRepository
import com.mxvier.search.data.repository.SearchRepository
import com.mxvier.search.domain.model.Movie
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private lateinit var viewModel: SearchViewModel
    private val searchRepository: SearchRepository = mockk()
    private val favoriteRepository: FavoriteRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    private val favoritesFlow = MutableStateFlow<List<FavoriteMovie>>(emptyList())

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { favoriteRepository.getFavoriteMovies() } returns favoritesFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() {
        viewModel = SearchViewModel(
            searchRepository,
            favoriteRepository,
            testDispatcher
        )
    }

    @Test
    fun `searchMovies should emit Success when results are found`() = runTest {
        val query = "Matrix"
        val movies = listOf(
            Movie(id = 1, title = "The Matrix", posterPath = "", voteAverage = 8.8, overview = "", releaseDate = "1999")
        )
        coEvery { searchRepository.searchMovies(query, 1) } returns movies
        
        createViewModel()
        
        viewModel.searchMovies(query)
        
        viewModel.uiState.test {
            // Depending on implementation, might emit Idle first or Loading directly
            val first = awaitItem()
            val loading = if (first == SearchUiState.Idle) awaitItem() else first
            
            assertEquals(SearchUiState.Loading, loading)
            
            val success = awaitItem()
            assertTrue(success is SearchUiState.Success)
            assertEquals(movies, (success as SearchUiState.Success).movies)
        }
    }

    @Test
    fun `searchMovies should emit Empty when no results are found`() = runTest {
        val query = "NonExistentMovie"
        coEvery { searchRepository.searchMovies(query, 1) } returns emptyList()
        
        createViewModel()
        
        viewModel.searchMovies(query)
        
        viewModel.uiState.test {
            val first = awaitItem()
            val loading = if (first == SearchUiState.Idle) awaitItem() else first
            assertEquals(SearchUiState.Loading, loading)
            assertEquals(SearchUiState.Empty, awaitItem())
        }
    }

    @Test
    fun `searchMovies should emit Error when exception occurs`() = runTest {
        val query = "Error"
        coEvery { searchRepository.searchMovies(query, 1) } throws Exception("Network error")
        
        createViewModel()
        
        viewModel.searchMovies(query)
        
        viewModel.uiState.test {
            val first = awaitItem()
            val loading = if (first == SearchUiState.Idle) awaitItem() else first
            assertEquals(SearchUiState.Loading, loading)
            val error = awaitItem()
            assertTrue(error is SearchUiState.Error)
            assertEquals("Ocorreu um erro ao buscar os filmes.", (error as SearchUiState.Error).message)
        }
    }

    @Test
    fun `searchMovies should emit Idle when query is blank`() = runTest {
        createViewModel()
        viewModel.searchMovies("  ")
        
        viewModel.uiState.test {
            assertEquals(SearchUiState.Idle, awaitItem())
        }
    }

    @Test
    fun `toggleFavorite should save when not favorite`() = runTest {
        val movie = Movie(id = 1, title = "Title", posterPath = "", voteAverage = 5.0, overview = "Overview", releaseDate = "2024", isFavorite = false)
        coEvery { favoriteRepository.saveFavorite(any()) } returns Unit
        
        createViewModel()
        viewModel.toggleFavorite(movie)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { favoriteRepository.saveFavorite(match { it.id == movie.id }) }
    }

    @Test
    fun `toggleFavorite should remove when is favorite`() = runTest {
        val movie = Movie(id = 1, title = "Title", posterPath = "", voteAverage = 5.0, overview = "Overview", releaseDate = "2024", isFavorite = true)
        coEvery { favoriteRepository.removeFavorite(1) } returns Unit
        
        createViewModel()
        viewModel.toggleFavorite(movie)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { favoriteRepository.removeFavorite(1) }
    }
}
