package com.radierdev.keveoq.data.repository

import com.radierdev.keveoq.core.result.AppError
import com.radierdev.keveoq.core.result.AppResult
import kotlinx.coroutines.CancellationException

/**
 * Executa uma operação no banco local e converte falhas em [AppError.Unknown],
 * para que os repositórios nunca propaguem exceções.
 *
 * Exemplos de falha: disco cheio, banco corrompido, violação de chave primária.
 * Como em [safeApiCall], a [CancellationException] é relançada.
 */
suspend fun <T> safeDbCall(call: suspend () -> T): AppResult<T> =
    try {
        AppResult.Success(call())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        AppResult.Failure(AppError.Unknown)
    }
