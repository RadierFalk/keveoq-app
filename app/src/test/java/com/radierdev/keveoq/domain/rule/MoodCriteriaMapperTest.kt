package com.radierdev.keveoq.domain.rule

import com.radierdev.keveoq.domain.model.Mood
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

// Os IDs esperados são escritos como números, copiados da tabela do CLAUDE.md,
// para que um erro nas constantes de TmdbGenre seja detectado aqui.
class MoodCriteriaMapperTest {

    @Test
    fun `humor feliz deve buscar comedia animacao e familia`() {
        assertEquals(setOf(35, 16, 10751), MoodCriteriaMapper.map(Mood.HAPPY).withGenres)
    }

    @Test
    fun `humor triste deve buscar drama e romance`() {
        assertEquals(setOf(18, 10749), MoodCriteriaMapper.map(Mood.SAD).withGenres)
    }

    @Test
    fun `humor animado deve buscar acao e aventura`() {
        assertEquals(setOf(28, 12), MoodCriteriaMapper.map(Mood.EXCITED).withGenres)
    }

    @Test
    fun `humor ansioso deve buscar comedia animacao e familia`() {
        assertEquals(setOf(35, 16, 10751), MoodCriteriaMapper.map(Mood.ANXIOUS).withGenres)
    }

    @Test
    fun `humor ansioso deve excluir terror e thriller`() {
        assertEquals(setOf(27, 53), MoodCriteriaMapper.map(Mood.ANXIOUS).withoutGenres)
    }

    @Test
    fun `humor entediado deve buscar misterio ficcao cientifica e thriller`() {
        assertEquals(setOf(9648, 878, 53), MoodCriteriaMapper.map(Mood.BORED).withGenres)
    }

    @Test
    fun `humor romantico deve buscar romance`() {
        assertEquals(setOf(10749), MoodCriteriaMapper.map(Mood.ROMANTIC).withGenres)
    }

    @Test
    fun `humor nostalgico deve buscar aventura e familia`() {
        assertEquals(setOf(12, 10751), MoodCriteriaMapper.map(Mood.NOSTALGIC).withGenres)
    }

    @Test
    fun `humor nostalgico deve buscar apenas filmes lancados ate 31 de dezembro de 1999`() {
        assertEquals(LocalDate.of(1999, 12, 31), MoodCriteriaMapper.map(Mood.NOSTALGIC).releasedUntil)
    }

    @Test
    fun `humor corajoso deve buscar terror e thriller`() {
        assertEquals(setOf(27, 53), MoodCriteriaMapper.map(Mood.BRAVE).withGenres)
    }

    @Test
    fun `todos os humores devem exigir pelo menos 100 votos`() {
        Mood.entries.forEach { mood ->
            assertEquals("humor $mood", 100, MoodCriteriaMapper.map(mood).minVoteCount)
        }
    }

    @Test
    fun `humores sem filtro extra nao devem excluir generos nem limitar data`() {
        val moodsWithoutExtraFilter = Mood.entries - Mood.ANXIOUS - Mood.NOSTALGIC
        moodsWithoutExtraFilter.forEach { mood ->
            val criteria = MoodCriteriaMapper.map(mood)
            assertTrue("humor $mood não deveria excluir gêneros", criteria.withoutGenres.isEmpty())
            assertNull("humor $mood não deveria limitar a data", criteria.releasedUntil)
        }
    }

    @Test
    fun `humor ansioso nao deve limitar a data de lancamento`() {
        assertNull(MoodCriteriaMapper.map(Mood.ANXIOUS).releasedUntil)
    }

    @Test
    fun `humor nostalgico nao deve excluir generos`() {
        assertTrue(MoodCriteriaMapper.map(Mood.NOSTALGIC).withoutGenres.isEmpty())
    }
}
