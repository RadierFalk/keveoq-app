package com.radierdev.keveoq.presentation.screens.watchlist

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radierdev.keveoq.R
import com.radierdev.keveoq.domain.model.MarkAsWatchedResult
import com.radierdev.keveoq.domain.model.RateMovieResult
import com.radierdev.keveoq.domain.model.SavedMovie
import com.radierdev.keveoq.domain.model.WatchStatus
import com.radierdev.keveoq.domain.usecase.MarkAsWatchedUseCase
import com.radierdev.keveoq.domain.usecase.ObserveWatchlistUseCase
import com.radierdev.keveoq.domain.usecase.RateMovieUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Filtros da lista (RF09). */
enum class WatchlistFilter(val status: WatchStatus?) {
    ALL(null),
    WANT_TO_WATCH(WatchStatus.WANT_TO_WATCH),
    WATCHED(WatchStatus.WATCHED),
}

@Immutable
sealed interface WatchlistContent {
    data object Loading : WatchlistContent
    data class Success(val movies: List<SavedMovie>) : WatchlistContent
    data object Empty : WatchlistContent
    data object Error : WatchlistContent
}

@Immutable
data class WatchlistUiState(
    val filter: WatchlistFilter = WatchlistFilter.ALL,
    val content: WatchlistContent = WatchlistContent.Loading,
)

/**
 * ViewModel da lista "Quero assistir" (RF09–RF12).
 *
 * Os dados são só locais, então a lista funciona igual com ou sem internet (CA25).
 * Ao trocar o filtro, `flatMapLatest` cancela a observação anterior e passa a
 * observar a lista do novo status.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WatchlistViewModel(
    private val observeWatchlist: ObserveWatchlistUseCase,
    private val markAsWatched: MarkAsWatchedUseCase,
    private val rateMovie: RateMovieUseCase,
) : ViewModel() {

    private val filter = MutableStateFlow(WatchlistFilter.ALL)

    /** Cada "Tentar novamente" incrementa o contador e reinicia a observação. */
    private val retryCount = MutableStateFlow(0)

    /** Eventos únicos: mensagens do Snackbar (ID do texto em strings.xml). */
    private val _messages = Channel<Int>(Channel.BUFFERED)
    val messages: Flow<Int> = _messages.receiveAsFlow()

    val uiState: StateFlow<WatchlistUiState> = combine(filter, retryCount) { currentFilter, _ -> currentFilter }
        .flatMapLatest { currentFilter ->
            observeWatchlist(currentFilter.status)
                .map { movies ->
                    val content = if (movies.isEmpty()) WatchlistContent.Empty else WatchlistContent.Success(movies)
                    WatchlistUiState(currentFilter, content)
                }
                .catch { emit(WatchlistUiState(currentFilter, WatchlistContent.Error)) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = WatchlistUiState(),
        )

    fun onFilterSelected(newFilter: WatchlistFilter) {
        filter.value = newFilter
    }

    fun onMarkAsWatched(movieId: Int) {
        viewModelScope.launch {
            when (markAsWatched(movieId)) {
                MarkAsWatchedResult.Marked, MarkAsWatchedResult.AlreadyWatched -> Unit
                MarkAsWatchedResult.NotInList, is MarkAsWatchedResult.Failure -> _messages.send(R.string.error_save)
            }
        }
    }

    fun onRate(movieId: Int, rating: Int) {
        viewModelScope.launch {
            when (rateMovie(movieId, rating)) {
                RateMovieResult.Rated -> Unit
                RateMovieResult.InvalidRating, RateMovieResult.NotWatched,
                is RateMovieResult.Failure -> _messages.send(R.string.error_save)
            }
        }
    }

    /** "Tentar novamente": volta a observar o banco com o filtro atual. */
    fun onRetry() {
        retryCount.update { it + 1 }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
