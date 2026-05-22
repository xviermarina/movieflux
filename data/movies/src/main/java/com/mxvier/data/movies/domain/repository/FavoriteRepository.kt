package com.mxvier.data.movies.domain.repository

import com.mxvier.data.movies.domain.model.FavoriteMovie
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getFavoriteMovies(): Flow<List<FavoriteMovie>>
    suspend fun saveFavorite(movie: FavoriteMovie)
    suspend fun removeFavorite(id: Int)
    fun isFavorite(id: Int): Flow<Boolean>
}
