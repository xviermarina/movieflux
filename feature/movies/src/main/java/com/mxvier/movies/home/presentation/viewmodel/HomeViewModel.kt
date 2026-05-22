package com.mxvier.movies.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mxvier.core.di.IoDispatcher
import com.mxvier.core.security.SecurityManager
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
    private val securityManager: SecurityManager,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var accumulatedMovies = listOf<MovieResponse>()
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
                
                // Create new instances to trigger DiffUtil update
                accumulatedMovies = accumulatedMovies.map { movie ->
                    movie.copy(isFavorite = favoriteIds.contains(movie.id))
                }
                
                val currentState = _uiState.value
                if (currentState is HomeUiState.Success) {
                    _uiState.value = currentState.copy(movies = accumulatedMovies)
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
                movies = accumulatedMovies,
                isPagingLoading = true
            )
        }

        viewModelScope.launch(dispatcher) {
            try {
                ensureGenresLoaded()
                val newMoviesFromApi = repository.fetchPopularMovies(currentPage)

                if (newMoviesFromApi.isEmpty()) {
                    isLastPage = true
                    if (currentPage == 1) {
                        _uiState.value = HomeUiState.Empty
                    } else {
                        _uiState.value = HomeUiState.Success(
                            movies = accumulatedMovies,
                            isPagingLoading = false
                        )
                    }
                } else {
                    val processedNewMovies = newMoviesFromApi.map { movie ->
                        movie.copy(
                            genreNames = movie.genreIds?.mapNotNull { id ->
                                genres.find { it.id == id }?.name
                            },
                            isFavorite = favoriteIds.contains(movie.id)
                        )
                    }
                    
                    accumulatedMovies = accumulatedMovies + processedNewMovies
                    
                    _uiState.value = HomeUiState.Success(
                        movies = accumulatedMovies,
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
                        accumulatedMovies = accumulatedMovies
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

    fun logout() {
        securityManager.logout()
    }
}
