package com.radierdev.keveoq.domain.rule

import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Janela diária da notificação de sugestão: das 19h às 21h (RN10).
 *
 * - Antes das 19h: espera até as 19h do mesmo dia.
 * - Das 19h até antes das 21h: já está na janela, atraso zero.
 * - A partir das 21h: espera até as 19h do dia seguinte.
 */
object NotificationWindow {

    val WINDOW_START: LocalTime = LocalTime.of(19, 0)
    val WINDOW_END: LocalTime = LocalTime.of(21, 0)

    /** @param now data e hora locais do momento do agendamento. */
    fun delayUntilNextWindow(now: LocalDateTime): Duration {
        val time = now.toLocalTime()
        val nextStart = when {
            time < WINDOW_START -> now.toLocalDate().atTime(WINDOW_START)
            time < WINDOW_END -> return Duration.ZERO
            else -> now.toLocalDate().plusDays(1).atTime(WINDOW_START)
        }
        return Duration.between(now, nextStart)
    }
}
