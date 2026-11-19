package com.radierdev.keveoq.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.radierdev.keveoq.data.local.dao.MovieDao
import com.radierdev.keveoq.data.local.dao.SavedMovieDao
import com.radierdev.keveoq.data.local.entity.MoodRecommendationEntity
import com.radierdev.keveoq.data.local.entity.MovieDetailsEntity
import com.radierdev.keveoq.data.local.entity.MovieEntity
import com.radierdev.keveoq.data.local.entity.SavedMovieEntity

/**
 * Banco local `keveoq.db`, a única fonte de verdade do app (seção 5 do planejamento).
 *
 * Versão 2: colunas de "onde assistir" em `movie_details` (ver [MIGRATION_1_2]).
 * `exportSchema = false`: o esquema não é exportado em arquivo. A migração
 * destrutiva continua configurada só como rede de segurança, para versões sem migração.
 */
@Database(
    entities = [
        MovieEntity::class,
        MoodRecommendationEntity::class,
        MovieDetailsEntity::class,
        SavedMovieEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao

    abstract fun savedMovieDao(): SavedMovieDao

    companion object {
        const val NAME = "keveoq.db"
    }
}
