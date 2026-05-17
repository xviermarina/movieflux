package com.mxvier.movies.presentation.viewmodel

import com.mxvier.movies.data.remote.response.MovieResponse

sealed interface HomeUiState {
    object Loading: HomeUiState
    data class Success(val movies: List<MovieResponse>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}