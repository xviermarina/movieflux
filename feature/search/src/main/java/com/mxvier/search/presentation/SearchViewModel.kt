package com.mxvier.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mxvier.core.di.IoDispatcher
import com.mxvier.search.data.repository.SearchRepository
import com.mxvier.data.movies.domain.repository.FavoriteRepository
import com.mxvier.data.movies.domain.model.FavoriteMovie
import com.mxvier.search.domain.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: SearchRepository,
    private val favoriteRepository: FavoriteRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState

    private var favoriteIds = setOf<Int>()

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch(dispatcher) {
            favoriteRepository.getFavoriteMovies().collectLatest { favs ->
                favoriteIds = favs.map { it.id }.toSet()
                
                val currentState = _uiState.value
                if (currentState is SearchUiState.Success) {
                    val updatedMovies = currentState.movies.map { movie ->
                        movie.copy(isFavorite = favoriteIds.contains(movie.id))
                    }
                    _uiState.value = SearchUiState.Success(updatedMovies)
                }
            }
        }
    }

    fun searchMovies(query: String) {
        if (query.isBlank()) {
            _uiState.value = SearchUiState.Idle
            return
        }

        viewModelScope.launch(dispatcher) {
            _uiState.value = SearchUiState.Loading
            try {
                val results = repository.searchMovies(query, 1)
                
                val processedResults = results.map { movie ->
                    movie.copy(isFavorite = favoriteIds.contains(movie.id))
                }

                _uiState.value = if (processedResults.isEmpty()) {
                    SearchUiState.Empty
                } else {
                    SearchUiState.Success(processedResults)
                }
            } catch (e: Exception) {
                _uiState.value = SearchUiState.Error("Ocorreu um erro ao buscar os filmes.")
            }
        }
    }

    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch(dispatcher) {
            if (movie.isFavorite) {
                favoriteRepository.removeFavorite(movie.id)
            } else {
                val favoriteMovie = FavoriteMovie(
                    id = movie.id,
                    title = movie.title,
                    posterPath = movie.posterPath,
                    voteAverage = movie.voteAverage,
                    overview = movie.overview ?: "",
                    genres = "" // Gêneros não vêm na busca básica do TMDB sem chamadas extras
                )
                favoriteRepository.saveFavorite(favoriteMovie)
            }
        }
    }
}
