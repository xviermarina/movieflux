package com.mxvier.search.data.repository

import com.mxvier.search.domain.model.Movie


interface SearchRepository {
    suspend fun searchMovies(query: String, page: Int): List<Movie>
}