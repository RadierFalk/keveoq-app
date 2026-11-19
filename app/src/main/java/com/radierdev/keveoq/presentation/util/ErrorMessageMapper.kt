package com.radierdev.keveoq.presentation.util

import androidx.annotation.StringRes
import com.radierdev.keveoq.R
import com.radierdev.keveoq.core.result.AppError

/**
 * Traduz um [AppError] na mensagem mostrada ao usuário (seção 7.2 do planejamento).
 *
 * Devolve o ID do texto em `strings.xml`, e não o texto em si: assim o ViewModel
 * não precisa de `Context`, e a tela resolve o texto com `stringResource`.
 * As mensagens dizem o que aconteceu e como resolver, sem códigos nem termos técnicos (CA13).
 */
object ErrorMessageMapper {

    @StringRes
    fun messageFor(error: AppError): Int = when (error) {
        AppError.Network -> R.string.error_network
        is AppError.Http -> when (error.code) {
            401 -> R.string.error_unauthorized
            in 500..599 -> R.string.error_server
            else -> R.string.error_generic
        }
        AppError.Serialization -> R.string.error_generic
        AppError.Unknown -> R.string.error_generic
    }
}
