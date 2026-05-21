package com.mxvier.movies.home.data.repository

import com.mxvier.movies.home.data.remote.MoviesService
import com.mxvier.movies.home.domain.model.Genre
import com.mxvier.movies.home.domain.repository.GenreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenreRepositoryImpl @Inject constructor(
    private val api: MoviesService
) : GenreRepository {
    private var cachedGenres: List<Genre>? = null

    override fun getGenres(): Flow<List<Genre>> = flow {
        cachedGenres?.let {
            emit(it)
            return@flow
        }

        val response = api.getGenres()
        val genres = response.genres.map { Genre(it.id, it.name) }
        cachedGenres = genres
        emit(genres)
    }
}
