package com.radierdev.keveoq.di

import androidx.room.Room
import com.radierdev.keveoq.BuildConfig
import com.radierdev.keveoq.data.local.AppDatabase
import com.radierdev.keveoq.data.local.MIGRATION_1_2
import com.radierdev.keveoq.data.remote.AuthInterceptor
import com.radierdev.keveoq.data.remote.TmdbApi
import com.radierdev.keveoq.data.repository.MovieRepositoryImpl
import com.radierdev.keveoq.data.repository.SavedMovieRepositoryImpl
import com.radierdev.keveoq.domain.repository.MovieRepository
import com.radierdev.keveoq.domain.repository.SavedMovieRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

/** API do TMDB, banco Room, DAOs e repositórios. */
val dataModule = module {

    single {
        Json {
            ignoreUnknownKeys = true // o TMDB devolve muitos campos que o app não usa
            coerceInputValues = true // `null` em campo não nulo usa o valor padrão do DTO
        }
    }

    single {
        val builder = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(BuildConfig.TMDB_API_KEY))
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)

        // Log das requisições só no build de depuração, com o token oculto.
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                    redactHeader(AuthInterceptor.HEADER_AUTHORIZATION)
                },
            )
        }
        builder.build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(TmdbApi.BASE_URL)
            .client(get())
            .addConverterFactory(get<Json>().asConverterFactory("application/json".toMediaType()))
            .build()
    }

    single<TmdbApi> { get<Retrofit>().create(TmdbApi::class.java) }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, AppDatabase.NAME)
            // 1 → 2 preserva os dados (lista "Quero assistir" e notas).
            .addMigrations(MIGRATION_1_2)
            // Rede de segurança: só é usada se faltar uma migração.
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
    single { get<AppDatabase>().movieDao() }
    single { get<AppDatabase>().savedMovieDao() }

    // Repositórios: registrados pela interface do domain, para que quem
    // pede um MovieRepository não saiba que existe Room ou Retrofit por trás.
    single<MovieRepository> {
        MovieRepositoryImpl(api = get(), movieDao = get(), clock = get(), dispatchers = get())
    }
    single<SavedMovieRepository> {
        SavedMovieRepositoryImpl(savedMovieDao = get(), dispatchers = get())
    }
}
