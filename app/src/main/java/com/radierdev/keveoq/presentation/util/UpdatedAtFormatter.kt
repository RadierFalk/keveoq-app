package com.radierdev.keveoq.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.radierdev.keveoq.R
import java.time.Duration
import java.time.Instant

/**
 * Há quanto tempo as recomendações foram atualizadas (RF03, CA32).
 * A decisão da faixa é Kotlin puro e testável; o texto é resolvido na tela.
 */
@Immutable
sealed interface UpdatedAtText {
    data object JustNow : UpdatedAtText
    data class MinutesAgo(val minutes: Int) : UpdatedAtText
    data class HoursAgo(val hours: Int) : UpdatedAtText
    data class DaysAgo(val days: Int) : UpdatedAtText
}

object UpdatedAtFormatter {

    /**
     * Faixas (seção 6.2): menos de 1 min → "agora"; menos de 1 h → minutos;
     * menos de 24 h → horas; a partir daí → dias. Datas no futuro contam como "agora".
     */
    fun format(updatedAt: Instant, now: Instant): UpdatedAtText {
        val elapsed = Duration.between(updatedAt, now)
        return when {
            elapsed < Duration.ofMinutes(1) -> UpdatedAtText.JustNow
            elapsed < Duration.ofHours(1) -> UpdatedAtText.MinutesAgo(elapsed.toMinutes().toInt())
            elapsed < Duration.ofDays(1) -> UpdatedAtText.HoursAgo(elapsed.toHours().toInt())
            else -> UpdatedAtText.DaysAgo(elapsed.toDays().toInt())
        }
    }
}

/** Resolve o texto em português, com plural correto ("1 dia", "2 dias"). */
@Composable
fun UpdatedAtText.asString(): String = when (this) {
    UpdatedAtText.JustNow -> stringResource(R.string.updated_just_now)
    is UpdatedAtText.MinutesAgo -> pluralStringResource(R.plurals.updated_minutes_ago, minutes, minutes)
    is UpdatedAtText.HoursAgo -> pluralStringResource(R.plurals.updated_hours_ago, hours, hours)
    is UpdatedAtText.DaysAgo -> pluralStringResource(R.plurals.updated_days_ago, days, days)
}
