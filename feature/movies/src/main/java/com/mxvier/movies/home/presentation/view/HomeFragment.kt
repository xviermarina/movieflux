package com.mxvier.movies.home.presentation.view

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
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.mxvier.movies.R
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.mxvier.core.ui.theme.MovieFluxTheme

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MovieFluxTheme {
                    HomeScreen(
                        viewModel = viewModel,
                        onSearchClick = { navigateToSearch() },
                        onFavoritesClick = { navigateToFavorites() },
                        onLogoutClick = { handleLogout() },
                        onMovieClick = { navigateToMovieDetail(it) }
                    )
                }
            }
        }
    }

    private fun navigateToSearch() {
        val searchUri = "app://movies/search".toUri()
        findNavController().navigate(searchUri)
    }

    private fun navigateToFavorites() {
        val favoritesUri = "app://movies/favorites".toUri()
        findNavController().navigate(favoritesUri)
    }

    private fun handleLogout() {
        viewModel.logout()
        val loginUri = "app://auth/login".toUri()
        val navOptions = NavOptions.Builder()
            .setPopUpTo(findNavController().graph.id, true)
            .build()
        findNavController().navigate(loginUri, navOptions)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun navigateToMovieDetail(movieId: Int) {
        if (!isInternetAvailable()) {
            Toast.makeText(requireContext(), getString(R.string.movies_detail_error_loading), Toast.LENGTH_SHORT).show()
            return
        }
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

    companion object {
        private const val DEEP_LINK_DETAIL = "app://movies/detail/"
    }
}