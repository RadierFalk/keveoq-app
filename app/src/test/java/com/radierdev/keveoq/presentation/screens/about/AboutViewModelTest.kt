package com.radierdev.keveoq.presentation.screens.about

import com.radierdev.keveoq.presentation.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class AboutViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `deve exibir a versao do app`() {
        val viewModel = AboutViewModel(appVersion = "1.0")

        assertEquals("1.0", viewModel.uiState.value.appVersion)
    }

    @Test
    fun `deve exibir exatamente o texto de atribuicao ao tmdb`() {
        val viewModel = AboutViewModel(appVersion = "1.0")

        assertEquals(
            "This product uses the TMDB API but is not endorsed or certified by TMDB.",
            viewModel.uiState.value.tmdbAttribution,
        )
    }
}
