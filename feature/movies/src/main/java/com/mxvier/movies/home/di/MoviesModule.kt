package com.mxvier.movies.home.di

import com.mxvier.movies.home.data.remote.MoviesService
import com.mxvier.movies.home.data.repository.HomeRepository
import com.mxvier.movies.home.data.repository.HomeRepositoryImpl
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