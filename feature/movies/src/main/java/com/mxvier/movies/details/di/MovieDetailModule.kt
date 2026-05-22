package com.mxvier.movies.details.di

import com.mxvier.movies.details.data.remote.service.MovieDetailApiService
import com.mxvier.movies.details.data.repository.MovieDetailRepository
import com.mxvier.movies.details.data.repository.MovieDetailRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MovieDetailModule {

    @Binds
    @Singleton
    abstract fun bindMovieDetailRepository(
        movieDetailRepositoryImpl: MovieDetailRepositoryImpl
    ): MovieDetailRepository

    companion object {
        @Provides
        @Singleton
        fun provideMovieDetailApiService(retrofit: Retrofit): MovieDetailApiService {
            return retrofit.create(MovieDetailApiService::class.java)
        }
    }
}