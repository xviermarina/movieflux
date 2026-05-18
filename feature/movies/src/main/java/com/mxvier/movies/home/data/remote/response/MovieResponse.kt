package com.mxvier.movies.home.data.remote.response

import com.google.gson.annotations.SerializedName

data class TMDBHomeResponse(
    @SerializedName("results") val results: List<MovieResponse>
)
data class MovieResponse(
        @SerializedName("id") val id: Int,
        @SerializedName("title") val title: String,
        @SerializedName("overview") val overview: String,
        @SerializedName("poster_path") val posterPath: String?,
        @SerializedName("vote_average") val voteAverage: Double
)