package com.mxvier.movies.home.data.remote

import com.mxvier.movies.home.data.remote.response.GenreListResponse
import com.mxvier.movies.home.data.remote.response.TMDBHomeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int,
        @Query("language") language: String = "pt-BR"
    ): TMDBHomeResponse

    @GET("genre/movie/list")
    suspend fun getGenres(
        @Query("language") language: String = "pt-BR"
    ): GenreListResponse
}
