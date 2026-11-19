package com.radierdev.keveoq.domain.model

/** Resultado do sorteio de um filme (RN12). */
sealed interface RandomMovieResult {

    /** Filme sorteado e o humor de onde ele veio. */
    data class Found(val movieId: Int, val mood: Mood) : RandomMovieResult

    /** Nenhum humor tinha filmes disponíveis (ex.: offline e sem cache). */
    data object NoMovieAvailable : RandomMovieResult
}
