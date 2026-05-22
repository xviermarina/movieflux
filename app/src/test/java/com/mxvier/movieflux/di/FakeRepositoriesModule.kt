package com.mxvier.movieflux.di

import com.mxvier.data.movies.domain.model.FavoriteMovie
import com.mxvier.data.movies.domain.repository.FavoriteRepository
import com.mxvier.movies.details.data.remote.response.MovieDetailResponse
import com.mxvier.movies.details.data.repository.MovieDetailRepository
import com.mxvier.movies.home.data.repository.HomeRepository
import com.mxvier.movies.home.data.remote.response.MovieResponse
import com.mxvier.movies.home.domain.model.Genre
import com.mxvier.movies.home.domain.repository.GenreRepository
import com.mxvier.search.data.repository.SearchRepository
import com.mxvier.search.domain.model.Movie
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Singleton
import com.mxvier.movies.home.di.MoviesModule
import com.mxvier.search.di.SearchModule
import com.mxvier.movies.details.di.MovieDetailModule
import com.mxvier.data.movies.di.DataMoviesModule

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [MoviesModule::class, SearchModule::class, MovieDetailModule::class, DataMoviesModule::class]
)
object FakeRepositoriesModule {

    @Provides
    @Singleton
    fun provideHomeRepository(): HomeRepository = object : HomeRepository {
        override suspend fun fetchPopularMovies(page: Int): List<MovieResponse> = listOf(
            MovieResponse(1, "Movie 1", "Overview 1", null, 8.0)
        )
    }

    @Provides
    @Singleton
    fun provideGenreRepository(): GenreRepository = object : GenreRepository {
        override fun getGenres(): Flow<List<Genre>> = flowOf(listOf(Genre(1, "Action")))
    }

    @Provides
    @Singleton
    fun provideFavoriteRepository(): FavoriteRepository = object : FavoriteRepository {
        private val favorites = MutableStateFlow<List<FavoriteMovie>>(emptyList())
        override fun getFavoriteMovies(): Flow<List<FavoriteMovie>> = favorites
        override suspend fun saveFavorite(movie: FavoriteMovie) {
            favorites.value = favorites.value + movie
        }
        override suspend fun removeFavorite(id: Int) {
            favorites.value = favorites.value.filter { it.id != id }
        }
        override fun isFavorite(id: Int): Flow<Boolean> = flowOf(false)
    }

    @Provides
    @Singleton
    fun provideMovieDetailRepository(): MovieDetailRepository = object : MovieDetailRepository {
        override suspend fun getMovieDetails(movieId: Int): MovieDetailResponse = MovieDetailResponse(
            id = movieId,
            title = "Movie Title $movieId",
            overview = "Overview for $movieId",
            posterPath = null,
            voteAverage = 9.0,
            genres = emptyList()
        )
    }

    @Provides
    @Singleton
    fun provideSearchRepository(): SearchRepository = object : SearchRepository {
        override suspend fun searchMovies(query: String, page: Int): List<Movie> = listOf(
            Movie(1, "Avengers", "", 9.0, "Overview", "2024")
        )
    }
}
