package com.radierdev.keveoq.domain.repository

import com.radierdev.keveoq.core.result.AppResult
import com.radierdev.keveoq.domain.model.SavedMovie
import com.radierdev.keveoq.domain.model.WatchStatus
import kotlinx.coroutines.flow.Flow

/**
 * Contrato da lista "Quero assistir". Os dados são exclusivamente locais
 * (não há servidor), mas o acesso ao banco ainda pode falhar (por exemplo,
 * disco cheio). Por isso as leituras e escritas pontuais devolvem [AppResult]
 * em vez de lançar exceção, como todos os repositórios do app.
 */
interface SavedMovieRepository {

    /** Todos os filmes salvos, do mais recente para o mais antigo. */
    fun observeSavedMovies(): Flow<List<SavedMovie>>

    /** Um filme salvo, ou `null` se ele não está na lista. */
    fun observeSavedMovie(movieId: Int): Flow<SavedMovie?>

    /** IDs dos filmes com status `WATCHED`, usados para filtrar as recomendações. */
    fun observeWatchedIds(): Flow<Set<Int>>

    /** O filme salvo, `Success(null)` se ele não está na lista, ou a falha de leitura. */
    suspend fun getSavedMovie(movieId: Int): AppResult<SavedMovie?>

    suspend fun save(movie: SavedMovie): AppResult<Unit>

    suspend fun updateStatus(movieId: Int, status: WatchStatus): AppResult<Unit>

    suspend fun updateRating(movieId: Int, rating: Int): AppResult<Unit>
}
