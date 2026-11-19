package com.radierdev.keveoq.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tabela `movies`: dados resumidos de cada filme recomendado.
 * Um mesmo filme pode aparecer em vários humores; a ligação fica em `mood_recommendations`.
 */
@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val posterUrl: String?,
    val releaseYear: Int?,
    val voteAverage: Double,
    val voteCount: Int,
)
