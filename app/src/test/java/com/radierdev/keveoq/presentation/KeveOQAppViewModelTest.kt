package com.radierdev.keveoq.presentation

import app.cash.turbine.test
import com.radierdev.keveoq.domain.fake.FakeNetworkMonitor
import com.radierdev.keveoq.domain.usecase.ObserveConnectivityUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class KeveOQAppViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val networkMonitor = FakeNetworkMonitor(online = true)
    private val viewModel = KeveOQAppViewModel(ObserveConnectivityUseCase(networkMonitor))

    @Test
    fun `deve comecar como online para o banner nao piscar`() = runTest {
        assertTrue(viewModel.isOnline.value)
    }

    @Test
    fun `deve indicar offline ao perder a conexao e online ao recuperar`() = runTest {
        viewModel.isOnline.test {
            assertTrue(awaitItem())

            networkMonitor.isOnline.value = false
            assertFalse(awaitItem())

            networkMonitor.isOnline.value = true
            assertTrue(awaitItem())
        }
    }
}
