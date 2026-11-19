package com.radierdev.keveoq.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.radierdev.keveoq.domain.model.Mood

/**
 * Tabela `mood_recommendations`: quais filmes pertencem às recomendações de cada humor.
 *
 * - Chave `(mood, movieId)`: o mesmo filme não aparece duas vezes no mesmo humor (RN04).
 * - `position`: ordem de popularidade devolvida pela API.
 * - `updatedAt` (epoch ms): todas as linhas de um humor recebem o mesmo instante,
 *   que é a data do cache (RN05).
 * - O enum [Mood] é gravado pelo nome ("HAPPY", "SAD"...), conversão nativa do Room.
 */
@Entity(
    tableName = "mood_recommendations",
    primaryKeys = ["mood", "movieId"],
    foreignKeys = [
        ForeignKey(
            entity = MovieEntity::class,
            parentColumns = ["id"],
            childColumns = ["movieId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("movieId")],
)
data class MoodRecommendationEntity(
    val mood: Mood,
    val movieId: Int,
    val position: Int,
    val updatedAt: Long,
)

/** Resultado da consulta que junta `mood_recommendations` com `movies`. */
data class RecommendedMovieRow(
    @Embedded val movie: MovieEntity,
    val position: Int,
    val updatedAt: Long,
)
