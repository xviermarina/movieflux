package com.mxvier.data.movies.data.repository

import com.mxvier.data.movies.data.local.dao.FavoriteMovieDao
import com.mxvier.data.movies.data.local.entity.FavoriteMovieEntity
import com.mxvier.data.movies.domain.model.FavoriteMovie
import com.mxvier.data.movies.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val movieDao: FavoriteMovieDao
) : FavoriteRepository {

    override fun getFavoriteMovies(): Flow<List<FavoriteMovie>> {
        return movieDao.getFavoriteMovies().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveFavorite(movie: FavoriteMovie) {
        movieDao.insertMovie(movie.toEntity())
    }

    override suspend fun removeFavorite(id: Int) {
        movieDao.getMovieById(id)?.let {
            movieDao.deleteMovie(it)
        }
    }

    override fun isFavorite(id: Int): Flow<Boolean> {
        return movieDao.isFavorite(id)
    }

    private fun FavoriteMovieEntity.toDomain() = FavoriteMovie(
        id = id,
        title = title,
        posterPath = posterPath,
        voteAverage = voteAverage,
        overview = overview,
        genres = genres
    )

    private fun FavoriteMovie.toEntity() = FavoriteMovieEntity(
        id = id,
        title = title,
        posterPath = posterPath,
        voteAverage = voteAverage,
        overview = overview,
        genres = genres
    )
}
