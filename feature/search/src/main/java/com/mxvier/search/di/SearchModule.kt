package com.mxvier.search.di

import com.mxvier.search.data.remote.MovieSearchApiService
import com.mxvier.search.data.repository.SearchRepository
import com.mxvier.search.data.SearchRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SearchModule {

    @Binds
    @Singleton
    abstract fun bindSearchRepository(
        searchRepositoryImpl: SearchRepositoryImpl
    ): SearchRepository

    companion object {
        @Provides
        @Singleton
        fun provideMovieSearchApiService(retrofit: Retrofit): MovieSearchApiService {
            return retrofit.create(MovieSearchApiService::class.java)
        }
    }
}