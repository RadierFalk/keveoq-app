package com.radierdev.keveoq.data.local

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

/**
 * Conversões de tipos que o Room não sabe gravar sozinho.
 *
 * Os gêneros são só exibidos, nunca filtrados no banco, então uma lista
 * gravada como texto JSON (`["Drama","Romance"]`) é suficiente e evita
 * uma tabela extra.
 */
class Converters {

    @TypeConverter
    fun fromStringList(value: List<String>): String = json.encodeToString(value)

    @TypeConverter
    fun toStringList(value: String): List<String> = json.decodeFromString(value)

    private companion object {
        val json = Json
    }
}
