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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
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

        if (movieId != -1) {
            viewModel.fetchMovieDetails(movieId)
        } else {
            Toast.makeText(requireContext(), "Código do filme inválido", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    private fun setupListeners() {
        binding.toolbarDetail.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnRetryDetail.setOnClickListener {
            if (movieId != -1) viewModel.fetchMovieDetails(movieId)
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
        binding.progressDetail.isVisible = state is MovieDetailUiState.Loading
        binding.containerDetail.isVisible = state is MovieDetailUiState.Success
        binding.layoutErrorDetail.isVisible = state is MovieDetailUiState.Error

        if (state is MovieDetailUiState.Success) {
            val movie = state.movie

            binding.tvDetailToolbarTitle.text = "Detalhes do filme"
            binding.tvMovieTitle.text = movie.title
            binding.tvVoteAverage.text = String.format("★ %.1f", movie.voteAverage)
            binding.tvOverview.text = movie.overview

            val genreNames = movie.genres?.joinToString(", ") { it.name }
            binding.tvMovieGenres.apply {
                text = genreNames
                isVisible = !genreNames.isNullOrEmpty()
            }

            val imageUrl = "https://image.tmdb.org/t/p/w500${movie.posterPath}"
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.progress_horizontal)
                .error(R.drawable.ic_menu_gallery)
                .into(binding.ivMoviePoster)

            setupShareButton(movie)
        }

        if (state is MovieDetailUiState.Error) {
            binding.tvErrorDetailMessage.text = state.message
            binding.tvDetailToolbarTitle.text = "Erro"
            binding.toolbarDetail.menu.clear()
        }
    }

    private fun setupShareButton(movie: MovieDetailResponse) {
        val toolbar = binding.toolbarDetail
        toolbar.menu.clear()

        val shareItem = toolbar.menu.add(0, 1, 0, "Compartilhar")
        shareItem.setIcon(R.drawable.ic_menu_share)
        shareItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        shareItem.iconTintList = ColorStateList.valueOf(Color.parseColor("#FFC107"))

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
        val movieUrl = "https://www.themoviedb.org/movie/$movieId"
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

        val shareIntent = Intent.createChooser(sendIntent, "Compartilhar filme via")
        startActivity(shareIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}