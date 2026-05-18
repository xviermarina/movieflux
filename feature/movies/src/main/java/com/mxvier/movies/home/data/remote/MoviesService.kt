package com.mxvier.movies.home.data.remote

import com.mxvier.movies.home.data.remote.response.TMDBHomeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int,
        @Query("language") language: String = "pt-BR"
    ): TMDBHomeResponse
}