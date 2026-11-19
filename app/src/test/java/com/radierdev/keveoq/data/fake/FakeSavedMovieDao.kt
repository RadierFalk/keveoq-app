package com.radierdev.keveoq.data.fake

import com.radierdev.keveoq.data.local.dao.SavedMovieDao
import com.radierdev.keveoq.data.local.entity.SavedMovieEntity
import com.radierdev.keveoq.domain.model.WatchStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/** DAO falso em memória que imita as consultas e restrições do Room. */
class FakeSavedMovieDao : SavedMovieDao {

    private val movies = MutableStateFlow<Map<Int, SavedMovieEntity>>(emptyMap())

    /** Quando preenchido, toda escrita lança esta exceção (simula falha do banco). */
    var writeError: Exception? = null

    override fun observeAll(): Flow<List<SavedMovieEntity>> =
        movies.map { all -> all.values.sortedByDescending { it.savedAt } }

    override fun observe(movieId: Int): Flow<SavedMovieEntity?> = movies.map { it[movieId] }

    override fun observeIdsByStatus(status: WatchStatus): Flow<List<Int>> =
        movies.map { all -> all.values.filter { it.status == status }.map { it.movieId } }

    override suspend fun get(movieId: Int): SavedMovieEntity? = movies.value[movieId]

    override suspend fun insert(movie: SavedMovieEntity) {
        writeError?.let { throw it }
        // Igual ao OnConflictStrategy.ABORT do Room: chave repetida é erro.
        check(movie.movieId !in movies.value) { "UNIQUE constraint failed: saved_movies.movieId" }
        movies.value = movies.value + (movie.movieId to movie)
    }

    override suspend fun updateStatus(movieId: Int, status: WatchStatus) {
        writeError?.let { throw it }
        movies.value[movieId]?.let { movies.value = movies.value + (movieId to it.copy(status = status)) }
    }

    override suspend fun updateRating(movieId: Int, rating: Int) {
        writeError?.let { throw it }
        movies.value[movieId]?.let { movies.value = movies.value + (movieId to it.copy(rating = rating)) }
    }
}
