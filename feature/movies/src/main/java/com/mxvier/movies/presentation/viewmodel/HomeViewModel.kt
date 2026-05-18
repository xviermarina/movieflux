package com.mxvier.movies.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mxvier.core.di.IoDispatcher
import com.mxvier.movies.data.remote.response.MovieResponse
import com.mxvier.movies.data.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val accumulatedMovies = mutableListOf<MovieResponse>()
    private var currentPage = 1
    private var isPageLoading = false
    private var isLastPage = false

    init {
        fetchMovies()
    }

    fun fetchMovies() {
        if (isPageLoading || isLastPage) return

        isPageLoading = true

        if (currentPage == 1) {
            _uiState.value = HomeUiState.Loading
        } else {
            _uiState.value = HomeUiState.Success(
                movies = accumulatedMovies.toList(),
                isPagingLoading = true
            )
        }

        viewModelScope.launch(dispatcher) {
            try {
                val newMovies = repository.fetchPopularMovies(currentPage)

                if (newMovies.isEmpty()) {
                    isLastPage = true
                    if (currentPage == 1) {
                        _uiState.value = HomeUiState.Error("Nenhum filme encontrado.")
                    } else {
                        _uiState.value = HomeUiState.Success(
                            movies = accumulatedMovies.toList(),
                            isPagingLoading = false
                        )
                    }
                } else {
                    accumulatedMovies.addAll(newMovies)
                    _uiState.value = HomeUiState.Success(
                        movies = accumulatedMovies.toList(),
                        isPagingLoading = false
                    )
                    currentPage++
                }
            } catch (e: Exception) {
                val errorMessage = e.localizedMessage ?: "Ocorreu um erro."
                if (currentPage == 1) {
                    _uiState.value = HomeUiState.Error(errorMessage)
                } else {
                    _uiState.value = HomeUiState.Error(
                        message = errorMessage,
                        accumulatedMovies = accumulatedMovies.toList()
                    )
                }
            } finally {
                isPageLoading = false
            }
        }
    }
}