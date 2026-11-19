package com.radierdev.keveoq.data.local

import androidx.room.TypeConverter
import com.radierdev.keveoq.data.local.entity.StoredWatchProviders
import kotlinx.serialization.json.Json

/**
 * Conversões de tipos que o Room não sabe gravar sozinho.
 *
 * Gêneros e "onde assistir" são só exibidos, nunca filtrados no banco, então
 * gravá-los como texto JSON (`["Drama","Romance"]`) é suficiente e evita tabelas extras.
 */
class Converters {

    @TypeConverter
    fun fromStringList(value: List<String>): String = json.encodeToString(value)

    @TypeConverter
    fun toStringList(value: String): List<String> = json.decodeFromString(value)

    @TypeConverter
    fun fromWatchProviders(value: StoredWatchProviders): String = json.encodeToString(value)

    @TypeConverter
    fun toWatchProviders(value: String): StoredWatchProviders = json.decodeFromString(value)

    private companion object {
        // ignoreUnknownKeys: um JSON gravado por uma versão futura do app não quebra a leitura.
        val json = Json { ignoreUnknownKeys = true }
    }
}
