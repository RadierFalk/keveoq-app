package com.radierdev.keveoq.domain.usecase

import com.radierdev.keveoq.domain.model.Mood

/** Lista os humores na ordem em que aparecem na tela inicial (RF01). */
class GetMoodsUseCase {
    operator fun invoke(): List<Mood> = Mood.entries
}
