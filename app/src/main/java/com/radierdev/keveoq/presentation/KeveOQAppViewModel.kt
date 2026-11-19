package com.radierdev.keveoq.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radierdev.keveoq.domain.usecase.ObserveConnectivityUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel da raiz do app: informa se há conexão, para o banner "sem internet" (RF13).
 *
 * Começa como online (`true`) para o banner não "piscar" na abertura do app
 * antes de o monitor emitir o estado real.
 */
class KeveOQAppViewModel(
    observeConnectivity: ObserveConnectivityUseCase,
) : ViewModel() {

    val isOnline: StateFlow<Boolean> = observeConnectivity()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = true,
        )

    private companion object {
        /** Mantém a coleta por 5 s após a tela sair, para sobreviver à rotação. */
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
