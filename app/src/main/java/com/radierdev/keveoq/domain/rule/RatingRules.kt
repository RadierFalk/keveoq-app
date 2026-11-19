package com.radierdev.keveoq.domain.rule

/**
 * Validação da avaliação pessoal (RN08): inteiro de 1 a 5 estrelas.
 *
 * A outra parte da RN08 (só filmes assistidos podem ser avaliados)
 * depende do filme salvo e é verificada no caso de uso de avaliação.
 */
object RatingRules {

    const val MIN_RATING = 1
    const val MAX_RATING = 5

    fun isValid(rating: Int): Boolean = rating in MIN_RATING..MAX_RATING
}
