package com.radierdev.keveoq.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.radierdev.keveoq.R
import com.radierdev.keveoq.presentation.theme.Dimens
import com.radierdev.keveoq.presentation.theme.KeveOQTheme

/**
 * Item de filme usado nas listas de recomendações e "Quero assistir":
 * pôster 72×108 dp, título (`titleLarge`), ano e nota (`bodySmall`, `onSurfaceVariant`).
 *
 * Recebe valores simples (e não o modelo do domain), o que o torna fácil de
 * reaproveitar e estável para o Compose. [extraContent] permite que a lista
 * "Quero assistir" acrescente status, estrelas e o botão "Já assisti".
 */
@Composable
fun MovieListItem(
    title: String,
    releaseYear: Int?,
    posterUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    voteAverage: Double? = null,
    extraContent: @Composable ColumnScope.() -> Unit = {},
) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(Dimens.ContentPadding),
            horizontalArrangement = Arrangement.spacedBy(Dimens.ItemSpacing),
        ) {
            PosterImage(
                posterUrl = posterUrl,
                contentDescription = null, // o título ao lado já identifica o filme
                modifier = Modifier.size(Dimens.PosterListWidth, Dimens.PosterListHeight),
            )
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SmallSpacing)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.MediumSpacing),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (releaseYear != null) {
                        Text(
                            text = releaseYear.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (voteAverage != null) {
                        VoteAverage(voteAverage)
                    }
                }
                extraContent()
            }
        }
    }
}

/** Nota média do TMDB (0 a 10), com uma casa decimal no formato do aparelho ("7,5"). */
@Composable
private fun VoteAverage(voteAverage: Double) {
    val description = stringResource(R.string.vote_average_description, voteAverage)
    Row(
        modifier = Modifier.semantics(mergeDescendants = true) { contentDescription = description },
        horizontalArrangement = Arrangement.spacedBy(Dimens.SmallSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Rounded.Star,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(Dimens.IconSmall),
        )
        Text(
            text = stringResource(R.string.vote_average, voteAverage),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@PreviewLightDark
@Composable
private fun MovieListItemPreview() {
    KeveOQTheme {
        Surface {
            Column(
                modifier = Modifier.padding(Dimens.ScreenHorizontalPadding),
                verticalArrangement = Arrangement.spacedBy(Dimens.ItemSpacing),
            ) {
                MovieListItem(
                    title = "Divertida Mente 2",
                    releaseYear = 2024,
                    posterUrl = null,
                    voteAverage = 7.6,
                    onClick = {},
                )
                MovieListItem(
                    title = "Um filme com um título bem comprido para testar a quebra de linha",
                    releaseYear = 1999,
                    posterUrl = null,
                    onClick = {},
                ) {
                    RatingBar(rating = 4)
                }
            }
        }
    }
}
