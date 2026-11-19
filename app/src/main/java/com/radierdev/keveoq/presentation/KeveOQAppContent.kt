package com.radierdev.keveoq.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.radierdev.keveoq.presentation.components.OfflineBanner
import com.radierdev.keveoq.presentation.navigation.KeveOQNavHost
import com.radierdev.keveoq.presentation.theme.KeveOQTheme
import org.koin.compose.viewmodel.koinViewModel

/** Raiz do app: liga o [KeveOQAppViewModel] ao conteúdo. */
@Composable
fun KeveOQApp(viewModel: KeveOQAppViewModel = koinViewModel()) {
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    KeveOQAppContent(isOnline = isOnline) {
        KeveOQNavHost(navController = navController)
    }
}

/**
 * Estrutura sem estado da raiz: banner "sem internet" no topo, acima de todas as
 * telas (RF13), e a navegação ocupando o resto.
 *
 * Quando o banner aparece, ele ocupa a área da barra de status; por isso essa área
 * é "consumida" para as telas abaixo não somarem o mesmo espaço de novo.
 */
@Composable
fun KeveOQAppContent(
    isOnline: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        AnimatedVisibility(visible = !isOnline) {
            OfflineBanner(modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars))
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .then(if (isOnline) Modifier else Modifier.consumeWindowInsets(WindowInsets.statusBars)),
        ) {
            content()
        }
    }
}

@PreviewLightDark
@Composable
private fun KeveOQAppContentOfflinePreview() {
    KeveOQTheme {
        KeveOQAppContent(isOnline = false) {}
    }
}
