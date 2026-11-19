package com.radierdev.keveoq.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.radierdev.keveoq.domain.model.WatchStatus

/**
 * Tabela `saved_movies`: a lista "Quero assistir" (dados só locais).
 *
 * - `movieId` é a chave primária: o banco impede duplicidade (RN06),
 *   além da checagem feita no caso de uso.
 * - Título, pôster e ano são copiados, para a lista não depender do cache de recomendações.
 * - `status` é gravado pelo nome do enum ("WANT_TO_WATCH" ou "WATCHED").
 */
@Entity(tableName = "saved_movies")
data class SavedMovieEntity(
    @PrimaryKey val movieId: Int,
    val title: String,
    val posterUrl: String?,
    val releaseYear: Int?,
    val status: WatchStatus,
    val rating: Int?,
    val savedAt: Long,
)
