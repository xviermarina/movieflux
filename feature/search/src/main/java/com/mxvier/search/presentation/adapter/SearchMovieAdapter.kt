package com.mxvier.search.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mxvier.core.util.Constants
import com.mxvier.search.databinding.SearchItemMovieBinding
import com.mxvier.search.domain.model.Movie
import androidx.core.view.isVisible

class SearchMovieAdapter(
    private val onMovieClick: (Int) -> Unit,
    private val onFavoriteClick: (Movie) -> Unit
) : ListAdapter<Movie, SearchMovieAdapter.SearchMovieViewHolder>(MovieDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchMovieViewHolder {
        val binding = SearchItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchMovieViewHolder(binding, onMovieClick, onFavoriteClick)
    }

    override fun onBindViewHolder(holder: SearchMovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SearchMovieViewHolder(
        private val binding: SearchItemMovieBinding,
        private val onMovieClick: (Int) -> Unit,
        private val onFavoriteClick: (Movie) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.searchTvMovieTitleItem.text = movie.title
            binding.searchTvMovieGenresItem.text = movie.genreNames?.joinToString(", ") ?: ""

            val (starIcon, starCd) = if (movie.isFavorite) {
                Pair(android.R.drawable.btn_star_big_on, binding.root.context.getString(com.mxvier.search.R.string.search_favorite_star_active_cd, movie.title))
            } else {
                Pair(android.R.drawable.btn_star_big_off, binding.root.context.getString(com.mxvier.search.R.string.search_favorite_star_inactive_cd, movie.title))
            }
            binding.searchIvFavoriteStar.setImageResource(starIcon)
            binding.searchIvFavoriteStar.contentDescription = starCd
            binding.searchIvFavoriteStar.isVisible = movie.isFavorite

            binding.searchIvMoviePosterItem.contentDescription = binding.root.context.getString(com.mxvier.search.R.string.search_poster_content_description, movie.title)
            
            binding.searchIvFavoriteStar.setOnClickListener {
                onFavoriteClick(movie)
            }

            Glide.with(binding.root.context)
                .load("${Constants.TMDB_IMAGE_BASE_URL}${movie.posterPath}")
                .placeholder(android.R.drawable.progress_horizontal)
                .error(android.R.drawable.ic_menu_gallery)
                .into(binding.searchIvMoviePosterItem)

            binding.root.setOnClickListener { onMovieClick(movie.id) }
        }
    }

    private class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id &&
                    oldItem.title == newItem.title &&
                    oldItem.isFavorite == newItem.isFavorite
        }
    }
}
