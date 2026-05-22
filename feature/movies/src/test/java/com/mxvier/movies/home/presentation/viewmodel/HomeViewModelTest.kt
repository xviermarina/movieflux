package com.mxvier.movies.home.presentation.viewmodel

import app.cash.turbine.test
import com.mxvier.core.security.SecurityManager
import com.mxvier.data.movies.domain.model.FavoriteMovie
import com.mxvier.data.movies.domain.repository.FavoriteRepository
import com.mxvier.movies.home.data.remote.response.MovieResponse
import com.mxvier.movies.home.data.repository.HomeRepository
import com.mxvier.movies.home.domain.model.Genre
import com.mxvier.movies.home.domain.repository.GenreRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
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
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private val repository: HomeRepository = mockk()
    private val genreRepository: GenreRepository = mockk()
    private val favoriteRepository: FavoriteRepository = mockk()
    private val securityManager: SecurityManager = mockk()
    private val testDispatcher = StandardTestDispatcher()

    private val favoritesFlow = MutableStateFlow<List<FavoriteMovie>>(emptyList())

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { favoriteRepository.getFavoriteMovies() } returns favoritesFlow
        coEvery { genreRepository.getGenres() } returns flowOf(listOf(Genre(1, "Ação")))
        every { securityManager.logout() } returns Unit
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() {
        viewModel = HomeViewModel(
            repository,
            genreRepository,
            favoriteRepository,
            securityManager,
            testDispatcher
        )
    }

    @Test
    fun `fetchMovies should emit Success when repository returns data`() = runTest {
        val movies = listOf(
            MovieResponse(id = 1, title = "Movie", overview = "Overview", posterPath = null, voteAverage = 7.0, genreIds = listOf(1))
        )
        coEvery { repository.fetchPopularMovies(1) } returns movies
        
        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is HomeUiState.Success)
            assertEquals(1, (state as HomeUiState.Success).movies.size)
            assertEquals("Ação", state.movies[0].genreNames?.first())
        }
    }

    @Test
    fun `fetchMovies should emit Error when exception occurs`() = runTest {
        coEvery { repository.fetchPopularMovies(1) } throws Exception("Error")
        
        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is HomeUiState.Error)
            assertEquals("Error", (state as HomeUiState.Error).message)
        }
    }

    @Test
    fun `logout should call securityManager logout`() {
        createViewModel()
        viewModel.logout()
        coVerify { securityManager.logout() }
    }
}
