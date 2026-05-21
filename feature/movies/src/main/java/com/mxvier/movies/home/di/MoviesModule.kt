package com.mxvier.movies.home.di

import com.mxvier.movies.home.data.remote.MoviesService
import com.mxvier.movies.home.data.repository.HomeRepository
import com.mxvier.movies.home.data.repository.HomeRepositoryImpl
import com.mxvier.movies.home.data.repository.GenreRepositoryImpl
import com.mxvier.movies.home.domain.repository.GenreRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MoviesModule {

    @Binds
    @Singleton
    abstract fun bindHomeRepository(
        impl: HomeRepositoryImpl
    ): HomeRepository

    @Binds
    @Singleton
    abstract fun bindGenreRepository(
        impl: GenreRepositoryImpl
    ): GenreRepository

    companion object {

        @Provides
        @Singleton
        fun provideMoviesService(retrofit: Retrofit): MoviesService {
            return retrofit.create(MoviesService::class.java)
        }
    }
}
