package com.radierdev.keveoq.domain.usecase

import com.radierdev.keveoq.core.result.AppError
import com.radierdev.keveoq.core.result.AppResult
import com.radierdev.keveoq.domain.fake.FakeMovieRepository
import com.radierdev.keveoq.domain.fake.FakeNetworkMonitor
import com.radierdev.keveoq.domain.fake.TestData
import com.radierdev.keveoq.domain.model.Video
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Duration

class RefreshMovieDetailsUseCaseTest {

    private val movieRepository = FakeMovieRepository()
    private val networkMonitor = FakeNetworkMonitor(online = true)
    private val refreshDetails =
        RefreshMovieDetailsUseCase(movieRepository, networkMonitor, TestData.clock)

    private fun trailer(key: String, official: Boolean = true) =
        Video(key = key, site = "YouTube", type = "Trailer", official = official)

    private fun teaser(key: String) =
        Video(key = key, site = "YouTube", type = "Teaser", official = true)

    @Test
    fun `deve gravar os detalhes com o trailer em portugues`() = runTest {
        movieRepository.fetchDetailsResult = AppResult.Success(TestData.details(10))
        movieRepository.videosByLanguage["pt-BR"] = AppResult.Success(listOf(trailer("pt-trailer")))

        val result = refreshDetails(10)

        assertEquals(AppResult.Success(Unit), result)
        assertEquals("pt-trailer", movieRepository.storedDetails(10)?.trailerKey)
        assertEquals(listOf("pt-BR"), movieRepository.videoLanguagesRequested)
    }

    @Test
    fun `sem trailer em portugues deve usar o trailer em ingles`() = runTest {
        movieRepository.fetchDetailsResult = AppResult.Success(TestData.details(10))
        movieRepository.videosByLanguage["pt-BR"] = AppResult.Success(listOf(teaser("pt-teaser")))
        movieRepository.videosByLanguage["en-US"] = AppResult.Success(listOf(trailer("en-trailer")))

        refreshDetails(10)

        assertEquals("en-trailer", movieRepository.storedDetails(10)?.trailerKey)
        assertEquals(listOf("pt-BR", "en-US"), movieRepository.videoLanguagesRequested)
    }

    @Test
    fun `filme sem trailer valido deve ser gravado sem trailer`() = runTest {
        movieRepository.fetchDetailsResult = AppResult.Success(TestData.details(10))
        movieRepository.videosByLanguage["pt-BR"] = AppResult.Success(emptyList())
        movieRepository.videosByLanguage["en-US"] = AppResult.Success(listOf(teaser("en-teaser")))

        val result = refreshDetails(10)

        assertEquals(AppResult.Success(Unit), result)
        assertEquals(10, movieRepository.storedDetails(10)?.id)
        assertNull(movieRepository.storedDetails(10)?.trailerKey)
    }

    @Test
    fun `detalhes guardados ha menos de 24 horas nao devem chamar a api`() = runTest {
        movieRepository.setDetails(TestData.details(10, updatedAt = TestData.NOW.minus(Duration.ofHours(2))))

        val result = refreshDetails(10)

        assertEquals(AppResult.Success(Unit), result)
        assertEquals(0, movieRepository.fetchDetailsCalls)
    }

    @Test
    fun `detalhes guardados ha mais de 24 horas devem ser buscados de novo`() = runTest {
        movieRepository.setDetails(TestData.details(10, updatedAt = TestData.NOW.minus(Duration.ofHours(30))))
        movieRepository.fetchDetailsResult = AppResult.Success(TestData.details(10))

        refreshDetails(10)

        assertEquals(1, movieRepository.fetchDetailsCalls)
        assertEquals(TestData.NOW, movieRepository.storedDetails(10)?.updatedAt)
    }

    @Test
    fun `sem conexao e sem cache deve retornar erro de rede sem chamar a api`() = runTest {
        networkMonitor.isOnline.value = false

        val result = refreshDetails(10)

        assertEquals(AppResult.Failure(AppError.Network), result)
        assertEquals(0, movieRepository.fetchDetailsCalls)
    }

    @Test
    fun `sem conexao e com cache valido deve retornar sucesso`() = runTest {
        movieRepository.setDetails(TestData.details(10, updatedAt = TestData.NOW.minus(Duration.ofHours(1))))
        networkMonitor.isOnline.value = false

        assertEquals(AppResult.Success(Unit), refreshDetails(10))
    }

    @Test
    fun `falha ao buscar detalhes deve ser repassada sem gravar`() = runTest {
        movieRepository.fetchDetailsResult = AppResult.Failure(AppError.Http(404))

        val result = refreshDetails(10)

        assertEquals(AppResult.Failure(AppError.Http(404)), result)
        assertEquals(0, movieRepository.saveDetailsCalls)
    }

    @Test
    fun `falha ao gravar os detalhes deve ser repassada`() = runTest {
        movieRepository.fetchDetailsResult = AppResult.Success(TestData.details(10))
        movieRepository.saveDetailsFailure = AppError.Unknown

        assertEquals(AppResult.Failure(AppError.Unknown), refreshDetails(10))
        assertNull(movieRepository.storedDetails(10))
    }

    @Test
    fun `falha ao buscar videos deve ser repassada sem gravar os detalhes`() = runTest {
        movieRepository.fetchDetailsResult = AppResult.Success(TestData.details(10))
        movieRepository.videosByLanguage["pt-BR"] = AppResult.Failure(AppError.Network)

        val result = refreshDetails(10)

        assertEquals(AppResult.Failure(AppError.Network), result)
        assertEquals(0, movieRepository.saveDetailsCalls)
    }
}
