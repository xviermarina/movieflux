package com.mxvier.movies.details.data.repository

import com.mxvier.movies.details.data.remote.response.MovieDetailResponse
import com.mxvier.movies.details.data.remote.service.MovieDetailsApiService
import javax.inject.Inject

class MovieDetailRepositoryImpl @Inject constructor(
    private val apiService: MovieDetailsApiService
): MovieDetailRepository {
    override suspend fun getMovieDetails(movieId: Int): MovieDetailResponse {
        return apiService.getMovieDetails(movieId)
    }
}