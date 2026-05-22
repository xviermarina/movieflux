package com.mxvier.movies.home.domain.repository

import com.mxvier.movies.home.domain.model.Genre
import kotlinx.coroutines.flow.Flow

interface GenreRepository {
    fun getGenres(): Flow<List<Genre>>
}
