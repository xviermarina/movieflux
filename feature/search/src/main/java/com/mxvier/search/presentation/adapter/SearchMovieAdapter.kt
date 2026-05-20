package com.mxvier.search.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mxvier.search.databinding.SearchItemMovieBinding
import com.mxvier.search.domain.model.Movie

class SearchMovieAdapter(
    private val onMovieClick: (Int) -> Unit
) : ListAdapter<Movie, SearchMovieAdapter.SearchMovieViewHolder>(MovieDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchMovieViewHolder {
        val binding = SearchItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchMovieViewHolder(binding, onMovieClick)
    }

    override fun onBindViewHolder(holder: SearchMovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SearchMovieViewHolder(
        private val binding: SearchItemMovieBinding,
        private val onMovieClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.tvMovieItemTitle.text = movie.title

            Glide.with(binding.root.context)
                .load("https://image.tmdb.org/t/p/w500${movie.posterPath}")
                .into(binding.ivMoviePoster)

            binding.root.setOnClickListener { onMovieClick(movie.id) }
        }
    }

    private class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem == newItem
    }
}