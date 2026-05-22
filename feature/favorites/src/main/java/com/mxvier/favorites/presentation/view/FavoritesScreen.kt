package com.mxvier.favorites.presentation.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mxvier.core.util.Constants
import com.mxvier.data.movies.domain.model.FavoriteMovie
import com.mxvier.favorites.R
import com.mxvier.favorites.presentation.viewmodel.FavoritesUiState
import com.mxvier.favorites.presentation.viewmodel.FavoritesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onBackClick: () -> Unit,
    onMovieClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.favorites_toolbar_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is FavoritesUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is FavoritesUiState.Empty -> {
                    Text(
                        text = stringResource(R.string.favorites_empty_list),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is FavoritesUiState.Success -> {
                    FavoritesGrid(
                        movies = state.movies,
                        onMovieClick = onMovieClick,
                        onFavoriteClick = { viewModel.removeFavorite(it) }
                    )
                }
                is FavoritesUiState.Error -> {
                    Text(
                        text = state.message,
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun FavoritesGrid(
    movies: List<FavoriteMovie>,
    onMovieClick: (Int) -> Unit,
    onFavoriteClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(movies) { movie ->
            FavoriteMovieItem(
                movie = movie,
                onMovieClick = onMovieClick,
                onFavoriteClick = onFavoriteClick
            )
        }
    }
}

@Composable
fun FavoriteMovieItem(
    movie: FavoriteMovie,
    onMovieClick: (Int) -> Unit,
    onFavoriteClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMovieClick(movie.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = "${Constants.TMDB_IMAGE_BASE_URL}${movie.posterPath}",
                    contentDescription = movie.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = { onFavoriteClick(movie.id) },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Remove Favorite",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = movie.genres ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
