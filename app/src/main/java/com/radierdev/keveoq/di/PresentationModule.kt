package com.radierdev.keveoq.di

import com.radierdev.keveoq.presentation.KeveOQAppViewModel
import com.radierdev.keveoq.presentation.screens.mood.MoodViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** ViewModels: o Koin cria cada um ligado ao ciclo de vida da tela que o pediu. */
val presentationModule = module {
    viewModelOf(::KeveOQAppViewModel)
    viewModelOf(::MoodViewModel)
}
