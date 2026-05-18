package com.mxvier.movies.data.repository

import com.mxvier.movies.data.remote.response.MovieResponse

interface HomeRepository {
    suspend fun fetchPopularMovies(page: Int): List<MovieResponse>
}