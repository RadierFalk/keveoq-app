package com.radierdev.keveoq.domain.model

// Resultados de regra de negócio da lista "Quero assistir".
// Não são falhas técnicas (AppError): são respostas válidas das regras,
// e cada caso precisa ser tratado explicitamente por quem chama.

/** Resultado de adicionar um filme à lista. */
sealed interface AddToWatchlistResult {
    data object Added : AddToWatchlistResult
    data object AlreadyInList : AddToWatchlistResult
}

/** Resultado de marcar um filme como assistido. */
sealed interface MarkAsWatchedResult {
    data object Marked : MarkAsWatchedResult
    data object AlreadyWatched : MarkAsWatchedResult
    data object NotInList : MarkAsWatchedResult
}

/** Resultado de avaliar um filme. */
sealed interface RateMovieResult {
    data object Rated : RateMovieResult
    data object InvalidRating : RateMovieResult
    data object NotWatched : RateMovieResult
}
