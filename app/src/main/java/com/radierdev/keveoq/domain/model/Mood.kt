package com.radierdev.keveoq.domain.model

/**
 * Humores que o usuário pode escolher, na ordem em que aparecem na tela.
 *
 * Os nomes ficam em inglês no código; o texto em português ("Feliz", "Triste"...)
 * é responsabilidade da presentation (strings.xml).
 */
enum class Mood {
    HAPPY,
    SAD,
    EXCITED,
    ANXIOUS,
    BORED,
    ROMANTIC,
    NOSTALGIC,
    BRAVE,
}
