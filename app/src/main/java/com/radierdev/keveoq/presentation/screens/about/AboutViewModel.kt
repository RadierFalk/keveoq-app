package com.radierdev.keveoq.presentation.screens.about

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Estado da tela "Sobre". O conteúdo é fixo, por isso só existe o estado de
 * conteúdo (seção 9.2 do planejamento).
 */
@Immutable
data class AboutUiState(
    val appVersion: String,
    /** Texto exigido pelos termos de uso do TMDB, sem tradução (RF16, CA27). */
    val tmdbAttribution: String = TMDB_ATTRIBUTION,
) {
    companion object {
        const val TMDB_ATTRIBUTION =
            "This product uses the TMDB API but is not endorsed or certified by TMDB."
    }
}

/**
 * Expõe a versão do app. A versão é recebida pelo construtor (o Koin passa
 * `BuildConfig.VERSION_NAME`), para o ViewModel não depender de `Context`
 * e poder ser testado com um valor fixo.
 */
class AboutViewModel(appVersion: String) : ViewModel() {

    private val _uiState = MutableStateFlow(AboutUiState(appVersion = appVersion))
    val uiState: StateFlow<AboutUiState> = _uiState.asStateFlow()
}
