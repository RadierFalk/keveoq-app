package com.radierdev.keveoq.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Versão 1 → 2: "onde assistir" (decisão 16 do planejamento).
 *
 * - Acrescenta as colunas sem apagar nada: a lista "Quero assistir" e as
 *   avaliações de quem já usa o app são preservadas.
 * - Marca os detalhes já guardados como expirados (`updatedAt = 0`), para que sejam
 *   buscados de novo, já com os serviços, na próxima abertura com internet.
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE movie_details ADD COLUMN watchProviders TEXT NOT NULL DEFAULT '{}'")
        db.execSQL("ALTER TABLE movie_details ADD COLUMN theatricalReleaseBr INTEGER")
        db.execSQL("UPDATE movie_details SET updatedAt = 0")
    }
}
