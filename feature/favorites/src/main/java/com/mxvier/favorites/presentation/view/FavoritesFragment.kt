package com.mxvier.favorites.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.content.Context
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.mxvier.favorites.databinding.FragmentFavoritesBinding
import com.mxvier.favorites.presentation.viewmodel.FavoritesUiState
import com.mxvier.favorites.presentation.viewmodel.FavoritesViewModel
import com.mxvier.favorites.presentation.adapter.FavoriteMovieAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding acessado fora do ciclo de vida da View")

    private val viewModel: FavoritesViewModel by viewModels()
    
    private val movieAdapter by lazy {
        FavoriteMovieAdapter(
            onMovieClick = ::navigateToMovieDetail,
            onFavoriteClick = { movieId -> viewModel.removeFavorite(movieId) }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupToolbar()
        observeUiState()
    }

    private fun setupToolbar() {
        binding.favoritesToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        binding.favoritesRvMovies.adapter = movieAdapter
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

    private fun handleUiState(state: FavoritesUiState) {
        binding.favoritesRvMovies.isVisible = state is FavoritesUiState.Success
        binding.favoritesLayoutEmpty.isVisible = state is FavoritesUiState.Empty

        when (state) {
            is FavoritesUiState.Success -> {
                movieAdapter.submitList(state.movies)
            }
            is FavoritesUiState.Error -> {
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
            }
            else -> { /* Loading handled by generic logic */ }
        }
    }

    private fun navigateToMovieDetail(movieId: Int) {
        if (!isInternetAvailable()) {
            Toast.makeText(requireContext(), "Ocorreu um erro ao carregar os detalhes", Toast.LENGTH_SHORT).show()
            return
        }
        val deepLinkUri = "app://movies/detail/$movieId".toUri()
        try {
            findNavController().navigate(deepLinkUri)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), com.mxvier.favorites.R.string.favorites_error_open_details, Toast.LENGTH_SHORT).show()
        }
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}