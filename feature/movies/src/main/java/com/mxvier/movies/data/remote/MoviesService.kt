package com.mxvier.movies.data.remote

import com.mxvier.movies.data.remote.response.TMDBHomeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int
    ): TMDBHomeResponse
}