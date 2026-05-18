package com.mxvier.movies.details.data.remote.response

import com.google.gson.annotations.SerializedName

class GenreDetailResponse (
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)