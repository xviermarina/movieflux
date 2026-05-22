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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.mxvier.core.ui.theme.MovieFluxTheme

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private val viewModel: FavoritesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MovieFluxTheme {
                    FavoritesScreen(
                        viewModel = viewModel,
                        onBackClick = { findNavController().popBackStack() },
                        onMovieClick = { navigateToMovieDetail(it) }
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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