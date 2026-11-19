package com.radierdev.keveoq.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.radierdev.keveoq.R
import com.radierdev.keveoq.presentation.theme.Dimens
import com.radierdev.keveoq.presentation.theme.KeveOQTheme

private const val MAX_STARS = 5

/**
 * Avaliação pessoal de 1 a 5 estrelas. Estrela cheia em `tertiary`, vazia em `outline`.
 *
 * - Com [onRate]: cada estrela é um `IconButton` (alvo de toque de 48 dp) que grava a nota.
 * - Sem [onRate]: só exibe a nota, em tamanho menor.
 */
@Composable
fun RatingBar(
    rating: Int?,
    modifier: Modifier = Modifier,
    onRate: ((Int) -> Unit)? = null,
) {
    val current = rating ?: 0
    if (onRate == null) {
        val description = if (rating == null) {
            stringResource(R.string.rating_none_description)
        } else {
            pluralStringResource(R.plurals.rating_description, rating, rating)
        }
        Row(
            modifier = modifier.semantics(mergeDescendants = true) { contentDescription = description },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            for (star in 1..MAX_STARS) {
                StarIcon(filled = star <= current, modifier = Modifier.size(Dimens.IconSmall))
            }
        }
    } else {
        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
            for (star in 1..MAX_STARS) {
                IconButton(onClick = { onRate(star) }) {
                    StarIcon(
                        filled = star <= current,
                        contentDescription = pluralStringResource(R.plurals.rating_action, star, star),
                    )
                }
            }
        }
    }
}

@Composable
private fun StarIcon(
    filled: Boolean,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    Icon(
        imageVector = Icons.Rounded.Star,
        contentDescription = contentDescription,
        tint = if (filled) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outline,
        modifier = modifier,
    )
}

@PreviewLightDark
@Composable
private fun RatingBarPreview() {
    KeveOQTheme {
        Surface {
            Column {
                RatingBar(rating = 3, onRate = {})
                RatingBar(rating = 4)
                RatingBar(rating = null)
            }
        }
    }
}
