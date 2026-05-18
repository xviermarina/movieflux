package com.mxvier.movies.details.data.remote.response

import com.google.gson.annotations.SerializedName

data class MovieDetailResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("genres") val genres: List<GenreDetailResponse>?
)