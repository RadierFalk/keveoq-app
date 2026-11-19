package com.radierdev.keveoq.domain.model

import com.radierdev.keveoq.core.result.AppError

// Resultados das ações da lista "Quero assistir".
// As recusas de regra de negócio (AlreadyInList, NotWatched...) não são falhas técnicas:
// são respostas válidas das regras. Já `Failure` carrega uma falha técnica (AppError),
// por exemplo um erro ao gravar no banco. Quem chama trata cada caso explicitamente.

/** Resultado de adicionar um filme à lista. */
sealed interface AddToWatchlistResult {
    data object Added : AddToWatchlistResult
    data object AlreadyInList : AddToWatchlistResult
    data class Failure(val error: AppError) : AddToWatchlistResult
}

/** Resultado de marcar um filme como assistido. */
sealed interface MarkAsWatchedResult {
    data object Marked : MarkAsWatchedResult
    data object AlreadyWatched : MarkAsWatchedResult
    data object NotInList : MarkAsWatchedResult
    data class Failure(val error: AppError) : MarkAsWatchedResult
}

/** Resultado de avaliar um filme. */
sealed interface RateMovieResult {
    data object Rated : RateMovieResult
    data object InvalidRating : RateMovieResult
    data object NotWatched : RateMovieResult
    data class Failure(val error: AppError) : RateMovieResult
}
