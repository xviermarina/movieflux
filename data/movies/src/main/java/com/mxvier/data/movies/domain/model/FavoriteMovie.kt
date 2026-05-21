package com.mxvier.data.movies.domain.model

data class FavoriteMovie(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val voteAverage: Double,
    val overview: String,
    val genres: String?
)
