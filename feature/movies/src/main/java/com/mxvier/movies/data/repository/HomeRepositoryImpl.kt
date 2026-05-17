package com.mxvier.movies.data.repository

import com.mxvier.movies.data.remote.MoviesService
import com.mxvier.movies.data.remote.response.MovieResponse
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val moviesService: MoviesService,
): HomeRepository {
    override suspend fun fetchPopularMovies(page: Int): List<MovieResponse> {
        return moviesService.getPopularMovies(page).results
    }

}