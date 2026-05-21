package com.mxvier.data.movies.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_movies")
data class FavoriteMovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val posterPath: String?,
    val voteAverage: Double,
    val overview: String,
    val genres: String?
)
