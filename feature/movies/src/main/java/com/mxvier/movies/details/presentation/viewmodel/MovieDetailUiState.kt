package com.mxvier.movies.details.presentation.viewmodel

import com.mxvier.movies.details.data.remote.response.MovieDetailResponse

sealed interface MovieDetailUiState {
    object Loading : MovieDetailUiState
    data class Success(val movie: MovieDetailResponse) : MovieDetailUiState
    data class Error(val message: String) : MovieDetailUiState
}