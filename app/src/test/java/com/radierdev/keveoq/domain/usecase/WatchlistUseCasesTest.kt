package com.radierdev.keveoq.domain.usecase

import app.cash.turbine.test
import com.radierdev.keveoq.core.result.AppError
import com.radierdev.keveoq.domain.fake.FakeSavedMovieRepository
import com.radierdev.keveoq.domain.fake.TestData
import com.radierdev.keveoq.domain.model.AddToWatchlistResult
import com.radierdev.keveoq.domain.model.MarkAsWatchedResult
import com.radierdev.keveoq.domain.model.RateMovieResult
import com.radierdev.keveoq.domain.model.WatchStatus
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Duration

class WatchlistUseCasesTest {

    private val repository = FakeSavedMovieRepository()
    private val addToWatchlist = AddToWatchlistUseCase(repository, TestData.clock)
    private val markAsWatched = MarkAsWatchedUseCase(repository)
    private val rateMovie = RateMovieUseCase(repository)
    private val observeSavedMovie = ObserveSavedMovieUseCase(repository)
    private val observeWatchlist = ObserveWatchlistUseCase(repository)

    // AddToWatchlistUseCase

    @Test
    fun `deve adicionar filme a lista como quero assistir`() = runTest {
        val result = addToWatchlist(TestData.details(10))

        assertEquals(AddToWatchlistResult.Added, result)
        val saved = repository.stored(10)
        assertEquals(WatchStatus.WANT_TO_WATCH, saved?.status)
        assertNull(saved?.rating)
        assertEquals(TestData.NOW, saved?.savedAt)
        assertEquals("Filme 10", saved?.title)
        assertEquals("https://image.tmdb.org/t/p/w500/poster10.jpg", saved?.posterUrl)
    }

    @Test
    fun `nao deve adicionar o mesmo filme duas vezes`() = runTest {
        addToWatchlist(TestData.details(10))

        val result = addToWatchlist(TestData.details(10))

        assertEquals(AddToWatchlistResult.AlreadyInList, result)
        assertEquals(1, repository.saveCalls)
    }

    @Test
    fun `nao deve readicionar filme ja assistido nem perder sua nota`() = runTest {
        repository.givenSaved(TestData.saved(10, status = WatchStatus.WATCHED, rating = 4))

        val result = addToWatchlist(TestData.details(10))

        assertEquals(AddToWatchlistResult.AlreadyInList, result)
        assertEquals(WatchStatus.WATCHED, repository.stored(10)?.status)
        assertEquals(4, repository.stored(10)?.rating)
    }

    // MarkAsWatchedUseCase

    @Test
    fun `deve marcar filme quero assistir como assistido`() = runTest {
        repository.givenSaved(TestData.saved(10))

        val result = markAsWatched(10)

        assertEquals(MarkAsWatchedResult.Marked, result)
        assertEquals(WatchStatus.WATCHED, repository.stored(10)?.status)
    }

    @Test
    fun `marcar filme ja assistido deve retornar ja assistido`() = runTest {
        repository.givenSaved(TestData.saved(10, status = WatchStatus.WATCHED))

        assertEquals(MarkAsWatchedResult.AlreadyWatched, markAsWatched(10))
        assertEquals(0, repository.updateStatusCalls)
    }

    @Test
    fun `marcar filme fora da lista deve retornar nao esta na lista`() = runTest {
        assertEquals(MarkAsWatchedResult.NotInList, markAsWatched(10))
        assertEquals(0, repository.updateStatusCalls)
    }

    // RateMovieUseCase

    @Test
    fun `deve avaliar filme assistido`() = runTest {
        repository.givenSaved(TestData.saved(10, status = WatchStatus.WATCHED))

        val result = rateMovie(10, rating = 4)

        assertEquals(RateMovieResult.Rated, result)
        assertEquals(4, repository.stored(10)?.rating)
    }

    @Test
    fun `nova avaliacao deve substituir a anterior`() = runTest {
        repository.givenSaved(TestData.saved(10, status = WatchStatus.WATCHED, rating = 4))

        val result = rateMovie(10, rating = 2)

        assertEquals(RateMovieResult.Rated, result)
        assertEquals(2, repository.stored(10)?.rating)
    }

    @Test
    fun `nao deve avaliar filme ainda nao assistido`() = runTest {
        repository.givenSaved(TestData.saved(10, status = WatchStatus.WANT_TO_WATCH))

        assertEquals(RateMovieResult.NotWatched, rateMovie(10, rating = 4))
        assertNull(repository.stored(10)?.rating)
    }

    @Test
    fun `nao deve avaliar filme fora da lista`() = runTest {
        assertEquals(RateMovieResult.NotWatched, rateMovie(10, rating = 4))
        assertEquals(0, repository.updateRatingCalls)
    }

