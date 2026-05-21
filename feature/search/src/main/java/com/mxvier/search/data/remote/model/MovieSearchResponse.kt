package com.mxvier.search.data.remote.model

import com.google.gson.annotations.SerializedName
import com.mxvier.search.domain.model.Movie

data class MovieSearchResponse(
    @SerializedName("page")
    val page: Int,

    @SerializedName("results")
    val results: List<Movie>,

    @SerializedName("total_results")
    val totalResults: Int,

    @SerializedName("total_pages")
    val totalPages: Int
)