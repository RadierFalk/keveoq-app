package com.radierdev.keveoq.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.radierdev.keveoq.data.local.entity.SavedMovieEntity
import com.radierdev.keveoq.domain.model.WatchStatus
import kotlinx.coroutines.flow.Flow

/** Acesso à lista "Quero assistir". */
@Dao
interface SavedMovieDao {

    @Query("SELECT * FROM saved_movies ORDER BY savedAt DESC")
    fun observeAll(): Flow<List<SavedMovieEntity>>

    @Query("SELECT * FROM saved_movies WHERE movieId = :movieId")
    fun observe(movieId: Int): Flow<SavedMovieEntity?>

    @Query("SELECT movieId FROM saved_movies WHERE status = :status")
    fun observeIdsByStatus(status: WatchStatus): Flow<List<Int>>

    @Query("SELECT * FROM saved_movies WHERE movieId = :movieId")
    suspend fun get(movieId: Int): SavedMovieEntity?

    /** ABORT: inserir um filme que já existe lança erro, em vez de sobrescrever (RN06). */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(movie: SavedMovieEntity)

    @Query("UPDATE saved_movies SET status = :status WHERE movieId = :movieId")
    suspend fun updateStatus(movieId: Int, status: WatchStatus)

    @Query("UPDATE saved_movies SET rating = :rating WHERE movieId = :movieId")
    suspend fun updateRating(movieId: Int, rating: Int)
}
