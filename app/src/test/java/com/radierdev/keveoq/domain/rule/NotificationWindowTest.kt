package com.radierdev.keveoq.domain.rule

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Duration
import java.time.LocalDateTime

class NotificationWindowTest {

    private fun at(year: Int, month: Int, day: Int, hour: Int, minute: Int = 0) =
        LocalDateTime.of(year, month, day, hour, minute)

    @Test
    fun `agendamento as 14h deve esperar ate as 19h do mesmo dia`() {
        val delay = NotificationWindow.delayUntilNextWindow(at(2026, 11, 19, 14))

        assertEquals(Duration.ofHours(5), delay)
    }

    @Test
    fun `agendamento as 19h deve ser imediato`() {
        val delay = NotificationWindow.delayUntilNextWindow(at(2026, 11, 19, 19))

        assertEquals(Duration.ZERO, delay)
    }

    @Test
    fun `agendamento as 20h59 deve ser imediato`() {
        val delay = NotificationWindow.delayUntilNextWindow(at(2026, 11, 19, 20, 59))

        assertEquals(Duration.ZERO, delay)
    }

    @Test
    fun `agendamento as 21h deve esperar ate as 19h do dia seguinte`() {
        val delay = NotificationWindow.delayUntilNextWindow(at(2026, 11, 19, 21))

        assertEquals(Duration.ofHours(22), delay)
    }

    @Test
    fun `agendamento as 22h deve esperar ate as 19h do dia seguinte`() {
        val delay = NotificationWindow.delayUntilNextWindow(at(2026, 11, 19, 22))

        assertEquals(Duration.ofHours(21), delay)
    }

    @Test
    fun `agendamento a meia noite deve esperar ate as 19h do mesmo dia`() {
        val delay = NotificationWindow.delayUntilNextWindow(at(2026, 11, 20, 0))

        assertEquals(Duration.ofHours(19), delay)
    }

    @Test
    fun `agendamento no ultimo dia do mes apos 21h deve ir para o dia 1 do mes seguinte`() {
        val now = at(2026, 11, 30, 22)

        val delay = NotificationWindow.delayUntilNextWindow(now)

        assertEquals(at(2026, 12, 1, 19), now.plus(delay))
    }

    @Test
    fun `agendamento no ultimo dia do ano apos 21h deve ir para 1 de janeiro`() {
        val now = at(2026, 12, 31, 23, 30)

        val delay = NotificationWindow.delayUntilNextWindow(now)

        assertEquals(at(2027, 1, 1, 19), now.plus(delay))
    }
}
