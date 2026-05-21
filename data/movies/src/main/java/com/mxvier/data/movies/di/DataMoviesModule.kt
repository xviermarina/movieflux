package com.mxvier.data.movies.di

import android.content.Context
import androidx.room.Room
import com.mxvier.data.movies.data.local.dao.FavoriteMovieDao
import com.mxvier.data.movies.data.local.db.FavoritesDatabase
import com.mxvier.data.movies.data.repository.FavoriteRepositoryImpl
import com.mxvier.data.movies.domain.repository.FavoriteRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataMoviesModule {

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(impl: FavoriteRepositoryImpl): FavoriteRepository

    companion object {
        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext context: Context): FavoritesDatabase {
            return Room.databaseBuilder(
                context,
                FavoritesDatabase::class.java,
                "movie_flux_favorites_db"
            ).build()
        }

        @Provides
        @Singleton
        fun provideFavoriteMovieDao(database: FavoritesDatabase): FavoriteMovieDao {
            return database.favoriteMovieDao()
        }
    }
}
