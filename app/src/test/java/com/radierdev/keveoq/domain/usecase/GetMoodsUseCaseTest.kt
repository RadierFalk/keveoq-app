package com.radierdev.keveoq.domain.usecase

import com.radierdev.keveoq.domain.model.Mood
import org.junit.Assert.assertEquals
import org.junit.Test

class GetMoodsUseCaseTest {

    @Test
    fun `deve listar os 8 humores na ordem da tela`() {
        val getMoods = GetMoodsUseCase()

        assertEquals(
            listOf(
                Mood.HAPPY, Mood.SAD, Mood.EXCITED, Mood.ANXIOUS,
                Mood.BORED, Mood.ROMANTIC, Mood.NOSTALGIC, Mood.BRAVE,
            ),
            getMoods(),
        )
    }
}
