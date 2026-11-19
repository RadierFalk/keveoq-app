package com.radierdev.keveoq.data.local.entity

import kotlinx.serialization.Serializable

/**
 * Formato em que "onde assistir" é gravado na coluna `watchProviders` de
 * `movie_details` (texto JSON, como os gêneros). São só exibidos, nunca filtrados
 * no banco, então não justificam tabelas próprias.
 *
 * Todos os campos têm valor padrão: o JSON `{}` (valor padrão da coluna criada
 * pela migração 1→2) é lido como "nenhum serviço".
 */
@Serializable
data class StoredWatchProviders(
    val subscription: List<StoredProvider> = emptyList(),
    val rent: List<StoredProvider> = emptyList(),
    val buy: List<StoredProvider> = emptyList(),
    val link: String? = null,
)

@Serializable
data class StoredProvider(
    val id: Int,
    val name: String,
    val logoUrl: String? = null,
)
