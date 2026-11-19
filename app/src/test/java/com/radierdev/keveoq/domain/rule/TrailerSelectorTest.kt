package com.radierdev.keveoq.domain.rule

import com.radierdev.keveoq.domain.model.Video
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TrailerSelectorTest {

    private fun video(
        key: String,
        site: String = "YouTube",
        type: String = "Trailer",
        official: Boolean = false,
    ) = Video(key = key, site = site, type = type, official = official)

    @Test
    fun `deve priorizar o trailer oficial`() {
        val videos = listOf(video("nao-oficial"), video("oficial", official = true))

        assertEquals("oficial", TrailerSelector.selectKey(videos))
    }

    @Test
    fun `deve usar trailer nao oficial quando nao houver oficial`() {
        val videos = listOf(video("teaser", type = "Teaser", official = true), video("trailer-fa"))

        assertEquals("trailer-fa", TrailerSelector.selectKey(videos))
    }

    @Test
    fun `deve ignorar teaser`() {
        val videos = listOf(video("teaser", type = "Teaser", official = true))

        assertNull(TrailerSelector.selectKey(videos))
    }

    @Test
    fun `deve ignorar videos de outro site`() {
        val videos = listOf(video("vimeo", site = "Vimeo", official = true), video("youtube"))

        assertEquals("youtube", TrailerSelector.selectKey(videos))
    }

    @Test
    fun `deve retornar nulo quando nao houver video valido`() {
        val videos = listOf(
            video("teaser", type = "Teaser"),
            video("vimeo", site = "Vimeo"),
            video("clip", type = "Clip", official = true),
        )

        assertNull(TrailerSelector.selectKey(videos))
    }

    @Test
    fun `deve retornar nulo para lista vazia`() {
        assertNull(TrailerSelector.selectKey(emptyList()))
    }

    @Test
    fun `deve escolher o primeiro entre varios trailers oficiais`() {
        val videos = listOf(
            video("nao-oficial"),
            video("oficial-1", official = true),
            video("oficial-2", official = true),
        )

        assertEquals("oficial-1", TrailerSelector.selectKey(videos))
    }

    @Test
    fun `deve ignorar video com chave em branco`() {
        val videos = listOf(video(" ", official = true), video("valido"))

        assertEquals("valido", TrailerSelector.selectKey(videos))
    }
}
