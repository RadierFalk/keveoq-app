package com.radierdev.keveoq.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.radierdev.keveoq.data.local.entity.MoodRecommendationEntity
import com.radierdev.keveoq.data.local.entity.MovieDetailsEntity
import com.radierdev.keveoq.data.local.entity.MovieEntity
import com.radierdev.keveoq.data.local.entity.RecommendedMovieRow
import com.radierdev.keveoq.domain.model.Mood
import kotlinx.coroutines.flow.Flow

/**
 * Acesso às recomendações e aos detalhes dos filmes.
 *
 * É uma classe abstrata (e não interface) para poder ter o método
 * [replaceRecommendations] com corpo, executado como uma única transação.
 */
@Dao
abstract class MovieDao {

    /** Filmes recomendados para um humor, na ordem de popularidade. */
    @Query(
        """
        SELECT movies.*, mood_recommendations.position, mood_recommendations.updatedAt
        FROM mood_recommendations
        INNER JOIN movies ON movies.id = mood_recommendations.movieId
        WHERE mood_recommendations.mood = :mood
        ORDER BY mood_recommendations.position
        """,
    )
    abstract fun observeRecommendations(mood: Mood): Flow<List<RecommendedMovieRow>>

    /**
     * Upsert (insere ou atualiza) em vez de REPLACE: o REPLACE apaga a linha antiga
     * antes de inserir, e o CASCADE da chave estrangeira apagaria junto as
     * recomendações desse filme nos outros humores.
     */
    @Upsert
    abstract suspend fun upsertMovies(movies: List<MovieEntity>)

    @Query("DELETE FROM mood_recommendations WHERE mood = :mood")
    abstract suspend fun deleteRecommendations(mood: Mood)

    @Upsert
    abstract suspend fun upsertRecommendations(recommendations: List<MoodRecommendationEntity>)

    /**
     * Substitui as recomendações de um humor em uma única transação:
     * a tela nunca vê a lista pela metade, e se algo falhar nada muda.
     */
    @Transaction
    open suspend fun replaceRecommendations(
        mood: Mood,
        movies: List<MovieEntity>,
        recommendations: List<MoodRecommendationEntity>,
    ) {
        upsertMovies(movies)
        deleteRecommendations(mood)
        upsertRecommendations(recommendations)
    }

    @Query("SELECT * FROM movie_details WHERE id = :movieId")
    abstract fun observeMovieDetails(movieId: Int): Flow<MovieDetailsEntity?>

    @Upsert
    abstract suspend fun upsertMovieDetails(details: MovieDetailsEntity)
}
