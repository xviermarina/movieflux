package com.mxvier.movies.di

import com.mxvier.movies.data.remote.MoviesService
import com.mxvier.movies.data.repository.HomeRepository
import com.mxvier.movies.data.repository.HomeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
abstract class MoviesModule {

    @Binds
    @ViewModelScoped
    abstract fun bindHomeRepository(
        impl: HomeRepositoryImpl
    ): HomeRepository

    companion object {

        @Provides
        @ViewModelScoped
        fun provideMoviesService(retrofit: Retrofit): MoviesService {
            return retrofit.create(MoviesService::class.java)
        }
    }
}