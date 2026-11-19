package com.radierdev.keveoq.data.repository

import com.radierdev.keveoq.core.result.AppError
import com.radierdev.keveoq.core.result.AppResult
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

/**
 * Executa uma chamada de rede e converte qualquer exceção em [AppError] (seção 7.1).
 *
 * A ordem dos `catch` importa:
 * - [CancellationException] é relançada primeiro: cancelar a coroutine (por exemplo,
 *   ao sair da tela) não é erro, e engoli-la quebraria o cancelamento estruturado.
 * - [IOException] cobre sem conexão, timeout e DNS.
 * - [HttpException] é lançada pelo Retrofit para respostas 4xx e 5xx.
 * - [SerializationException] indica JSON que não pôde ser convertido.
 * - Qualquer outra exceção vira [AppError.Unknown].
 */
suspend fun <T> safeApiCall(call: suspend () -> T): AppResult<T> =
    try {
        AppResult.Success(call())
    } catch (e: CancellationException) {
        throw e
    } catch (e: IOException) {
        AppResult.Failure(AppError.Network)
    } catch (e: HttpException) {
        AppResult.Failure(AppError.Http(e.code()))
    } catch (e: SerializationException) {
        AppResult.Failure(AppError.Serialization)
    } catch (e: Exception) {
        AppResult.Failure(AppError.Unknown)
    }
