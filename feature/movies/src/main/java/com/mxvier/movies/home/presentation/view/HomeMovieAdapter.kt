package com.mxvier.movies.home.presentation.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mxvier.core.util.Constants
import com.mxvier.movies.databinding.ItemMovieBinding
import com.mxvier.movies.home.data.remote.response.MovieResponse
import androidx.core.view.isVisible

class HomeMovieAdapter(
    private val onMovieClick: (movieId: Int) -> Unit,
    private val onFavoriteClick: (movie: MovieResponse) -> Unit
) : ListAdapter<MovieResponse, HomeMovieAdapter.MovieViewHolder>(MovieDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieViewHolder(binding, onMovieClick, onFavoriteClick)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MovieViewHolder(
        private val binding: ItemMovieBinding,
        private val onMovieClick: (movieId: Int) -> Unit,
        private val onFavoriteClick: (movie: MovieResponse) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: MovieResponse) {
            binding.root.setOnClickListener {
                onMovieClick(movie.id)
            }

            binding.moviesIvFavoriteStar.setOnClickListener {
                onFavoriteClick(movie)
            }

            binding.moviesTvMovieTitleItem.text = movie.title.trim()
            binding.moviesTvMovieGenresItem.text = movie.genreNames?.joinToString(", ") ?: ""
            
            binding.moviesIvFavoriteStar.setImageResource(android.R.drawable.btn_star_big_on)
            binding.moviesIvFavoriteStar.isVisible = movie.isFavorite

            val imageUrl = "${Constants.TMDB_IMAGE_BASE_URL}${movie.posterPath}"
            Glide.with(binding.root.context)
                .load(imageUrl)
                .centerCrop()
                .placeholder(android.R.drawable.progress_horizontal)
                .error(android.R.drawable.ic_menu_gallery)
                .into(binding.moviesIvMoviePosterItem)
        }
    }

    class MovieDiffCallback : DiffUtil.ItemCallback<MovieResponse>() {
        override fun areItemsTheSame(oldItem: MovieResponse, newItem: MovieResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MovieResponse, newItem: MovieResponse): Boolean {
            return oldItem.id == newItem.id &&
                    oldItem.title == newItem.title &&
                    oldItem.isFavorite == newItem.isFavorite
        }
    }
}
