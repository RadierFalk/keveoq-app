package com.radierdev.keveoq.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tabela `movie_details`: detalhes completos, guardados para consulta offline (RF07).
 *
 * - `genres`: lista de nomes gravada como texto JSON pelo `Converters`.
 * - `trailerKey`: chave do YouTube, ou `null` se o filme não tem trailer válido.
 * - `updatedAt` (epoch ms): momento da busca, para a validade de 24 h.
 */
@Entity(tableName = "movie_details")
data class MovieDetailsEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val voteAverage: Double,
    val releaseYear: Int?,
    val runtimeMinutes: Int?,
    val genres: List<String>,
    val trailerKey: String?,
    val updatedAt: Long,
)
