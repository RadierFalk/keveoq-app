package com.radierdev.keveoq.domain.usecase

import com.radierdev.keveoq.core.network.NetworkMonitor
import kotlinx.coroutines.flow.Flow

/** Expõe, em tempo real, se há conexão com a internet (RF13). */
class ObserveConnectivityUseCase(
    private val networkMonitor: NetworkMonitor,
) {
    operator fun invoke(): Flow<Boolean> = networkMonitor.isOnline
}
