package com.mxvier.search.data.remote

import com.mxvier.search.data.remote.model.MovieSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieSearchApiService {
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): MovieSearchResponse
}