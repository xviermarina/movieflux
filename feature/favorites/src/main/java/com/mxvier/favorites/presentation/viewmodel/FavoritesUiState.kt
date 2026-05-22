package com.mxvier.favorites.presentation.viewmodel

import com.mxvier.data.movies.domain.model.FavoriteMovie

sealed interface FavoritesUiState {
    object Loading : FavoritesUiState
    data class Success(val movies: List<FavoriteMovie>) : FavoritesUiState
    object Empty : FavoritesUiState
    data class Error(val message: String) : FavoritesUiState
}