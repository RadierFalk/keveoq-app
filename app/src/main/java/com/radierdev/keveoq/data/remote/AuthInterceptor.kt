package com.radierdev.keveoq.data.remote

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Acrescenta o token do TMDB (API Read Access Token) a toda requisição,
 * no cabeçalho `Authorization: Bearer <token>`.
 *
 * O token vem de `BuildConfig.TMDB_API_KEY`, gerado a partir do `local.properties`,
 * e é recebido pelo construtor: assim esta classe não conhece o BuildConfig e o
 * token nunca é escrito no código. Por estar no cabeçalho, ele não aparece na URL.
 */
class AuthInterceptor(private val token: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header(HEADER_AUTHORIZATION, "Bearer $token")
            .build()
        return chain.proceed(request)
    }

    companion object {
        const val HEADER_AUTHORIZATION = "Authorization"
    }
}
