package com.radierdev.keveoq.presentation.navigation

import kotlinx.serialization.Serializable

// Rotas type-safe do Navigation Compose (seção 9.1 do planejamento).
// Cada rota é uma classe serializável: o compilador confere os argumentos,
// em vez de montar strings como "details/123" à mão.

/** Seleção de humor: destino inicial. */
@Serializable
data object MoodRoute

/** Recomendações. O humor vai como o nome do enum ("HAPPY"), um tipo simples. */
@Serializable
data class RecommendationsRoute(val mood: String)

/** Detalhes de um filme. */
@Serializable
data class DetailsRoute(val movieId: Int)

/** Lista "Quero assistir". */
@Serializable
data object WatchlistRoute

/** Tela "Sobre". */
@Serializable
data object AboutRoute
