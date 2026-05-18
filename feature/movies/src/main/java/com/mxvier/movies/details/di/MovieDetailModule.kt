package com.mxvier.movies.details.di

import com.mxvier.movies.details.data.remote.service.MovieDetailsApiService
import com.mxvier.movies.details.data.repository.MovieDetailRepository
import com.mxvier.movies.details.data.repository.MovieDetailRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
object MovieDetailModule {

    @Provides
    @ViewModelScoped
    fun provideMovieDetailsApiService(retrofit: Retrofit): MovieDetailsApiService {
        return retrofit.create(MovieDetailsApiService::class.java)
    }

    @Provides
    @ViewModelScoped
    fun provideMovieDetailRepository(
        apiService: MovieDetailsApiService
    ): MovieDetailRepository {
        return MovieDetailRepositoryImpl(apiService)
    }
}