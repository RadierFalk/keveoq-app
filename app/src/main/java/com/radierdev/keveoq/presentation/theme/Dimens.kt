package com.radierdev.keveoq.presentation.theme

import androidx.compose.ui.unit.dp

/**
 * Espaçamentos e tamanhos definidos em docs/identidade-visual.md ("Espaçamento e toque").
 * Centralizados aqui para nenhuma tela repetir números soltos.
 */
object Dimens {
    val ScreenHorizontalPadding = 20.dp
    val ItemSpacing = 16.dp
    val SectionSpacing = 24.dp

    val PosterListWidth = 72.dp
    val PosterListHeight = 108.dp
    val PosterDetailsHeight = 320.dp

    // Valores internos dos componentes, derivados da escala acima.
    val SmallSpacing = 4.dp
    val MediumSpacing = 8.dp
    val ContentPadding = 12.dp
    val BorderWidth = 1.dp
    val SelectedBorderWidth = 2.dp // card de humor selecionado (identidade visual)
    val IconSmall = 18.dp
    val LogoSize = 32.dp // logo do app na barra da tela inicial
    val ProgressStrokeWidth = 2.dp // indicador pequeno dentro de botões
}
