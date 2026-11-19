package com.radierdev.keveoq.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

/**
 * Player do YouTube incorporado (android-youtube-player), usado no Compose por meio
 * de `AndroidView`, porque a biblioteca fornece uma View tradicional.
 *
 * - O vídeo é apenas preparado (`cueVideo`): só toca quando o usuário aperta play.
 * - A View acompanha o ciclo de vida da tela (pausa ao sair) e é liberada quando o
 *   componente sai da composição, para não vazar memória.
 * - Se o player não conseguir reproduzir (vídeo bloqueado para incorporação, por
 *   exemplo), avisa por [onError] para a tela mostrar "Assistir no YouTube" (RF22).
 */
@Composable
fun TrailerPlayer(
    videoKey: String,
    onError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentOnError by rememberUpdatedState(onError)

    // Uma View por vídeo: se a chave mudar, um novo player é criado.
    val playerView = remember(videoKey) {
        YouTubePlayerView(context).apply {
            enableAutomaticInitialization = false
            val options = IFramePlayerOptions.Builder(context)
                .controls(1)
                .rel(0) // ao final, não sugerir vídeos de outros canais
                .build()
            initialize(
                object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.cueVideo(videoKey, 0f)
                    }

                    override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                        currentOnError()
                    }
                },
                options,
            )
        }
    }

    DisposableEffect(lifecycle, playerView) {
        lifecycle.addObserver(playerView)
        onDispose {
            lifecycle.removeObserver(playerView)
            playerView.release()
        }
    }

    AndroidView(
        factory = { playerView },
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium),
    )
}
