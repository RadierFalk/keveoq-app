package com.radierdev.keveoq.presentation.screens.details

import app.cash.turbine.test
import com.radierdev.keveoq.R
import com.radierdev.keveoq.core.result.AppError
import com.radierdev.keveoq.core.result.AppResult
import com.radierdev.keveoq.domain.fake.FakeMovieRepository
import com.radierdev.keveoq.domain.fake.FakeNetworkMonitor
import com.radierdev.keveoq.domain.fake.FakeSavedMovieRepository
import com.radierdev.keveoq.domain.fake.TestData
import com.radierdev.keveoq.domain.model.WatchStatus
import com.radierdev.keveoq.domain.usecase.AddToWatchlistUseCase
import com.radierdev.keveoq.domain.usecase.MarkAsWatchedUseCase
import com.radierdev.keveoq.domain.usecase.ObserveConnectivityUseCase
import com.radierdev.keveoq.domain.usecase.ObserveMovieDetailsUseCase
import com.radierdev.keveoq.domain.usecase.ObserveSavedMovieUseCase
import com.radierdev.keveoq.domain.usecase.RateMovieUseCase
import com.radierdev.keveoq.domain.usecase.RefreshMovieDetailsUseCase
import com.radierdev.keveoq.presentation.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.Duration

@OptIn(ExperimentalCoroutinesApi::class) // advanceUntilIdle
class DetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val movieId = 10
    private val movieRepository = FakeMovieRepository()
    private val savedMovieRepository = FakeSavedMovieRepository()
    private val networkMonitor = FakeNetworkMonitor(online = true)

    private fun createViewModel() = DetailsViewModel(
        movieId = movieId,
        observeMovieDetails = ObserveMovieDetailsUseCase(movieRepository),
        observeSavedMovie = ObserveSavedMovieUseCase(savedMovieRepository),
        observeConnectivity = ObserveConnectivityUseCase(networkMonitor),
        refreshMovieDetails = RefreshMovieDetailsUseCase(movieRepository, networkMonitor, TestData.clock),
        addToWatchlist = AddToWatchlistUseCase(savedMovieRepository, TestData.clock),
        markAsWatched = MarkAsWatchedUseCase(savedMovieRepository),
        rateMovie = RateMovieUseCase(savedMovieRepository),
    )

    @Test
    fun `deve ir de loading para success com os detalhes da api`() = runTest {
        movieRepository.fetchDetailsResult = AppResult.Success(TestData.details(movieId))
        val viewModel = createViewModel()

        viewModel.uiState.test {
            assertEquals(DetailsUiState.Loading, awaitItem())

            val state = awaitItem() as DetailsUiState.Success
            assertEquals("Filme 10", state.details.title)
            assertEquals(WatchlistAction.AddToWatchlist, state.action)
        }
    }

    @Test
    fun `detalhes abertos devem ser gravados para consulta offline`() = runTest {
        movieRepository.fetchDetailsResult = AppResult.Success(TestData.details(movieId))
        createViewModel()

        advanceUntilIdle()

        assertEquals(movieId, movieRepository.storedDetails(movieId)?.id)
    }

    @Test
    fun `offline sem cache deve ir de loading para error`() = runTest {
        networkMonitor.isOnline.value = false
        val viewModel = createViewModel()

        viewModel.uiState.test {
            assertEquals(DetailsUiState.Loading, awaitItem())
            assertEquals(DetailsUiState.Error(R.string.error_network), awaitItem())
        }
    }

    @Test
    fun `offline com detalhes ja abertos deve mostrar os detalhes`() = runTest {
        movieRepository.setDetails(TestData.details(movieId, updatedAt = TestData.NOW.minus(Duration.ofDays(3))))
        networkMonitor.isOnline.value = false
        val viewModel = createViewModel()

        viewModel.uiState.test {
            skipItems(1) // valor inicial
            assertTrue(awaitItem() is DetailsUiState.Success)
        }
    }

    @Test
    fun `tentar novamente apos erro deve carregar os detalhes`() = runTest {
        networkMonitor.isOnline.value = false
        val viewModel = createViewModel()

        viewModel.uiState.test {
            skipItems(1)
            assertTrue(awaitItem() is DetailsUiState.Error)

            networkMonitor.isOnline.value = true
            movieRepository.fetchDetailsResult = AppResult.Success(TestData.details(movieId))
            viewModel.onRetry()

            assertEquals(DetailsUiState.Loading, awaitItem())
            assertTrue(awaitItem() is DetailsUiState.Success)
        }
    }

    @Test
    fun `salvar deve trocar a acao para ja assisti`() = runTest {
        movieRepository.setDetails(TestData.details(movieId))
        val viewModel = createViewModel()

        viewModel.uiState.test {
            skipItems(1)
            assertEquals(WatchlistAction.AddToWatchlist, (awaitItem() as DetailsUiState.Success).action)

            viewModel.onAddToWatchlist()

            assertEquals(WatchlistAction.MarkAsWatched, (awaitItem() as DetailsUiState.Success).action)
        }
        assertEquals(WatchStatus.WANT_TO_WATCH, savedMovieRepository.stored(movieId)?.status)
    }

    @Test
    fun `marcar como assistido deve trocar a acao para avaliar`() = runTest {
        movieRepository.setDetails(TestData.details(movieId))
        savedMovieRepository.givenSaved(TestData.saved(movieId))
        val viewModel = createViewModel()

        viewModel.uiState.test {
            skipItems(1)
            assertEquals(WatchlistAction.MarkAsWatched, (awaitItem() as DetailsUiState.Success).action)

            viewModel.onMarkAsWatched()

            assertEquals(WatchlistAction.Rate(rating = null), (awaitItem() as DetailsUiState.Success).action)
        }
    }

    @Test
    fun `avaliar e trocar a nota deve atualizar as estrelas`() = runTest {
        movieRepository.setDetails(TestData.details(movieId))
        savedMovieRepository.givenSaved(TestData.saved(movieId, status = WatchStatus.WATCHED))
        val viewModel = createViewModel()

        viewModel.uiState.test {
            skipItems(1)
            assertEquals(WatchlistAction.Rate(null), (awaitItem() as DetailsUiState.Success).action)

            viewModel.onRate(4)
            assertEquals(WatchlistAction.Rate(4), (awaitItem() as DetailsUiState.Success).action)

            viewModel.onRate(2)
            assertEquals(WatchlistAction.Rate(2), (awaitItem() as DetailsUiState.Success).action)
        }
    }

    // Visibilidade do trailer (RF20, RF21)

    @Test
    fun `com conexao e trailer valido deve exibir o trailer`() = runTest {
        movieRepository.setDetails(TestData.details(movieId, trailerKey = "abc123"))
        val viewModel = createViewModel()

        viewModel.uiState.test {
            skipItems(1)
            assertEquals("abc123", (awaitItem() as DetailsUiState.Success).trailerKey)
        }
    }

    @Test
    fun `sem trailer valido a secao nao deve aparecer`() = runTest {
        movieRepository.setDetails(TestData.details(movieId, trailerKey = null))
        val viewModel = createViewModel()

        viewModel.uiState.test {
            skipItems(1)
            assertNull((awaitItem() as DetailsUiState.Success).trailerKey)
        }
    }

    @Test
    fun `offline o trailer nao deve aparecer mesmo com a chave gravada`() = runTest {
        movieRepository.setDetails(TestData.details(movieId, trailerKey = "abc123"))
        networkMonitor.isOnline.value = false
        val viewModel = createViewModel()

        viewModel.uiState.test {
            skipItems(1)
            val state = awaitItem() as DetailsUiState.Success
            assertNull(state.trailerKey)
            assertEquals("Filme 10", state.details.title) // os detalhes continuam visíveis
        }
    }

    @Test
    fun `trailer deve sumir ao perder a conexao e voltar ao recupera-la`() = runTest {
        movieRepository.setDetails(TestData.details(movieId, trailerKey = "abc123"))
        val viewModel = createViewModel()

        viewModel.uiState.test {
            skipItems(1)
            assertEquals("abc123", (awaitItem() as DetailsUiState.Success).trailerKey)

            networkMonitor.isOnline.value = false
            assertNull((awaitItem() as DetailsUiState.Success).trailerKey)

            networkMonitor.isOnline.value = true
            assertEquals("abc123", (awaitItem() as DetailsUiState.Success).trailerKey)
        }
    }

    @Test
    fun `falha ao salvar deve mostrar snackbar`() = runTest {
        movieRepository.setDetails(TestData.details(movieId))
        savedMovieRepository.writeFailure = AppError.Unknown
        val viewModel = createViewModel()

        viewModel.uiState.test {
            skipItems(1)
            awaitItem() // Success
            viewModel.onAddToWatchlist()
            viewModel.messages.test {
                assertEquals(R.string.error_save, awaitItem())
            }
            cancelAndIgnoreRemainingEvents()
        }
    }
}
