package com.mxvier.movies.details.data.remote.service

import com.mxvier.movies.details.data.remote.response.MovieDetailResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface MovieDetailsApiService {
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int
    ): MovieDetailResponse
}