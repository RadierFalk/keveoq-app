package com.radierdev.keveoq.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import coil3.compose.AsyncImage
import com.radierdev.keveoq.R
import com.radierdev.keveoq.presentation.theme.Dimens
import com.radierdev.keveoq.presentation.theme.KeveOQTheme

/**
 * Pôster do filme carregado pelo Coil, que guarda as imagens em cache de disco.
 *
 * Por baixo da imagem fica sempre o marcador de "pôster indisponível"
 * (`surfaceContainer`, borda e ícone de filme em `outline`). Se a imagem carregar,
 * ela o cobre; se não houver URL ou não der para carregar (por exemplo, offline
 * sem cache), o marcador continua visível. Assim não é preciso tratar cada estado do Coil.
 */
@Composable
fun PosterImage(
    posterUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small,
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(Dimens.BorderWidth, MaterialTheme.colorScheme.outline, shape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_movie),
            contentDescription = if (posterUrl == null) stringResource(R.string.poster_unavailable) else null,
            tint = MaterialTheme.colorScheme.outline,
        )
        if (posterUrl != null) {
            AsyncImage(
                model = posterUrl,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PosterImageUnavailablePreview() {
    KeveOQTheme {
        PosterImage(
            posterUrl = null,
            contentDescription = null,
            modifier = Modifier.size(Dimens.PosterListWidth, Dimens.PosterListHeight),
        )
    }
}
