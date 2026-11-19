package com.radierdev.keveoq.core.result

/**
 * Falhas técnicas (de infraestrutura) que uma operação pode ter.
 *
 * Recusas de regra de negócio (por exemplo, filme já na lista) não entram aqui:
 * os casos de uso têm tipos de resultado próprios para elas.
 */
sealed interface AppError {

    /** Sem conexão, timeout ou falha de DNS. */
    data object Network : AppError

    /** O servidor respondeu com um código HTTP de erro (4xx ou 5xx). */
    data class Http(val code: Int) : AppError

    /** A resposta chegou, mas não pôde ser convertida para os modelos do app. */
    data object Serialization : AppError

    /** Qualquer outra falha inesperada. */
    data object Unknown : AppError
}
