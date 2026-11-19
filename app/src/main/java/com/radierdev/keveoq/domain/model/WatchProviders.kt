package com.radierdev.keveoq.domain.model

/** Um serviço onde o filme pode ser assistido (ex.: Netflix, Apple TV). */
data class WatchProvider(
    val id: Int,
    val name: String,
    val logoUrl: String?,
)

/**
 * Onde assistir o filme no Brasil (RN13), por tipo de disponibilidade.
 *
 * @property link página do filme no TMDB/JustWatch com os links de cada serviço,
 * ou `null` se a API não informar.
 */
data class WatchProviders(
    val subscription: List<WatchProvider> = emptyList(),
    val rent: List<WatchProvider> = emptyList(),
    val buy: List<WatchProvider> = emptyList(),
    val link: String? = null,
) {
    /** Nenhum serviço em nenhum grupo. */
    val isEmpty: Boolean get() = subscription.isEmpty() && rent.isEmpty() && buy.isEmpty()

    companion object {
        val EMPTY = WatchProviders()
    }
}
