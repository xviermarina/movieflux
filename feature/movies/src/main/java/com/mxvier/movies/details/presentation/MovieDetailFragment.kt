package com.mxvier.movies.details.presentation

import android.R
import android.os.Bundle
import android.view.LayoutInflater
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
        binding.scrollContainer.isVisible = state is MovieDetailUiState.Success
        binding.layoutErrorDetail.isVisible = state is MovieDetailUiState.Error

        if (state is MovieDetailUiState.Loading) {
            binding.tvDetailToolbarTitle.text = "Detalhes"
        }

        if (state is MovieDetailUiState.Success) {
            val movie = state.movie

            binding.tvDetailToolbarTitle.text = movie.title
            binding.tvMovieTitle.text = movie.title
            binding.tvVoteAverage.text = String.format("★ %.1f", movie.voteAverage)
            binding.tvOverview.text = movie.overview

            val imageUrl = "https://image.tmdb.org/t/p/w500${movie.posterPath}"
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.progress_horizontal)
                .error(R.drawable.ic_menu_gallery)
                .into(binding.ivMoviePoster)
        }

        if (state is MovieDetailUiState.Error) {
            binding.tvErrorDetailMessage.text = state.message
            binding.tvDetailToolbarTitle.text = "Erro"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}