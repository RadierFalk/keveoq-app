package com.radierdev.keveoq.presentation.screens.watchlist

import app.cash.turbine.test
import com.radierdev.keveoq.R
import com.radierdev.keveoq.core.result.AppError
import com.radierdev.keveoq.domain.fake.FakeSavedMovieRepository
import com.radierdev.keveoq.domain.fake.TestData
import com.radierdev.keveoq.domain.model.WatchStatus
import com.radierdev.keveoq.domain.usecase.MarkAsWatchedUseCase
import com.radierdev.keveoq.domain.usecase.ObserveWatchlistUseCase
import com.radierdev.keveoq.domain.usecase.RateMovieUseCase
import com.radierdev.keveoq.presentation.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.time.Duration

class WatchlistViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeSavedMovieRepository()

    private fun createViewModel() = WatchlistViewModel(
        observeWatchlist = ObserveWatchlistUseCase(repository),
        markAsWatched = MarkAsWatchedUseCase(repository),
        rateMovie = RateMovieUseCase(repository),
    )

    private fun givenWatchlist() {
        repository.givenSaved(TestData.saved(1, savedAt = TestData.NOW.minus(Duration.ofDays(2))))
        repository.givenSaved(
            TestData.saved(2, status = WatchStatus.WATCHED, rating = 4, savedAt = TestData.NOW.minus(Duration.ofDays(1))),
        )
        repository.givenSaved(TestData.saved(3, savedAt = TestData.NOW))
    }

    private fun WatchlistUiState.ids() = (content as WatchlistContent.Success).movies.map { it.movieId }

    @Test
    fun `lista vazia deve ir de loading para empty`() = runTest {
        val viewModel = createViewModel()

        viewModel.uiState.test {
            assertEquals(WatchlistContent.Loading, awaitItem().content)
            assertEquals(WatchlistContent.Empty, awaitItem().content)
        }
    }

    @Test
    fun `deve ir de loading para success com todos os filmes do mais recente ao mais antigo`() = runTest {
        givenWatchlist()
        val viewModel = createViewModel()

        viewModel.uiState.test {
            assertEquals(WatchlistContent.Loading, awaitItem().content)
            assertEquals(listOf(3, 2, 1), awaitItem().ids())
        }
    }

    @Test
    fun `filtros devem mostrar apenas os filmes do status escolhido`() = runTest {
        givenWatchlist()
        val viewModel = createViewModel()

        viewModel.uiState.test {
            skipItems(1)
            assertEquals(listOf(3, 2, 1), awaitItem().ids())

            viewModel.onFilterSelected(WatchlistFilter.WATCHED)
            val watched = awaitItem()
            assertEquals(WatchlistFilter.WATCHED, watched.filter)
            assertEquals(listOf(2), watched.ids())

            viewModel.onFilterSelected(WatchlistFilter.WANT_TO_WATCH)
            assertEquals(listOf(3, 1), awaitItem().ids())
        }
    }

    @Test
    fun `filtro sem filmes deve ficar empty`() = runTest {
        repository.givenSaved(TestData.saved(1))
        val viewModel = createViewModel()

        viewModel.uiState.test {
            skipItems(2) // Loading e a lista com todos
            viewModel.onFilterSelected(WatchlistFilter.WATCHED)

            val state = awaitItem()
            assertEquals(WatchlistFilter.WATCHED, state.filter)
            assertEquals(WatchlistContent.Empty, state.content)
        }
    }

    @Test
    fun `marcar como assistido deve mover o filme para assistidos`() = runTest {
        givenWatchlist()
        val viewModel = createViewModel()

        viewModel.uiState.test {
            skipItems(2) // Loading e a lista com todos
            viewModel.onFilterSelected(WatchlistFilter.WATCHED)
            assertEquals(listOf(2), awaitItem().ids())

            viewModel.onMarkAsWatched(1)

            assertEquals(listOf(2, 1), awaitItem().ids())
        }
    }

    @Test
    fun `avaliar deve atualizar a nota do filme na lista`() = runTest {
        givenWatchlist()
        val viewModel = createViewModel()

        viewModel.uiState.test {
            skipItems(2)

            viewModel.onRate(2, 5)

            val movie = (awaitItem().content as WatchlistContent.Success).movies.first { it.movieId == 2 }
            assertEquals(5, movie.rating)
        }
    }

    @Test
    fun `falha ao salvar deve mostrar snackbar`() = runTest {
        givenWatchlist()
        repository.writeFailure = AppError.Unknown
        val viewModel = createViewModel()

        viewModel.messages.test {
            viewModel.onMarkAsWatched(1)
            assertEquals(R.string.error_save, awaitItem())
        }
    }
}
