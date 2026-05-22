package com.mxvier.search.domain.model

import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("poster_path")
    val posterPath: String?,

    @SerializedName("vote_average")
    val voteAverage: Double,

    @SerializedName("overview")
    val overview: String?,

    @SerializedName("release_date")
    val releaseDate: String?,

    @SerializedName("genre_ids")
    val genreIds: List<Int>? = null,

    var genreNames: List<String>? = null,

    var isFavorite: Boolean = false
) {
    /**
     * Gera um template formatado para compartilhamento dos detalhes do filme.
     */
    fun getShareTemplate(movieUrl: String): String {
        val description = if (overview.isNullOrBlank()) "Nenhuma descrição disponível." else overview
        return """
            🎬 *$title*

            📝 $description

            🔗 Veja mais em: $movieUrl

            Enviado via MovieFlux
        """.trimIndent()
    }
}
