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
 * `exportSchema = false`: o banco nasce na versão 1 e não há migrações;
 * se o esquema mudar durante o desenvolvimento, usa-se migração destrutiva.
 */
@Database(
    entities = [
        MovieEntity::class,
        MoodRecommendationEntity::class,
        MovieDetailsEntity::class,
        SavedMovieEntity::class,
    ],
    version = 1,
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
