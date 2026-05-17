package com.mxvier.movies.presentation.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mxvier.movies.data.remote.response.MovieResponse
import com.mxvier.movies.databinding.ItemMovieBinding
import java.util.Locale

class HomeMovieAdapter: ListAdapter<MovieResponse, HomeMovieAdapter.MovieViewHolder>(MovieDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


class MovieViewHolder(
    private val binding: ItemMovieBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(movie: MovieResponse) {
        binding.tvMovieTitle.text = movie.title
        String.format(Locale.getDefault(), "%.1f", movie.voteAverage).also { binding.tvMovieRating.text = it }

        val posterUrl = "https://image.tmdb.org/t/p/w500${movie.posterPath}"

        Glide.with(binding.root.context)
            .load(posterUrl)
            .centerCrop()
            .into(binding.ivMoviePoster)
    }
}
}

class MovieDiffCallback : DiffUtil.ItemCallback<MovieResponse>() {
    override fun areItemsTheSame(oldItem: MovieResponse, newItem: MovieResponse): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MovieResponse, newItem: MovieResponse): Boolean {
        return oldItem == newItem
    }
}