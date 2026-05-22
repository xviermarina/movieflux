package com.mxvier.search.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mxvier.core.ui.theme.MovieFluxTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MovieFluxTheme {
                    SearchScreen(
                        viewModel = viewModel,
                        onBackClick = { findNavController().popBackStack() },
                        onMovieClick = { navigateToMovieDetail(it) }
                    )
                }
            }
        }
    }

    private fun navigateToMovieDetail(movieId: Int) {
        val deepLinkUri = "app://movies/detail/$movieId".toUri()
        findNavController().navigate(deepLinkUri)
    }
}
