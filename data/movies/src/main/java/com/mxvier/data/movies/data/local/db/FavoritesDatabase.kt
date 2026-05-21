package com.mxvier.data.movies.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mxvier.data.movies.data.local.dao.FavoriteMovieDao
import com.mxvier.data.movies.data.local.entity.FavoriteMovieEntity

@Database(entities = [FavoriteMovieEntity::class], version = 1, exportSchema = false)
abstract class FavoritesDatabase : RoomDatabase() {
    abstract fun favoriteMovieDao(): FavoriteMovieDao
}
