package com.mxvier.movies.home.data.repository

import com.mxvier.movies.home.data.remote.response.MovieResponse

interface HomeRepository {
    suspend fun fetchPopularMovies(page: Int): List<MovieResponse>
}