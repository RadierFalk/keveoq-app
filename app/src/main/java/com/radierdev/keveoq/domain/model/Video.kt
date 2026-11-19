package com.radierdev.keveoq.domain.model

/** Vídeo associado a um filme, usado para escolher o trailer. */
data class Video(
    val key: String,
    val site: String,
    val type: String,
    val official: Boolean,
)
