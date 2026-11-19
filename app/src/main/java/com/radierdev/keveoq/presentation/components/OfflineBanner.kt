package com.radierdev.keveoq.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.radierdev.keveoq.R
import com.radierdev.keveoq.presentation.theme.Dimens
import com.radierdev.keveoq.presentation.theme.KeveOQTheme

/**
 * Banner "sem internet" (RF13), em `tertiaryContainer` conforme a identidade visual.
 * Quem decide quando mostrar é o `KeveOQAppContent`, a partir do estado da conexão.
 */
@Composable
fun OfflineBanner(modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = Dimens.ScreenHorizontalPadding,
                vertical = Dimens.MediumSpacing,
            ),
            horizontalArrangement = Arrangement.spacedBy(Dimens.MediumSpacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Ícone decorativo: o texto ao lado já informa a situação.
            Icon(
                painter = painterResource(R.drawable.ic_cloud_off),
                contentDescription = null,
                modifier = Modifier.size(Dimens.IconSmall),
            )
            Text(text = stringResource(R.string.offline_banner), style = MaterialTheme.typography.labelMedium)
        }
    }
}

@PreviewLightDark
@Composable
private fun OfflineBannerPreview() {
    KeveOQTheme { OfflineBanner() }
}
