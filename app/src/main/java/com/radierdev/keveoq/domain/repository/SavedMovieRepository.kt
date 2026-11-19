package com.radierdev.keveoq.domain.repository

import com.radierdev.keveoq.domain.model.SavedMovie
import com.radierdev.keveoq.domain.model.WatchStatus
import kotlinx.coroutines.flow.Flow

/**
 * Contrato da lista "Quero assistir". Os dados são exclusivamente locais
 * (não há servidor), por isso os métodos não devolvem erros de rede.
 */
interface SavedMovieRepository {

    /** Todos os filmes salvos, do mais recente para o mais antigo. */
    fun observeSavedMovies(): Flow<List<SavedMovie>>

    /** Um filme salvo, ou `null` se ele não está na lista. */
    fun observeSavedMovie(movieId: Int): Flow<SavedMovie?>

    /** IDs dos filmes com status `WATCHED`, usados para filtrar as recomendações. */
    fun observeWatchedIds(): Flow<Set<Int>>

    suspend fun getSavedMovie(movieId: Int): SavedMovie?

    suspend fun save(movie: SavedMovie)

    suspend fun updateStatus(movieId: Int, status: WatchStatus)

    suspend fun updateRating(movieId: Int, rating: Int)
}
