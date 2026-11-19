package com.radierdev.keveoq.presentation.screens.recommendations

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radierdev.keveoq.R
import com.radierdev.keveoq.core.result.AppError
import com.radierdev.keveoq.core.result.AppResult
import com.radierdev.keveoq.domain.model.Mood
import com.radierdev.keveoq.domain.model.Movie
import com.radierdev.keveoq.domain.model.Recommendations
import com.radierdev.keveoq.domain.usecase.ObserveRecommendationsUseCase
import com.radierdev.keveoq.domain.usecase.RefreshRecommendationsUseCase
import com.radierdev.keveoq.presentation.util.ErrorMessageMapper
import com.radierdev.keveoq.presentation.util.UpdatedAtFormatter
import com.radierdev.keveoq.presentation.util.UpdatedAtText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Clock

/** Estado da tela de recomendações. */
@Immutable
data class RecommendationsUiState(
    val mood: Mood,
    val content: RecommendationsContent = RecommendationsContent.Loading,
    /** `true` durante o pull-to-refresh, para mostrar o indicador no topo da lista. */
    val isRefreshing: Boolean = false,
)

/** Os quatro estados exigidos pelo `CLAUDE.md` (RF23). */
@Immutable
sealed interface RecommendationsContent {
    data object Loading : RecommendationsContent
    data class Success(val movies: List<Movie>, val updatedAt: UpdatedAtText?) : RecommendationsContent
    data object Empty : RecommendationsContent
    data class Error(@StringRes val messageRes: Int) : RecommendationsContent
}

/**
 * Situação da última atualização, usada só quando o cache está vazio.
 *
 * - [AwaitingData]: buscando, ou a busca terminou e os dados ainda vão chegar do banco.
 * - [NoResults]: a busca terminou e o banco continua vazio (a API não trouxe filmes).
 * - [Failed]: a busca falhou.
 */
private sealed interface RefreshStatus {
    data object AwaitingData : RefreshStatus
    data object NoResults : RefreshStatus
    data class Failed(val error: AppError) : RefreshStatus
}

/**
 * ViewModel das recomendações de um humor (seção 6.2 do planejamento).
 *
 * A lista vem sempre do banco (Offline First). Ao abrir, pede uma atualização,
 * que só chama a API se o cache estiver vazio ou com 24 h ou mais. O estado da
 * tela combina o que está no banco com a situação da atualização:
 *
 * | Cache     | Atualização     | Estado                    |
 * |-----------|-----------------|---------------------------|
 * | com dados | qualquer        | Success (+ Snackbar se falhar) |
 * | vazio     | em andamento    | Loading                   |
 * | vazio     | sem filmes      | Empty                     |
 * | vazio     | falhou          | Error ("Tentar novamente")|
 */
class RecommendationsViewModel(
    private val mood: Mood,
    observeRecommendations: ObserveRecommendationsUseCase,
    private val refreshRecommendations: RefreshRecommendationsUseCase,
    private val clock: Clock,
) : ViewModel() {

    private val refreshStatus = MutableStateFlow<RefreshStatus>(RefreshStatus.AwaitingData)
    private val isRefreshing = MutableStateFlow(false)

    /** Eventos únicos: mensagens do Snackbar (ID do texto em strings.xml). */
    private val _messages = Channel<Int>(Channel.BUFFERED)
    val messages: Flow<Int> = _messages.receiveAsFlow()

    private val recommendations: Flow<Recommendations> = observeRecommendations(mood)

    val uiState: StateFlow<RecommendationsUiState> = combine(
        recommendations,
        refreshStatus,
        isRefreshing,
    ) { recommendations, status, refreshing ->
        RecommendationsUiState(
            mood = mood,
            content = contentFor(recommendations, status),
            isRefreshing = refreshing,
        )
    }
        .catch { emit(RecommendationsUiState(mood, RecommendationsContent.Error(R.string.error_generic))) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = RecommendationsUiState(mood = mood),
        )

    init {
        refresh(force = false)
    }

    /** Pull-to-refresh: força a busca, ignorando a validade do cache (RF05). */
    fun onRefresh() {
        isRefreshing.value = true
        refresh(force = true, onFinished = { isRefreshing.value = false })
    }

    /** Botão "Tentar novamente" do estado de erro (RF15). */
    fun onRetry() {
        refresh(force = false)
    }

    private fun refresh(force: Boolean, onFinished: () -> Unit = {}) {
        refreshStatus.value = RefreshStatus.AwaitingData
        viewModelScope.launch {
            when (val result = refreshRecommendations(mood, force)) {
                is AppResult.Success -> {
                    // O banco avisa a tela dos dados novos de forma assíncrona. Marcar "sem
                    // resultados" agora faria a tela mostrar "vazio" por um instante antes
                    // da lista. Por isso só marcamos se o banco estiver de fato vazio.
                    if (isCacheEmpty()) refreshStatus.value = RefreshStatus.NoResults
                }
                is AppResult.Failure -> {
                    refreshStatus.value = RefreshStatus.Failed(result.error)
                    // Com filmes na tela, o erro não bloqueia: aparece num Snackbar (RF14).
                    if (hasCachedMovies()) _messages.send(ErrorMessageMapper.messageFor(result.error))
                }
            }
            onFinished()
        }
    }

    /** Consulta o banco no momento da falha: há filmes na tela? */
    private suspend fun hasCachedMovies(): Boolean =
        currentRecommendations()?.movies?.isNotEmpty() ?: false

    /** O banco não tem nenhuma recomendação gravada para este humor. */
    private suspend fun isCacheEmpty(): Boolean {
        val current = currentRecommendations() ?: return true
        return current.movies.isEmpty() && current.updatedAt == null
    }

    /** Leitura pontual do banco; `null` se a leitura falhar. */
    private suspend fun currentRecommendations(): Recommendations? =
        try {
            recommendations.first()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }

    private fun contentFor(recommendations: Recommendations, status: RefreshStatus): RecommendationsContent =
        when {
            recommendations.movies.isNotEmpty() -> RecommendationsContent.Success(
                movies = recommendations.movies,
                updatedAt = recommendations.updatedAt?.let { UpdatedAtFormatter.format(it, clock.instant()) },
            )
            // Há cache, mas todos os filmes foram filtrados (ex.: todos já assistidos).
            recommendations.updatedAt != null -> RecommendationsContent.Empty
            else -> when (status) {
                RefreshStatus.AwaitingData -> RecommendationsContent.Loading
                RefreshStatus.NoResults -> RecommendationsContent.Empty
                is RefreshStatus.Failed -> RecommendationsContent.Error(ErrorMessageMapper.messageFor(status.error))
            }
        }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
