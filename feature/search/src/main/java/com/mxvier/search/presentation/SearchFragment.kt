package com.mxvier.search.presentation

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
import com.mxvier.search.databinding.FragmentSearchBinding
import com.mxvier.search.presentation.adapter.SearchMovieAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding acessado fora do ciclo de vida da View")

    private val viewModel: SearchViewModel by viewModels()
    private val movieAdapter by lazy {
        SearchMovieAdapter(
            onMovieClick = ::navigateToMovieDetail,
            onFavoriteClick = { movie -> viewModel.toggleFavorite(movie) }
        )
    }

    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchRvMovies.adapter = movieAdapter

        setupToolbar()
        setupSearchView()
        observeUiState()
    }

    private fun setupToolbar() {
        binding.searchToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchMovies(it) }
                binding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel()

                if (newText.isNullOrBlank()) {
                    return true
                }

                searchJob = viewLifecycleOwner.lifecycleScope.launch {
                    delay(500)
                    viewModel.searchMovies(newText)
                }
                return true
            }
        })
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

    private fun handleUiState(state: SearchUiState) {
        when (state) {
            is SearchUiState.Idle -> {
                binding.searchProgressBar.isVisible = false
                binding.searchLayoutError.isVisible = false
            }
            is SearchUiState.Loading -> {
                binding.searchProgressBar.isVisible = true
                binding.searchRvMovies.isVisible = false
                binding.searchLayoutError.isVisible = false
            }
            is SearchUiState.Success -> {
                binding.searchProgressBar.isVisible = false
                binding.searchRvMovies.isVisible = true
                binding.searchLayoutError.isVisible = false
                movieAdapter.submitList(state.movies)
            }
            is SearchUiState.Empty -> {
                binding.searchProgressBar.isVisible = false
                binding.searchRvMovies.isVisible = false
                binding.searchLayoutError.isVisible = true
                binding.searchTvErrorMessage.text = getString(com.mxvier.search.R.string.search_error_no_results)
            }
            is SearchUiState.Error -> {
                binding.searchProgressBar.isVisible = false
                binding.searchRvMovies.isVisible = false
                binding.searchLayoutError.isVisible = true
                binding.searchTvErrorMessage.text = state.message
            }
        }
    }

    private fun navigateToMovieDetail(movieId: Int) {
        val deepLinkUri = "app://movies/detail/$movieId".toUri()
        try {
            findNavController().navigate(deepLinkUri)
        } catch (e: Exception) {
            Toast.makeText(context, getString(com.mxvier.search.R.string.search_error_open_details), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}