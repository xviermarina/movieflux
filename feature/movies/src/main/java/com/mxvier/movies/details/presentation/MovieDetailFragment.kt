package com.mxvier.movies.details.presentation

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.mxvier.core.ui.theme.MovieFluxTheme
import com.mxvier.core.util.Constants
import com.mxvier.movies.details.presentation.viewmodel.MovieDetailUiState
import com.mxvier.movies.details.presentation.viewmodel.MovieDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class MovieDetailFragment : Fragment() {

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
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MovieFluxTheme {
                    MovieDetailScreen(
                        viewModel = viewModel,
                        onBackClick = { findNavController().popBackStack() },
                        onShareClick = { 
                            val state = viewModel.uiState.value
                            if (state is MovieDetailUiState.Success) {
                                shareMovie(state.movie.title, state.movie.overview, movieId, state.movie.posterPath)
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (movieId != -1) {
            viewModel.fetchMovieDetails(movieId)
        } else {
            Toast.makeText(requireContext(), getString(com.mxvier.movies.R.string.movies_error_invalid_movie_id), Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    private fun shareMovie(title: String, overview: String, movieId: Int, posterPath: String?) {
        val movieUrl = "${Constants.TMDB_MOVIE_DETAILS_URL}$movieId"
        val shareText = """
        🎬 *$title*
        
        📝 $overview
        
        🔗 Veja mais em: $movieUrl
        
        Enviado via MovieFlux
    """.trimIndent()

        if (posterPath != null) {
            val imageUrl = "${Constants.TMDB_IMAGE_BASE_URL}$posterPath"

            Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        val uri = saveImageToCache(resource, "movie_poster_$movieId.png")
                        if (uri != null) {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                putExtra(Intent.EXTRA_STREAM, uri)
                                type = "image/png"
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            val shareIntent = Intent.createChooser(sendIntent, getString(com.mxvier.movies.R.string.movies_detail_share_chooser_title))
                            startActivity(shareIntent)
                        } else {
                            shareTextOnly(shareText)
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        shareTextOnly(shareText)
                    }
                })
        } else {
            shareTextOnly(shareText)
        }
    }

    private fun shareTextOnly(shareText: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, getString(com.mxvier.movies.R.string.movies_detail_share_chooser_title))
        startActivity(shareIntent)
    }

    private fun saveImageToCache(bitmap: Bitmap, fileName: String): android.net.Uri? {
        return try {
            val cachePath = File(requireContext().cacheDir, "shared_images")
            cachePath.mkdirs()
            val file = File(cachePath, fileName)
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()
            FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
