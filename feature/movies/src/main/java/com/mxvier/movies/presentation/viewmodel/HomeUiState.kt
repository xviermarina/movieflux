package com.mxvier.movies.presentation.viewmodel

import com.mxvier.movies.data.remote.response.MovieResponse

sealed interface HomeUiState {
    object Loading : HomeUiState

    data class Success(
        val movies: List<MovieResponse>,
        val isPagingLoading: Boolean = false
    ) : HomeUiState

    data class Error(
        val message: String,
        val accumulatedMovies: List<MovieResponse> = emptyList()
    ) : HomeUiState
}