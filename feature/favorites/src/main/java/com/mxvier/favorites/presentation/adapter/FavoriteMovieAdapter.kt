package com.mxvier.favorites.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mxvier.core.util.Constants
import com.mxvier.favorites.databinding.ItemFavoriteMovieBinding
import com.mxvier.data.movies.domain.model.FavoriteMovie

class FavoriteMovieAdapter(
    private val onMovieClick: (movieId: Int) -> Unit,
    private val onFavoriteClick: (movieId: Int) -> Unit
) : ListAdapter<FavoriteMovie, FavoriteMovieAdapter.FavoriteMovieViewHolder>(FavoriteMovieDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteMovieViewHolder {
        val binding = ItemFavoriteMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavoriteMovieViewHolder(binding, onMovieClick, onFavoriteClick)
    }

    override fun onBindViewHolder(holder: FavoriteMovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FavoriteMovieViewHolder(
        private val binding: ItemFavoriteMovieBinding,
        private val onMovieClick: (movieId: Int) -> Unit,
        private val onFavoriteClick: (movieId: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: FavoriteMovie) {
            binding.root.setOnClickListener {
                onMovieClick(movie.id)
            }

            binding.favoritesIvFavoriteStar.contentDescription = binding.root.context.getString(com.mxvier.favorites.R.string.favorites_favorite_star_active_cd, movie.title)
            binding.favoritesIvFavoriteStar.setOnClickListener {
                onFavoriteClick(movie.id)
            }

            binding.favoritesTvMovieTitleItem.text = movie.title.takeIf { it.isNotBlank() } ?: binding.root.context.getString(com.mxvier.favorites.R.string.favorites_info_not_available)
            binding.favoritesTvMovieGenresItem.text = movie.genres?.takeIf { it.isNotBlank() } ?: binding.root.context.getString(com.mxvier.favorites.R.string.favorites_info_not_available)

            binding.favoritesIvMoviePosterItem.contentDescription = binding.root.context.getString(com.mxvier.favorites.R.string.favorites_poster_content_description, movie.title)

            val imageUrl = "${Constants.TMDB_IMAGE_BASE_URL}${movie.posterPath}"
            Glide.with(binding.root.context)
                .load(imageUrl)
                .centerCrop()
                .placeholder(android.R.drawable.progress_horizontal)
                .error(android.R.drawable.ic_menu_gallery)
                .into(binding.favoritesIvMoviePosterItem)
        }
    }

    class FavoriteMovieDiffCallback : DiffUtil.ItemCallback<FavoriteMovie>() {
        override fun areItemsTheSame(oldItem: FavoriteMovie, newItem: FavoriteMovie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FavoriteMovie, newItem: FavoriteMovie): Boolean {
            return oldItem == newItem
        }
    }
}
