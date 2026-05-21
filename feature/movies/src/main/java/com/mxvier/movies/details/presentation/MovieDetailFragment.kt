package com.mxvier.movies.details.presentation

import android.R
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.color.MaterialColors
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.mxvier.core.util.Constants
import com.mxvier.movies.databinding.FragmentMovieDetailBinding
import com.mxvier.movies.details.data.remote.response.MovieDetailResponse
import com.mxvier.movies.details.presentation.viewmodel.MovieDetailUiState
import com.mxvier.movies.details.presentation.viewmodel.MovieDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieDetailFragment : Fragment() {

    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding acessado fora do ciclo de vida da View")

    private val viewModel: MovieDetailViewModel by viewModels()
    private var movieId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            movieId = it.getInt("movieId", -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeUiState()
        observeFavoriteStatus()

        if (movieId != -1) {
            viewModel.fetchMovieDetails(movieId)
        } else {
            Toast.makeText(requireContext(), getString(com.mxvier.movies.R.string.movies_error_invalid_movie_id), Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    private fun setupListeners() {
        binding.moviesToolbarDetail.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.moviesBtnRetryDetail.setOnClickListener {
            if (movieId != -1) viewModel.fetchMovieDetails(movieId)
        }
        binding.moviesBtnFavoriteContent.setOnClickListener {
            val state = viewModel.uiState.value
            if (state is MovieDetailUiState.Success) {
                viewModel.toggleFavorite(state.movie)
            }
        }
    }

    private fun observeFavoriteStatus() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isFavorite.collect { isFav ->
                    val (icon, text) = if (isFav) {
                        Pair(android.R.drawable.btn_star_big_on, getString(com.mxvier.movies.R.string.movies_detail_remove_favorite))
                    } else {
                        Pair(android.R.drawable.btn_star_big_off, getString(com.mxvier.movies.R.string.movies_detail_add_favorite))
                    }
                    
                    binding.moviesBtnFavoriteContent.icon = ContextCompat.getDrawable(requireContext(), icon)
                    binding.moviesBtnFavoriteContent.text = text
                }
            }
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    handleUiState(state)
                }
            }
        }
    }

    private fun handleUiState(state: MovieDetailUiState) {
        binding.moviesProgressDetail.isVisible = state is MovieDetailUiState.Loading
        binding.moviesContainerDetail.isVisible = state is MovieDetailUiState.Success
        binding.moviesLayoutErrorDetail.isVisible = state is MovieDetailUiState.Error

        if (state is MovieDetailUiState.Success) {
            val movie = state.movie

            binding.moviesIvMoviePoster.contentDescription = getString(com.mxvier.movies.R.string.movies_poster_content_description, movie.title)

            binding.moviesTvDetailToolbarTitle.text = getString(com.mxvier.movies.R.string.movies_detail_toolbar_label)
            binding.moviesTvMovieTitle.text = movie.title
            binding.moviesTvVoteAverage.text = String.format("★ %.1f", movie.voteAverage)
            binding.moviesTvOverview.text = movie.overview

            movie.genres?.let { genres ->
                if (genres.isNotEmpty()) {
                    binding.moviesTvMovieGenres.isVisible = true
                    binding.moviesTvMovieGenres.text = genres.joinToString { it.name }
                }
            }

            val imageUrl = "${Constants.TMDB_IMAGE_BASE_URL}${movie.posterPath}"
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.progress_horizontal)
                .error(R.drawable.ic_menu_gallery)
                .into(binding.moviesIvMoviePoster)

            setupShareButton(movie)
        }

        if (state is MovieDetailUiState.Error) {
            binding.moviesTvErrorDetailMessage.text = state.message
            binding.moviesTvDetailToolbarTitle.text = getString(com.mxvier.movies.R.string.movies_error_label)
            binding.moviesToolbarDetail.menu.clear()
        }
    }

    private fun setupShareButton(movie: MovieDetailResponse) {
        val toolbar = binding.moviesToolbarDetail
        toolbar.menu.clear()

        val shareItem = toolbar.menu.add(0, 1, 0, getString(com.mxvier.movies.R.string.movies_detail_share_label))
        shareItem.setIcon(android.R.drawable.ic_menu_share)
        shareItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        
        val iconColor = MaterialColors.getColor(toolbar, com.google.android.material.R.attr.colorOnPrimary)
        shareItem.iconTintList = ColorStateList.valueOf(iconColor)

        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == 1) {
                shareMovie(movie.title, movie.overview, movieId)
                true
            } else {
                false
            }
        }
    }

    private fun shareMovie(title: String, overview: String, movieId: Int) {
        val movieUrl = "${Constants.TMDB_MOVIE_DETAILS_URL}$movieId"
        val shareText = """
        🎬 *$title*
        
        📝 $overview
        
        🔗 Veja mais em: $movieUrl
        
        Enviado via MovieFlux
    """.trimIndent()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, getString(com.mxvier.movies.R.string.movies_detail_share_chooser_title))
        startActivity(shareIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}