    @Test
    fun `deve rejeitar notas 0 e 6`() = runTest {
        repository.givenSaved(TestData.saved(10, status = WatchStatus.WATCHED, rating = 3))

        assertEquals(RateMovieResult.InvalidRating, rateMovie(10, rating = 0))
        assertEquals(RateMovieResult.InvalidRating, rateMovie(10, rating = 6))
        assertEquals(3, repository.stored(10)?.rating)
        assertEquals(0, repository.updateRatingCalls)
    }

    // Falhas do banco: viram o resultado Failure, sem exceção

    @Test
    fun `falha ao ler a lista deve impedir a adicao`() = runTest {
        repository.readFailure = AppError.Unknown

        assertEquals(AddToWatchlistResult.Failure(AppError.Unknown), addToWatchlist(TestData.details(10)))
        assertEquals(0, repository.saveCalls)
    }

    @Test
    fun `falha ao gravar deve retornar falha na adicao`() = runTest {
        repository.writeFailure = AppError.Unknown

        assertEquals(AddToWatchlistResult.Failure(AppError.Unknown), addToWatchlist(TestData.details(10)))
        assertNull(repository.stored(10))
    }

    @Test
    fun `falha ao gravar deve retornar falha ao marcar como assistido`() = runTest {
        repository.givenSaved(TestData.saved(10))
        repository.writeFailure = AppError.Unknown

        assertEquals(MarkAsWatchedResult.Failure(AppError.Unknown), markAsWatched(10))
        assertEquals(WatchStatus.WANT_TO_WATCH, repository.stored(10)?.status)
    }

    @Test
    fun `falha ao ler deve retornar falha ao marcar como assistido`() = runTest {
        repository.readFailure = AppError.Unknown

        assertEquals(MarkAsWatchedResult.Failure(AppError.Unknown), markAsWatched(10))
    }

    @Test
    fun `falha ao gravar deve retornar falha ao avaliar`() = runTest {
        repository.givenSaved(TestData.saved(10, status = WatchStatus.WATCHED, rating = 3))
        repository.writeFailure = AppError.Unknown

        assertEquals(RateMovieResult.Failure(AppError.Unknown), rateMovie(10, rating = 5))
        assertEquals(3, repository.stored(10)?.rating)
    }

    @Test
    fun `falha ao ler deve retornar falha ao avaliar`() = runTest {
        repository.readFailure = AppError.Unknown

        assertEquals(RateMovieResult.Failure(AppError.Unknown), rateMovie(10, rating = 5))
    }

    // ObserveSavedMovieUseCase

    @Test
    fun `deve acompanhar o status do filme salvo`() = runTest {
        observeSavedMovie(10).test {
            assertNull(awaitItem())

            addToWatchlist(TestData.details(10))
            assertEquals(WatchStatus.WANT_TO_WATCH, awaitItem()?.status)

            markAsWatched(10)
            assertEquals(WatchStatus.WATCHED, awaitItem()?.status)

            rateMovie(10, rating = 5)
            assertEquals(5, awaitItem()?.rating)
        }
    }

    // ObserveWatchlistUseCase

    private fun givenWatchlist() {
        repository.givenSaved(TestData.saved(1, savedAt = TestData.NOW.minus(Duration.ofDays(3))))
        repository.givenSaved(
            TestData.saved(2, status = WatchStatus.WATCHED, savedAt = TestData.NOW.minus(Duration.ofDays(2))),
        )
        repository.givenSaved(TestData.saved(3, savedAt = TestData.NOW.minus(Duration.ofDays(1))))
    }

    @Test
    fun `filtro todos deve listar todos os filmes do mais recente para o mais antigo`() = runTest {
        givenWatchlist()

        observeWatchlist(filter = null).test {
            assertEquals(listOf(3, 2, 1), awaitItem().map { it.movieId })
        }
    }

    @Test
    fun `filtro quero assistir deve listar apenas filmes quero assistir`() = runTest {
        givenWatchlist()

        observeWatchlist(filter = WatchStatus.WANT_TO_WATCH).test {
            assertEquals(listOf(3, 1), awaitItem().map { it.movieId })
        }
    }

    @Test
    fun `filtro assistidos deve listar apenas filmes assistidos`() = runTest {
        givenWatchlist()

        observeWatchlist(filter = WatchStatus.WATCHED).test {
            assertEquals(listOf(2), awaitItem().map { it.movieId })
        }
    }

    @Test
    fun `filtro deve refletir filme marcado como assistido`() = runTest {
        givenWatchlist()

        observeWatchlist(filter = WatchStatus.WATCHED).test {
            assertEquals(listOf(2), awaitItem().map { it.movieId })

            markAsWatched(1)

            assertEquals(listOf(2, 1), awaitItem().map { it.movieId })
        }
    }

    @Test
    fun `lista vazia deve emitir lista vazia`() = runTest {
        observeWatchlist(filter = null).test {
            assertEquals(emptyList<Int>(), awaitItem().map { it.movieId })
        }
    }
}
