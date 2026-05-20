package com.mxvier.search.data

import com.mxvier.search.data.remote.MovieSearchApiService
import com.mxvier.search.data.repository.SearchRepository
import com.mxvier.search.domain.model.Movie
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val api: MovieSearchApiService
) : SearchRepository {

    override suspend fun searchMovies(query: String, page: Int): List<Movie> {
        return api.searchMovies(query = query, page = page).results
    }
}