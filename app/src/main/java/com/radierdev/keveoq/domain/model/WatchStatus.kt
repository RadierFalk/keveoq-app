package com.radierdev.keveoq.domain.model

/** Situação de um filme na lista "Quero assistir". Só avança de WANT_TO_WATCH para WATCHED. */
enum class WatchStatus {
    WANT_TO_WATCH,
    WATCHED,
}
