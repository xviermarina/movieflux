package com.mxvier.movies.home.presentation.viewmodel

import com.mxvier.movies.home.data.remote.response.MovieResponse

sealed interface HomeUiState {
    object Loading : HomeUiState

    object Empty : HomeUiState

    data class Success(
        val movies: List<MovieResponse>,
        val isPagingLoading: Boolean = false
    ) : HomeUiState

    data class Error(
        val message: String,
        val accumulatedMovies: List<MovieResponse> = emptyList()
    ) : HomeUiState
}