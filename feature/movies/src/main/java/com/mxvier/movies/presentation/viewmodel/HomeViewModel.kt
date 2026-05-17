package com.mxvier.movies.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mxvier.movies.data.repository.HomeRepository
import com.mxvier.core.di.IoDispatcher
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
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        fetchMovies()
    }

    internal fun fetchMovies() {
        viewModelScope.launch(ioDispatcher) {
            _uiState.value = HomeUiState.Loading

            try {
                val movieList = repository.fetchPopularMovies()
                if (movieList.isEmpty()){
                    _uiState.value = HomeUiState.Error("Nenhum filme encontrado.")
                } else {
                    _uiState.value = HomeUiState.Success(movieList)
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }
}