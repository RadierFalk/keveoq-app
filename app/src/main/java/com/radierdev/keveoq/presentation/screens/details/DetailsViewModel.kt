package com.radierdev.keveoq.presentation.screens.details

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radierdev.keveoq.R
import com.radierdev.keveoq.core.result.AppError
import com.radierdev.keveoq.core.result.AppResult
import com.radierdev.keveoq.domain.model.AddToWatchlistResult
import com.radierdev.keveoq.domain.model.MarkAsWatchedResult
import com.radierdev.keveoq.domain.model.MovieDetails
import com.radierdev.keveoq.domain.model.RateMovieResult
import com.radierdev.keveoq.domain.model.SavedMovie
import com.radierdev.keveoq.domain.model.WatchStatus
import com.radierdev.keveoq.domain.usecase.AddToWatchlistUseCase
import com.radierdev.keveoq.domain.usecase.MarkAsWatchedUseCase
import com.radierdev.keveoq.domain.usecase.ObserveConnectivityUseCase
import com.radierdev.keveoq.domain.usecase.ObserveMovieDetailsUseCase
import com.radierdev.keveoq.domain.usecase.ObserveSavedMovieUseCase
import com.radierdev.keveoq.domain.usecase.RateMovieUseCase
import com.radierdev.keveoq.domain.usecase.RefreshMovieDetailsUseCase
import com.radierdev.keveoq.presentation.util.ErrorMessageMapper
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Ação disponível conforme a situação do filme na lista "Quero assistir". */
@Immutable
sealed interface WatchlistAction {
    /** Fora da lista: botão "Quero assistir". */
    data object AddToWatchlist : WatchlistAction

    /** Na lista, ainda não visto: botão "Já assisti". */
    data object MarkAsWatched : WatchlistAction

    /** Assistido: estrelas para avaliar (ou trocar a nota). */
    data class Rate(val rating: Int?) : WatchlistAction
}

@Immutable
sealed interface DetailsUiState {
    data object Loading : DetailsUiState
    /**
     * @property trailerKey chave do trailer a exibir, ou `null` para esconder a seção:
     * sem trailer válido (CA17) ou sem conexão (RF21, CA18).
     */
    data class Success(
        val details: MovieDetails,
        val action: WatchlistAction,
        val trailerKey: String? = null,
    ) : DetailsUiState
    data class Error(@StringRes val messageRes: Int) : DetailsUiState
}

/** Situação da busca dos detalhes, usada só enquanto não há nada no banco. */
private sealed interface RefreshStatus {
    data object AwaitingData : RefreshStatus
    data class Failed(val error: AppError) : RefreshStatus
}

/**
 * ViewModel dos detalhes de um filme (seção 6.3 do planejamento).
 *
 * Os detalhes vêm do banco. Ao abrir, pede uma atualização, que grava os detalhes
 * (e a chave do trailer) para consulta offline (RF07). Se o filme já foi aberto
 * antes, os detalhes aparecem mesmo sem internet (CA12).
 */
class DetailsViewModel(
    private val movieId: Int,
    observeMovieDetails: ObserveMovieDetailsUseCase,
    observeSavedMovie: ObserveSavedMovieUseCase,
    observeConnectivity: ObserveConnectivityUseCase,
    private val refreshMovieDetails: RefreshMovieDetailsUseCase,
    private val addToWatchlist: AddToWatchlistUseCase,
    private val markAsWatched: MarkAsWatchedUseCase,
    private val rateMovie: RateMovieUseCase,
) : ViewModel() {

    private val refreshStatus = MutableStateFlow<RefreshStatus>(RefreshStatus.AwaitingData)

    /** Eventos únicos: mensagens do Snackbar (ID do texto em strings.xml). */
    private val _messages = Channel<Int>(Channel.BUFFERED)
    val messages: Flow<Int> = _messages.receiveAsFlow()

    val uiState: StateFlow<DetailsUiState> = combine(
        observeMovieDetails(movieId),
        observeSavedMovie(movieId),
        observeConnectivity(),
        refreshStatus,
    ) { details, saved, isOnline, status ->
        when {
            details != null -> DetailsUiState.Success(
                details = details,
                action = actionFor(saved),
                // A chave fica gravada, mas o trailer só aparece com conexão (seção 6.3).
                trailerKey = details.trailerKey.takeIf { isOnline },
            )
            status is RefreshStatus.Failed -> DetailsUiState.Error(ErrorMessageMapper.messageFor(status.error))
            else -> DetailsUiState.Loading
        }
    }
        .catch { emit(DetailsUiState.Error(R.string.error_generic)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = DetailsUiState.Loading,
        )

    init {
        refresh()
    }

    /** Botão "Tentar novamente" do estado de erro. */
    fun onRetry() {
        refresh()
    }

    fun onAddToWatchlist() {
        val details = (uiState.value as? DetailsUiState.Success)?.details ?: return
        viewModelScope.launch {
            when (val result = addToWatchlist(details)) {
                AddToWatchlistResult.Added -> Unit // o botão muda sozinho, pelo Flow do banco
                AddToWatchlistResult.AlreadyInList -> _messages.send(R.string.message_already_in_list)
                is AddToWatchlistResult.Failure -> _messages.send(R.string.error_save)
            }
        }
    }

    fun onMarkAsWatched() {
        viewModelScope.launch {
            when (markAsWatched(movieId)) {
                MarkAsWatchedResult.Marked, MarkAsWatchedResult.AlreadyWatched -> Unit
                MarkAsWatchedResult.NotInList, is MarkAsWatchedResult.Failure -> _messages.send(R.string.error_save)
            }
        }
    }

    fun onRate(rating: Int) {
        viewModelScope.launch {
            when (rateMovie(movieId, rating)) {
                RateMovieResult.Rated -> Unit
                // A tela só oferece 1 a 5 estrelas e só para assistidos; ainda assim, nada é gravado.
                RateMovieResult.InvalidRating, RateMovieResult.NotWatched,
                is RateMovieResult.Failure -> _messages.send(R.string.error_save)
            }
        }
    }

    private fun refresh() {
        refreshStatus.value = RefreshStatus.AwaitingData
        viewModelScope.launch {
            val result = refreshMovieDetails(movieId)
            if (result is AppResult.Failure) {
                refreshStatus.value = RefreshStatus.Failed(result.error)
            }
        }
    }

    private fun actionFor(saved: SavedMovie?): WatchlistAction = when (saved?.status) {
        null -> WatchlistAction.AddToWatchlist
        WatchStatus.WANT_TO_WATCH -> WatchlistAction.MarkAsWatched
        WatchStatus.WATCHED -> WatchlistAction.Rate(saved.rating)
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
