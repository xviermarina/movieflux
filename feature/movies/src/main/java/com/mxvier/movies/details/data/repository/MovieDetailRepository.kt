package com.mxvier.movies.details.data.repository

import com.mxvier.movies.details.data.remote.response.MovieDetailResponse

interface MovieDetailRepository {
    suspend fun getMovieDetails(movieId: Int): MovieDetailResponse
}