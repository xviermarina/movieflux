package com.mxvier.movies.home.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.mxvier.movies.R
import com.mxvier.movies.databinding.FragmentHomeBinding
import com.mxvier.movies.home.presentation.viewmodel.HomeUiState
import com.mxvier.movies.home.presentation.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding acessado fora do ciclo de vida da View")

    private val viewModel: HomeViewModel by viewModels()
    private val movieAdapter by lazy {
        HomeMovieAdapter(onMovieClick = ::navigateToMovieDetail)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarHome.title = ""

        setupRecyclerView()
        setupListeners()
        observeUiState()
    }

    private fun setupRecyclerView() {
        val gridLayoutManager = binding.rvMovies.layoutManager as GridLayoutManager

        binding.rvMovies.adapter = movieAdapter
        binding.rvMovies.addOnScrollListener(
            EndlessScrollListener(gridLayoutManager) {
                viewModel.fetchMovies()
            }
        )
    }

    private fun setupListeners() {
        binding.btnRetry.setOnClickListener {
            viewModel.fetchMovies()
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

    private fun handleUiState(state: HomeUiState) {
        when (state) {
            is HomeUiState.Loading -> {
                binding.progressBar.isVisible = true
                binding.progressPaging.isVisible = false
                binding.rvMovies.isVisible = false
                binding.layoutError.isVisible = false
            }
            is HomeUiState.Success -> {
                binding.progressBar.isVisible = false
                binding.progressPaging.isVisible = state.isPagingLoading
                binding.rvMovies.isVisible = true
                binding.layoutError.isVisible = false

                movieAdapter.submitList(state.movies)
            }
            is HomeUiState.Error -> {
                binding.progressBar.isVisible = false
                binding.progressPaging.isVisible = false

                val hasCachedMovies = state.accumulatedMovies.isNotEmpty()
                binding.rvMovies.isVisible = hasCachedMovies
                binding.layoutError.isVisible = !hasCachedMovies

                if (hasCachedMovies) {
                    movieAdapter.submitList(state.accumulatedMovies)
                } else {
                    binding.tvErrorMessage.text = state.message
                }
            }
        }
    }

    private fun navigateToMovieDetail(movieId: Int) {
        val deepLinkUri = "$DEEP_LINK_DETAIL$movieId".toUri()
        try {
            findNavController().navigate(deepLinkUri)
        } catch (e: Exception) {
            context?.let { ctx ->
                Toast.makeText(
                    ctx,
                    getString(R.string.error_navigating_to_details),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val DEEP_LINK_DETAIL = "app://movies/detail/"
    }
}