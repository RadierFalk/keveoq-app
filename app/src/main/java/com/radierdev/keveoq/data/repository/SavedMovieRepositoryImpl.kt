package com.radierdev.keveoq.data.repository

import com.radierdev.keveoq.core.dispatcher.AppDispatchers
import com.radierdev.keveoq.core.result.AppResult
import com.radierdev.keveoq.data.local.dao.SavedMovieDao
import com.radierdev.keveoq.data.mapper.toDomain
import com.radierdev.keveoq.data.mapper.toEntity
import com.radierdev.keveoq.domain.model.SavedMovie
import com.radierdev.keveoq.domain.model.WatchStatus
import com.radierdev.keveoq.domain.repository.SavedMovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Implementação da lista "Quero assistir" com Room.
 *
 * Os dados são só locais (RNF05): toda operação é uma escrita no banco, e as
 * telas se atualizam pelos Flows. Não há servidor, logo não há conflito de sincronização.
 */
class SavedMovieRepositoryImpl(
    private val savedMovieDao: SavedMovieDao,
    private val dispatchers: AppDispatchers,
) : SavedMovieRepository {

    override fun observeSavedMovies(): Flow<List<SavedMovie>> =
        savedMovieDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun observeSavedMovie(movieId: Int): Flow<SavedMovie?> =
        savedMovieDao.observe(movieId).map { it?.toDomain() }

    override fun observeWatchedIds(): Flow<Set<Int>> =
        savedMovieDao.observeIdsByStatus(WatchStatus.WATCHED).map { it.toSet() }

    override suspend fun getSavedMovie(movieId: Int): AppResult<SavedMovie?> =
        withContext(dispatchers.io) {
            safeDbCall { savedMovieDao.get(movieId)?.toDomain() }
        }

    override suspend fun save(movie: SavedMovie): AppResult<Unit> =
        withContext(dispatchers.io) {
            safeDbCall { savedMovieDao.insert(movie.toEntity()) }
        }

    override suspend fun updateStatus(movieId: Int, status: WatchStatus): AppResult<Unit> =
        withContext(dispatchers.io) {
            safeDbCall { savedMovieDao.updateStatus(movieId, status) }
        }

    override suspend fun updateRating(movieId: Int, rating: Int): AppResult<Unit> =
        withContext(dispatchers.io) {
            safeDbCall { savedMovieDao.updateRating(movieId, rating) }
        }
}
