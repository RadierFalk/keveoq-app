package com.radierdev.keveoq.domain.rule

import com.radierdev.keveoq.domain.model.Video

/**
 * Escolhe o trailer de um filme entre os vídeos devolvidos pela API (RN09).
 *
 * Só valem vídeos do YouTube do tipo "Trailer", com chave preenchida.
 * Entre eles, o oficial tem prioridade; se não houver oficial, fica o
 * primeiro válido na ordem da API. O fallback de idioma (pt-BR → en-US)
 * fica no caso de uso, que chama este seletor uma vez para cada idioma.
 */
object TrailerSelector {

    const val SITE_YOUTUBE = "YouTube"
    const val TYPE_TRAILER = "Trailer"

    /** @return a chave do trailer no YouTube, ou `null` se não houver vídeo válido. */
    fun selectKey(videos: List<Video>): String? {
        val trailers = videos.filter { video ->
            video.site == SITE_YOUTUBE &&
                video.type == TYPE_TRAILER &&
                video.key.isNotBlank() // proteção: chave vazia geraria um player quebrado
        }
        val chosen = trailers.firstOrNull { it.official } ?: trailers.firstOrNull()
        return chosen?.key
    }
}
