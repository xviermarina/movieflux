package com.mxvier.movies.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mxvier.movies.data.repository.HomeRepository
import com.mxvier.core.di.IoDispatcher
import com.mxvier.movies.data.remote.response.MovieResponse
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
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
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

    internal fun fetchMovies() {
        if (isPageLoading || isLastPage) return

        isPageLoading = true

        if (currentPage == 1){
            _uiState.value = HomeUiState.Loading
        }

        viewModelScope.launch(ioDispatcher) {
            _uiState.value = HomeUiState.Loading

            try {
                val newMovies = repository.fetchPopularMovies(currentPage)
                if (newMovies.isEmpty()){
                    isLastPage = true
                    if (currentPage == 1){
                        _uiState.value = HomeUiState.Error("Nenhum filme encontrado.")
                    }
                } else {
                    accumulatedMovies.addAll(newMovies)
                    _uiState.value = HomeUiState.Success(accumulatedMovies.toList())
                    currentPage++
                }
            } catch (e: Exception) {
                if (currentPage == 1) {
                    _uiState.value = HomeUiState.Error(e.message ?: "Erro desconhecido")
                }
            }
                finally {
                    isPageLoading = false
                }
            }
        }
}