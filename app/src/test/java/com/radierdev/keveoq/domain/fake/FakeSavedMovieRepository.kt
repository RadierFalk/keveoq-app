package com.radierdev.keveoq.domain.fake

import com.radierdev.keveoq.core.result.AppError
import com.radierdev.keveoq.core.result.AppResult
import com.radierdev.keveoq.domain.model.SavedMovie
import com.radierdev.keveoq.domain.model.WatchStatus
import com.radierdev.keveoq.domain.repository.SavedMovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/** Lista "Quero assistir" em memória, com o mesmo comportamento esperado do Room. */
class FakeSavedMovieRepository : SavedMovieRepository {

    private val movies = MutableStateFlow<Map<Int, SavedMovie>>(emptyMap())

    /** Quando preenchido, a leitura pontual (`getSavedMovie`) falha com este erro. */
    var readFailure: AppError? = null

    /** Quando preenchido, toda escrita falha com este erro e nada é alterado. */
    var writeFailure: AppError? = null

    var saveCalls = 0
        private set
    var updateStatusCalls = 0
        private set
    var updateRatingCalls = 0
        private set

    fun stored(movieId: Int): SavedMovie? = movies.value[movieId]

    /** Coloca um filme na lista sem contar como chamada do caso de uso. */
    fun givenSaved(movie: SavedMovie) {
        movies.value = movies.value + (movie.movieId to movie)
    }

    override fun observeSavedMovies(): Flow<List<SavedMovie>> =
        movies.map { all -> all.values.sortedByDescending { it.savedAt } }

    override fun observeSavedMovie(movieId: Int): Flow<SavedMovie?> =
        movies.map { it[movieId] }

    override fun observeWatchedIds(): Flow<Set<Int>> =
        movies.map { all ->
            all.values.filter { it.status == WatchStatus.WATCHED }.map { it.movieId }.toSet()
        }

    override suspend fun getSavedMovie(movieId: Int): AppResult<SavedMovie?> {
        readFailure?.let { return AppResult.Failure(it) }
        return AppResult.Success(movies.value[movieId])
    }

    override suspend fun save(movie: SavedMovie): AppResult<Unit> {
        saveCalls++
        return write { givenSaved(movie) }
    }

    override suspend fun updateStatus(movieId: Int, status: WatchStatus): AppResult<Unit> {
        updateStatusCalls++
        return write { update(movieId) { it.copy(status = status) } }
    }

    override suspend fun updateRating(movieId: Int, rating: Int): AppResult<Unit> {
        updateRatingCalls++
        return write { update(movieId) { it.copy(rating = rating) } }
    }

    private fun write(change: () -> Unit): AppResult<Unit> {
        writeFailure?.let { return AppResult.Failure(it) }
        change()
        return AppResult.Success(Unit)
    }

    private fun update(movieId: Int, change: (SavedMovie) -> SavedMovie) {
        val movie = movies.value[movieId] ?: return
        movies.value = movies.value + (movieId to change(movie))
    }
}
