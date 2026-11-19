package com.radierdev.keveoq.di

import androidx.room.Room
import com.radierdev.keveoq.BuildConfig
import com.radierdev.keveoq.data.local.AppDatabase
import com.radierdev.keveoq.data.remote.AuthInterceptor
import com.radierdev.keveoq.data.remote.TmdbApi
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
            // Sem migrações: se o esquema mudar em desenvolvimento, o banco é recriado.
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
    single { get<AppDatabase>().movieDao() }
    single { get<AppDatabase>().savedMovieDao() }
}
