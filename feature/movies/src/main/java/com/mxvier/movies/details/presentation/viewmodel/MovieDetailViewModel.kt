package com.mxvier.movies.details.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mxvier.core.di.IoDispatcher
import com.mxvier.movies.details.data.repository.MovieDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val repository: MovieDetailRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow<MovieDetailUiState>(MovieDetailUiState.Loading)
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()

    fun fetchMovieDetails(movieId: Int) {
        _uiState.value = MovieDetailUiState.Loading

        viewModelScope.launch(dispatcher) {
            try {
                val detail = repository.getMovieDetails(movieId)
                _uiState.value = MovieDetailUiState.Success(detail)
            } catch (e: Exception) {
                _uiState.value = MovieDetailUiState.Error(
                    e.localizedMessage ?: "Erro ao carregar detalhes do filme."
                )
            }
        }
    }
}