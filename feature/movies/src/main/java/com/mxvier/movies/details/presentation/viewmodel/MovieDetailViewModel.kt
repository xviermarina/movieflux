package com.mxvier.movies.details.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mxvier.core.di.IoDispatcher
import com.mxvier.movies.details.data.repository.MovieDetailRepository
import com.mxvier.data.movies.domain.repository.FavoriteRepository
import com.mxvier.data.movies.domain.model.FavoriteMovie
import com.mxvier.movies.details.data.remote.response.MovieDetailResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val repository: MovieDetailRepository,
    private val favoriteRepository: FavoriteRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow<MovieDetailUiState>(MovieDetailUiState.Loading)
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private var currentMovieId: Int? = null

    fun fetchMovieDetails(movieId: Int) {
        currentMovieId = movieId
        _uiState.value = MovieDetailUiState.Loading

        viewModelScope.launch(dispatcher) {
            observeFavoriteStatus(movieId)
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

    fun retryFetch() {
        currentMovieId?.let { fetchMovieDetails(it) }
    }

    private fun observeFavoriteStatus(movieId: Int) {
        viewModelScope.launch(dispatcher) {
            favoriteRepository.isFavorite(movieId).collectLatest { isFav ->
                _isFavorite.value = isFav
            }
        }
    }

    fun toggleFavorite(movie: MovieDetailResponse) {
        viewModelScope.launch(dispatcher) {
            val favoriteMovie = FavoriteMovie(
                id = movie.id,
                title = movie.title,
                posterPath = movie.posterPath,
                voteAverage = movie.voteAverage,
                overview = movie.overview,
                genres = movie.genres?.joinToString { it.name }
            )

            if (_isFavorite.value) {
                favoriteRepository.removeFavorite(movie.id)
            } else {
                favoriteRepository.saveFavorite(favoriteMovie)
            }
        }
    }
}