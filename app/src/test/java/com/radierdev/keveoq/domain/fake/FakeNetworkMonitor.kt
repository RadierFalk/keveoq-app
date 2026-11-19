package com.radierdev.keveoq.domain.fake

import com.radierdev.keveoq.core.network.NetworkMonitor
import kotlinx.coroutines.flow.MutableStateFlow

/** Monitor de conexão controlado pelo teste. */
class FakeNetworkMonitor(online: Boolean = true) : NetworkMonitor {
    override val isOnline = MutableStateFlow(online)
}
