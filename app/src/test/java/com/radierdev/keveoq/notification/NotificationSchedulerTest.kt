package com.radierdev.keveoq.notification

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.radierdev.keveoq.domain.usecase.CalculateNotificationDelayUseCase
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Clock
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId

class NotificationSchedulerTest {

    private val zone = ZoneId.of("America/Sao_Paulo")
    private val workManager = mockk<WorkManager>(relaxed = true)

    private fun schedulerAt(localDateTime: LocalDateTime): NotificationScheduler {
        val clock = Clock.fixed(localDateTime.atZone(zone).toInstant(), zone)
        return NotificationScheduler(workManager, CalculateNotificationDelayUseCase(clock))
    }

    private fun scheduledRequest(): Pair<ExistingPeriodicWorkPolicy, PeriodicWorkRequest> {
        val policy = slot<ExistingPeriodicWorkPolicy>()
        val request = slot<PeriodicWorkRequest>()
        verify { workManager.enqueueUniquePeriodicWork("movie_suggestion", capture(policy), capture(request)) }
        return policy.captured to request.captured
    }

    @Test
    fun `deve agendar com nome unico e politica keep para nao duplicar`() {
        schedulerAt(LocalDateTime.of(2026, 11, 19, 14, 0)).schedule()

        val (policy, _) = scheduledRequest()
        assertEquals(ExistingPeriodicWorkPolicy.KEEP, policy)
    }

    @Test
    fun `deve repetir a cada 4 dias`() {
        schedulerAt(LocalDateTime.of(2026, 11, 19, 14, 0)).schedule()

        val (_, request) = scheduledRequest()
        assertEquals(Duration.ofDays(4).toMillis(), request.workSpec.intervalDuration)
    }

    @Test
    fun `agendado as 14h a primeira execucao deve esperar ate as 19h`() {
        schedulerAt(LocalDateTime.of(2026, 11, 19, 14, 0)).schedule()

        val (_, request) = scheduledRequest()
        assertEquals(Duration.ofHours(5).toMillis(), request.workSpec.initialDelay)
    }

    @Test
    fun `agendado as 22h a primeira execucao deve esperar ate as 19h do dia seguinte`() {
        schedulerAt(LocalDateTime.of(2026, 11, 19, 22, 0)).schedule()

        val (_, request) = scheduledRequest()
        assertEquals(Duration.ofHours(21).toMillis(), request.workSpec.initialDelay)
    }

    @Test
    fun `deve agendar o worker de sugestao de filmes`() {
        schedulerAt(LocalDateTime.of(2026, 11, 19, 20, 0)).schedule()

        val (_, request) = scheduledRequest()
        assertEquals(MovieSuggestionWorker::class.java.name, request.workSpec.workerClassName)
        assertEquals(0L, request.workSpec.initialDelay)
    }
}
