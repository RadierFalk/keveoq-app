package com.radierdev.keveoq.presentation.screens.mood

import androidx.annotation.StringRes
import com.radierdev.keveoq.R
import com.radierdev.keveoq.domain.model.Mood

// Textos de cada humor. Ficam na presentation, e não no enum do domain,
// porque são apresentação (strings.xml em português).

/** Nome do humor ("Feliz", "Triste"...). Também usado como título das recomendações. */
@get:StringRes
val Mood.labelRes: Int
    get() = when (this) {
        Mood.HAPPY -> R.string.mood_happy
        Mood.SAD -> R.string.mood_sad
        Mood.EXCITED -> R.string.mood_excited
        Mood.ANXIOUS -> R.string.mood_anxious
        Mood.BORED -> R.string.mood_bored
        Mood.ROMANTIC -> R.string.mood_romantic
        Mood.NOSTALGIC -> R.string.mood_nostalgic
        Mood.BRAVE -> R.string.mood_brave
    }

/** Dica curta exibida no card do humor. */
@get:StringRes
val Mood.hintRes: Int
    get() = when (this) {
        Mood.HAPPY -> R.string.mood_happy_hint
        Mood.SAD -> R.string.mood_sad_hint
        Mood.EXCITED -> R.string.mood_excited_hint
        Mood.ANXIOUS -> R.string.mood_anxious_hint
        Mood.BORED -> R.string.mood_bored_hint
        Mood.ROMANTIC -> R.string.mood_romantic_hint
        Mood.NOSTALGIC -> R.string.mood_nostalgic_hint
        Mood.BRAVE -> R.string.mood_brave_hint
    }
