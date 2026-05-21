package com.mxvier.movies.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mxvier.core.di.IoDispatcher
import com.mxvier.movies.home.domain.model.Genre
import com.mxvier.movies.home.domain.repository.GenreRepository
import com.mxvier.movies.home.data.remote.response.MovieResponse
import com.mxvier.movies.home.data.repository.HomeRepository
import com.mxvier.data.movies.domain.repository.FavoriteRepository
import com.mxvier.data.movies.domain.model.FavoriteMovie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
    private val genreRepository: GenreRepository,
    private val favoriteRepository: FavoriteRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val accumulatedMovies = mutableListOf<MovieResponse>()
    private var favoriteIds = setOf<Int>()
    private var genres: List<Genre> = emptyList()
    private var currentPage = 1
    private var isPageLoading = false
    private var isLastPage = false

    init {
        observeFavorites()
        fetchMovies()
    }

    private fun observeFavorites() {
        viewModelScope.launch(dispatcher) {
            favoriteRepository.getFavoriteMovies().collectLatest { favs ->
                favoriteIds = favs.map { it.id }.toSet()
                accumulatedMovies.forEach { movie ->
                    movie.isFavorite = favoriteIds.contains(movie.id)
                }
                
                val currentState = _uiState.value
                if (currentState is HomeUiState.Success) {
                    _uiState.value = currentState.copy(movies = accumulatedMovies.toList())
                }
            }
        }
    }

    private suspend fun ensureGenresLoaded() {
        if (genres.isEmpty()) {
            try {
                genres = genreRepository.getGenres().first()
            } catch (e: Exception) {
                // Silently fail or log, we can still show movies without genre names
            }
        }
    }

    fun fetchMovies() {
        if (isPageLoading || isLastPage) return

        isPageLoading = true

        if (currentPage == 1) {
            _uiState.value = HomeUiState.Loading
        } else {
            _uiState.value = HomeUiState.Success(
                movies = accumulatedMovies.toList(),
                isPagingLoading = true
            )
        }

        viewModelScope.launch(dispatcher) {
            try {
                ensureGenresLoaded()
                val newMovies = repository.fetchPopularMovies(currentPage)

                if (newMovies.isEmpty()) {
                    isLastPage = true
                    if (currentPage == 1) {
                        _uiState.value = HomeUiState.Error("Nenhum filme encontrado.")
                    } else {
                        _uiState.value = HomeUiState.Success(
                            movies = accumulatedMovies.toList(),
                            isPagingLoading = false
                        )
                    }
                } else {
                    newMovies.forEach { movie ->
                        movie.genreNames = movie.genreIds?.mapNotNull { id ->
                            genres.find { it.id == id }?.name
                        }
                        movie.isFavorite = favoriteIds.contains(movie.id)
                    }
                    accumulatedMovies.addAll(newMovies)
                    _uiState.value = HomeUiState.Success(
                        movies = accumulatedMovies.toList(),
                        isPagingLoading = false
                    )
                    currentPage++
                }
            } catch (e: Exception) {
                val errorMessage = e.localizedMessage ?: "Ocorreu um erro."
                if (currentPage == 1) {
                    _uiState.value = HomeUiState.Error(errorMessage)
                } else {
                    _uiState.value = HomeUiState.Error(
                        message = errorMessage,
                        accumulatedMovies = accumulatedMovies.toList()
                    )
                }
            } finally {
                isPageLoading = false
            }
        }
    }

    fun toggleFavorite(movie: MovieResponse) {
        viewModelScope.launch(dispatcher) {
            if (movie.isFavorite) {
                favoriteRepository.removeFavorite(movie.id)
            } else {
                val favoriteMovie = FavoriteMovie(
                    id = movie.id,
                    title = movie.title,
                    posterPath = movie.posterPath,
                    voteAverage = movie.voteAverage,
                    overview = movie.overview,
                    genres = movie.genreNames?.joinToString(", ")
                )
                favoriteRepository.saveFavorite(favoriteMovie)
            }
        }
    }
}
