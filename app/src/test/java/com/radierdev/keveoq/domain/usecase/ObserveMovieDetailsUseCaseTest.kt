package com.radierdev.keveoq.domain.usecase

import app.cash.turbine.test
import com.radierdev.keveoq.domain.fake.FakeMovieRepository
import com.radierdev.keveoq.domain.fake.TestData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ObserveMovieDetailsUseCaseTest {

    private val movieRepository = FakeMovieRepository()
    private val observeDetails = ObserveMovieDetailsUseCase(movieRepository)

    @Test
    fun `deve emitir nulo enquanto os detalhes nao foram buscados e depois os detalhes`() = runTest {
        observeDetails(10).test {
            assertNull(awaitItem())

            movieRepository.setDetails(TestData.details(10))

            assertEquals(10, awaitItem()?.id)
        }
    }
}
