package com.mxvier.movies.home.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.mxvier.core.ui.theme.MovieFluxTheme
import com.mxvier.movies.home.presentation.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

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

    private fun navigateToMovieDetail(movieId: Int) {
        val deepLinkUri = "app://movies/detail/$movieId".toUri()
        findNavController().navigate(deepLinkUri)
    }
}
