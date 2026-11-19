package com.radierdev.keveoq.domain.usecase

import com.radierdev.keveoq.domain.rule.NotificationWindow
import java.time.Clock
import java.time.Duration
import java.time.LocalDateTime

/**
 * Calcula o atraso até a primeira notificação de sugestão (RF17, RN10).
 *
 * Lê a hora atual do [Clock] injetado (no fuso do aparelho em produção,
 * fixo nos testes) e aplica a [NotificationWindow]. O agendamento com
 * WorkManager usa este valor como `initialDelay`.
 */
class CalculateNotificationDelayUseCase(
    private val clock: Clock,
) {
    operator fun invoke(): Duration =
        NotificationWindow.delayUntilNextWindow(LocalDateTime.now(clock))
}
