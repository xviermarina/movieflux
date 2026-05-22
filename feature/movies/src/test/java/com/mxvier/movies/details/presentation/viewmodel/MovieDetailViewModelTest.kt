package com.mxvier.movies.details.presentation.viewmodel

import app.cash.turbine.test
import com.mxvier.data.movies.domain.repository.FavoriteRepository
import com.mxvier.movies.details.data.remote.response.MovieDetailResponse
import com.mxvier.movies.details.data.repository.MovieDetailRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class MovieDetailViewModelTest {

    private lateinit var viewModel: MovieDetailViewModel
    private val repository: MovieDetailRepository = mockk()
    private val favoriteRepository: FavoriteRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() {
        viewModel = MovieDetailViewModel(repository, favoriteRepository, testDispatcher)
    }

    @Test
    fun `fetchMovieDetails should emit Success when repository returns data`() = runTest {
        val detail = MovieDetailResponse(1, "Title", "Overview", null, 8.0, emptyList())
        coEvery { repository.getMovieDetails(1) } returns detail
        coEvery { favoriteRepository.isFavorite(1) } returns flowOf(false)

        createViewModel()
        viewModel.fetchMovieDetails(1)

        viewModel.uiState.test {
            assertEquals(MovieDetailUiState.Loading, awaitItem())
            val state = awaitItem()
            assertTrue(state is MovieDetailUiState.Success)
            assertEquals(detail, (state as MovieDetailUiState.Success).movie)
        }
    }

    @Test
    fun `fetchMovieDetails should emit Error when exception occurs`() = runTest {
        coEvery { repository.getMovieDetails(1) } throws Exception("API Error")
        coEvery { favoriteRepository.isFavorite(1) } returns flowOf(false)

        createViewModel()
        viewModel.fetchMovieDetails(1)

        viewModel.uiState.test {
            assertEquals(MovieDetailUiState.Loading, awaitItem())
            val state = awaitItem()
            assertTrue(state is MovieDetailUiState.Error)
            assertEquals("API Error", (state as MovieDetailUiState.Error).message)
        }
    }
}
