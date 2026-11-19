package com.radierdev.keveoq.domain.usecase

import app.cash.turbine.test
import com.radierdev.keveoq.domain.fake.FakeNetworkMonitor
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ObserveConnectivityUseCaseTest {

    @Test
    fun `deve emitir as mudancas de conexao`() = runTest {
        val networkMonitor = FakeNetworkMonitor(online = true)
        val observeConnectivity = ObserveConnectivityUseCase(networkMonitor)

        observeConnectivity().test {
            assertTrue(awaitItem())
            networkMonitor.isOnline.value = false
            assertFalse(awaitItem())
            networkMonitor.isOnline.value = true
            assertTrue(awaitItem())
        }
    }
}
