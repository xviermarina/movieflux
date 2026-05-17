package com.mxvier.movies.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.mxvier.movies.databinding.FragmentHomeBinding
import com.mxvier.movies.presentation.viewmodel.HomeUiState
import com.mxvier.movies.presentation.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding acessado fora do ciclo de vida da View")

    private val viewModel: HomeViewModel by viewModels()
    private val movieAdapter by lazy { HomeMovieAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        observeUiState()
    }

    private fun setupRecyclerView() {
        binding.rvMovies.adapter = movieAdapter
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
                binding.rvMovies.isVisible = false
                binding.layoutError.isVisible = false
            }
            is HomeUiState.Success -> {
                binding.progressBar.isVisible = false
                binding.rvMovies.isVisible = true
                binding.layoutError.isVisible = false
                movieAdapter.submitList(state.movies)
            }
            is HomeUiState.Error -> {
                binding.progressBar.isVisible = false
                binding.rvMovies.isVisible = false
                binding.layoutError.isVisible = true
                binding.tvErrorMessage.text = state.message
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}