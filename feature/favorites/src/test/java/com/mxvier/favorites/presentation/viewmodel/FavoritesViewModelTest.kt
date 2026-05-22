package com.mxvier.favorites.presentation.viewmodel

import app.cash.turbine.test
import com.mxvier.data.movies.domain.model.FavoriteMovie
import com.mxvier.data.movies.domain.repository.FavoriteRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
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
class FavoritesViewModelTest {

    private lateinit var viewModel: FavoritesViewModel
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
        viewModel = FavoritesViewModel(favoriteRepository, testDispatcher)
    }

    @Test
    fun `observeFavorites should emit Success when list is not empty`() = runTest {
        val movies = listOf(
            FavoriteMovie(1, "Title", null, 5.0, "Overview", "Action")
        )
        
        createViewModel()
        favoritesFlow.value = movies
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is FavoritesUiState.Success)
            assertEquals(movies, (state as FavoritesUiState.Success).movies)
        }
    }

    @Test
    fun `observeFavorites should emit Empty when list is empty`() = runTest {
        createViewModel()
        favoritesFlow.value = emptyList()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            assertEquals(FavoritesUiState.Empty, awaitItem())
        }
    }

    @Test
    fun `observeFavorites should emit Error when exception occurs`() = runTest {
        coEvery { favoriteRepository.getFavoriteMovies() } returns flow { throw Exception("DB Error") }
        
        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is FavoritesUiState.Error)
            assertEquals("DB Error", (state as FavoritesUiState.Error).message)
        }
    }
}
