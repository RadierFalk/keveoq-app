package com.radierdev.keveoq.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tabela `movie_details`: detalhes completos, guardados para consulta offline (RF07).
 *
 * - `genres`: lista de nomes gravada como texto JSON pelo `Converters`.
 * - `trailerKey`: chave do YouTube, ou `null` se o filme não tem trailer válido.
 * - `updatedAt` (epoch ms): momento da busca, para a validade de 24 h.
 * - `watchProviders` (versão 2): onde assistir no Brasil, em JSON. O valor padrão
 *   `{}` precisa ser igual ao da migração 1→2, porque o Room confere o esquema.
 * - `theatricalReleaseBr` (versão 2): estreia nos cinemas do Brasil, em dias desde
 *   1970-01-01 (`LocalDate.toEpochDay`), ou `null`.
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
    @ColumnInfo(defaultValue = "{}") val watchProviders: StoredWatchProviders = StoredWatchProviders(),
    val theatricalReleaseBr: Long? = null,
)
