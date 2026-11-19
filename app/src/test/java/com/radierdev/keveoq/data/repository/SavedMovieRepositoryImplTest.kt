package com.radierdev.keveoq.data.repository

import app.cash.turbine.test
import com.radierdev.keveoq.core.dispatcher.AppDispatchers
import com.radierdev.keveoq.core.result.AppError
import com.radierdev.keveoq.core.result.AppResult
import com.radierdev.keveoq.data.fake.FakeSavedMovieDao
import com.radierdev.keveoq.domain.model.SavedMovie
import com.radierdev.keveoq.domain.model.WatchStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Duration
import java.time.Instant

class SavedMovieRepositoryImplTest {

    private val dispatcher = StandardTestDispatcher()
    private val testScope = TestScope(dispatcher)
    private val dao = FakeSavedMovieDao()
    private val repository = SavedMovieRepositoryImpl(
        savedMovieDao = dao,
        dispatchers = AppDispatchers(io = dispatcher, default = dispatcher, main = dispatcher),
    )

    private val now = Instant.parse("2026-11-19T12:00:00Z")

    private fun saved(id: Int, status: WatchStatus = WatchStatus.WANT_TO_WATCH, savedAt: Instant = now) =
        SavedMovie(
            movieId = id,
            title = "Filme $id",
            posterUrl = null,
            releaseYear = 2020,
            status = status,
            rating = null,
            savedAt = savedAt,
        )

    private fun runRepoTest(block: suspend TestScope.() -> Unit) = testScope.runTest { block() }

    @Test
    fun `deve salvar e ler um filme`() = runRepoTest {
        assertEquals(AppResult.Success(Unit), repository.save(saved(10)))

        assertEquals(AppResult.Success(saved(10)), repository.getSavedMovie(10))
    }

    @Test
    fun `filme nao salvo deve ser lido como nulo`() = runRepoTest {
        assertEquals(AppResult.Success(null), repository.getSavedMovie(10))
    }

    @Test
    fun `salvar o mesmo filme duas vezes deve retornar falha sem lancar excecao`() = runRepoTest {
        repository.save(saved(10))

        assertEquals(AppResult.Failure(AppError.Unknown), repository.save(saved(10)))
    }

    @Test
    fun `deve marcar como assistido`() = runRepoTest {
        repository.save(saved(10))

        repository.updateStatus(10, WatchStatus.WATCHED)

        assertEquals(WatchStatus.WATCHED, repository.observeSavedMovie(10).first()?.status)
    }

    @Test
    fun `deve gravar e trocar a avaliacao`() = runRepoTest {
        repository.save(saved(10, status = WatchStatus.WATCHED))

        repository.updateRating(10, 4)
        repository.updateRating(10, 2)

        assertEquals(2, repository.observeSavedMovie(10).first()?.rating)
    }

    @Test
    fun `deve observar a lista do mais recente para o mais antigo`() = runRepoTest {
        repository.save(saved(1, savedAt = now.minus(Duration.ofDays(2))))
        repository.save(saved(2, savedAt = now))
        repository.save(saved(3, savedAt = now.minus(Duration.ofDays(1))))

        assertEquals(listOf(2, 3, 1), repository.observeSavedMovies().first().map { it.movieId })
    }

    @Test
    fun `deve observar apenas os ids assistidos`() = runRepoTest {
        repository.save(saved(1, status = WatchStatus.WATCHED))
        repository.save(saved(2))

        repository.observeWatchedIds().test {
            assertEquals(setOf(1), awaitItem())

            repository.updateStatus(2, WatchStatus.WATCHED)

            assertEquals(setOf(1, 2), awaitItem())
        }
    }

    @Test
    fun `filme nao salvo deve ser observado como nulo`() = runRepoTest {
        assertNull(repository.observeSavedMovie(10).first())
    }

    @Test
    fun `falha do banco ao gravar deve retornar erro sem lancar excecao`() = runRepoTest {
        dao.writeError = IllegalStateException("disco cheio")

        assertEquals(AppResult.Failure(AppError.Unknown), repository.save(saved(10)))
        assertEquals(AppResult.Failure(AppError.Unknown), repository.updateStatus(10, WatchStatus.WATCHED))
        assertEquals(AppResult.Failure(AppError.Unknown), repository.updateRating(10, 3))
    }
}
