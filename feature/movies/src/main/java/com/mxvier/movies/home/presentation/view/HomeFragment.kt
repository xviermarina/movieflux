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
        HomeMovieAdapter(
            onMovieClick = ::navigateToMovieDetail,
            onFavoriteClick = { movie -> viewModel.toggleFavorite(movie) }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.moviesToolbarHome.title = ""

        setupToolbar()
        setupRecyclerView()
        setupListeners()
        observeUiState()
    }

    private fun setupToolbar() {
        binding.moviesToolbarHome.inflateMenu(R.menu.home_menu)
        binding.moviesToolbarHome.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_search -> {
                    val searchUri = "app://movies/search".toUri()
                    findNavController().navigate(searchUri)
                    true
                }
                R.id.action_favorites -> {
                    val favoritesUri = "app://movies/favorites".toUri()
                    findNavController().navigate(favoritesUri)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        val gridLayoutManager = binding.moviesRvMovies.layoutManager as GridLayoutManager

        binding.moviesRvMovies.adapter = movieAdapter
        binding.moviesRvMovies.addOnScrollListener(
            EndlessScrollListener(gridLayoutManager) {
                viewModel.fetchMovies()
            }
        )
    }

    private fun setupListeners() {
        binding.moviesBtnRetry.setOnClickListener {
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
                binding.moviesProgressBar.isVisible = true
                binding.moviesProgressPaging.isVisible = false
                binding.moviesRvMovies.isVisible = false
                binding.moviesLayoutError.isVisible = false
            }
            is HomeUiState.Success -> {
                binding.moviesProgressBar.isVisible = false
                binding.moviesProgressPaging.isVisible = state.isPagingLoading
                binding.moviesRvMovies.isVisible = true
                binding.moviesLayoutError.isVisible = false

                movieAdapter.submitList(state.movies)
            }
            is HomeUiState.Error -> {
                binding.moviesProgressBar.isVisible = false
                binding.moviesProgressPaging.isVisible = false

                val hasCachedMovies = state.accumulatedMovies.isNotEmpty()
                binding.moviesRvMovies.isVisible = hasCachedMovies
                binding.moviesLayoutError.isVisible = !hasCachedMovies

                if (hasCachedMovies) {
                    movieAdapter.submitList(state.accumulatedMovies)
                } else {
                    binding.moviesTvErrorMessage.text = state.message
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
                    getString(R.string.movies_error_navigating_to_details),
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