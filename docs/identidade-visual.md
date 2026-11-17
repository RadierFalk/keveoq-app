# Identidade visual — KeveOQ

Este documento define o tema do app. Siga-o em toda tela e componente.
A referência visual completa (canvas de design) é mantida pelo responsável do projeto; em caso de dúvida sobre aparência, pergunte.

## Princípios

- Sensação: aconchegante, "sofá e pipoca".
- Base de cor: laranja + azul-petróleo (o par clássico da correção de cor no cinema), com neutros quentes (manteiga no claro, azul-noite no escuro).
- Temas claro e escuro são obrigatórios e seguem o sistema (`isSystemInDarkTheme()`).

## Regras para o agente

1. **Cores somente via `MaterialTheme.colorScheme`.** Nunca use `Color(0xFF...)` em telas ou componentes. Valores hexadecimais existem apenas em `Color.kt`.
2. **Não use cor dinâmica (Material You).** O tema não tem o parâmetro `dynamicColor`; não o adicione.
3. **`KeveOQBrand` é só para ilustração** (ícone, imagens decorativas). Nunca use essas cores como fundo de texto, botão ou superfície.
4. **Tipografia somente via `MaterialTheme.typography`.** Não defina `fontSize` ou `fontFamily` diretamente em telas.
5. **Não crie cores, fontes ou formas novas.** Se algo parecer faltar, pergunte.
6. Todo `@Composable` de tela deve ter preview nos dois temas com `@PreviewLightDark`.

## Arquivos do tema

Os arquivos ficam em `presentation/theme/`. O template do Android Studio os cria em `ui/theme/`: mova-os para `presentation/theme/` e ajuste o `package`, conforme a arquitetura do `CLAUDE.md`.

### Color.kt

```kotlin
package com.radierdev.keveoq.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * Cores exclusivas de ilustração (ícone e imagens decorativas).
 * NUNCA usar como fundo de texto, botão ou superfície.
 */
object KeveOQBrand {
    val Abobora = Color(0xFFE8661E)
    val Caramelo = Color(0xFFEBC789)
    val Creme = Color(0xFFFFF1CF)
}

// ---------- Tema claro ----------
internal val PrimaryLight = Color(0xFFB8460B)
internal val OnPrimaryLight = Color(0xFFFFFFFF)
internal val PrimaryContainerLight = Color(0xFFFFDCC4)
internal val OnPrimaryContainerLight = Color(0xFF3B1400)
internal val InversePrimaryLight = Color(0xFFFFA066)

internal val SecondaryLight = Color(0xFF23525E)
internal val OnSecondaryLight = Color(0xFFFFFFFF)
internal val SecondaryContainerLight = Color(0xFFCDE7EC)
internal val OnSecondaryContainerLight = Color(0xFF062F38)

internal val TertiaryLight = Color(0xFF7A5800)
internal val OnTertiaryLight = Color(0xFFFFFFFF)
internal val TertiaryContainerLight = Color(0xFFFFE08F)
internal val OnTertiaryContainerLight = Color(0xFF261A00)

internal val BackgroundLight = Color(0xFFFFF6E3)
internal val OnBackgroundLight = Color(0xFF2B1C14)
internal val SurfaceVariantLight = Color(0xFFF2E0C8)
internal val OnSurfaceVariantLight = Color(0xFF5A4636)
internal val SurfaceDimLight = Color(0xFFE6D4B6)
internal val SurfaceContainerLowestLight = Color(0xFFFFFFFF)
internal val SurfaceContainerLowLight = Color(0xFFFBEFD9)
internal val SurfaceContainerLight = Color(0xFFF8EAD0)
internal val SurfaceContainerHighLight = Color(0xFFF3E3C6)
internal val SurfaceContainerHighestLight = Color(0xFFEDDCBC)
internal val InverseSurfaceLight = Color(0xFF3A2C24)
internal val InverseOnSurfaceLight = Color(0xFFFCEEE2)

internal val OutlineLight = Color(0xFF8C7461)
internal val OutlineVariantLight = Color(0xFFDCC6AD)
internal val ErrorLight = Color(0xFFBA1A1A)
internal val OnErrorLight = Color(0xFFFFFFFF)
internal val ErrorContainerLight = Color(0xFFFFDAD6)
internal val OnErrorContainerLight = Color(0xFF410002)

// ---------- Tema escuro ----------
internal val PrimaryDark = Color(0xFFFFA066)
internal val OnPrimaryDark = Color(0xFF4A1800)
internal val PrimaryContainerDark = Color(0xFF8C3507)
internal val OnPrimaryContainerDark = Color(0xFFFFDCC4)
internal val InversePrimaryDark = Color(0xFFB8460B)

internal val SecondaryDark = Color(0xFF9FCFDA)
internal val OnSecondaryDark = Color(0xFF00363F)
internal val SecondaryContainerDark = Color(0xFF1F4D58)
internal val OnSecondaryContainerDark = Color(0xFFCDE7EC)

internal val TertiaryDark = Color(0xFFF2C25B)
internal val OnTertiaryDark = Color(0xFF3F2E00)
internal val TertiaryContainerDark = Color(0xFF5C4300)
internal val OnTertiaryContainerDark = Color(0xFFFFE08F)

internal val BackgroundDark = Color(0xFF0E1D22)
internal val OnBackgroundDark = Color(0xFFF3E6DA)
internal val SurfaceVariantDark = Color(0xFF2A3E45)
internal val OnSurfaceVariantDark = Color(0xFFC5D3D6)
internal val SurfaceBrightDark = Color(0xFF2F444B)
internal val SurfaceContainerLowestDark = Color(0xFF091519)
internal val SurfaceContainerLowDark = Color(0xFF132429)
internal val SurfaceContainerDark = Color(0xFF17292F)
internal val SurfaceContainerHighDark = Color(0xFF20343B)
internal val SurfaceContainerHighestDark = Color(0xFF2A3E45)
internal val InverseSurfaceDark = Color(0xFFF3E6DA)
internal val InverseOnSurfaceDark = Color(0xFF1B2A30)

internal val OutlineDark = Color(0xFF8A9DA2)
internal val OutlineVariantDark = Color(0xFF3E545B)
internal val ErrorDark = Color(0xFFFFB4AB)
internal val OnErrorDark = Color(0xFF690005)
internal val ErrorContainerDark = Color(0xFF93000A)
internal val OnErrorContainerDark = Color(0xFFFFDAD6)
```

### Type.kt

As fontes ficam embutidas no app (não usar "downloadable fonts"), porque o app é Offline First. Os arquivos `.ttf` são adicionados manualmente em `app/src/main/res/font/` com estes nomes exatos:

- `bricolage_grotesque_bold.ttf`
- `bricolage_grotesque_extrabold.ttf`
- `nunito_regular.ttf`
- `nunito_bold.ttf`
- `nunito_extrabold.ttf`

**Se os arquivos não existirem, pare e me peça.** Não baixe, não substitua por outra fonte e não remova a referência. O texto da licença (OFL) fica em `docs/licencas/` e as fontes são citadas no README.

```kotlin
package com.radierdev.keveoq.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.radierdev.keveoq.R

private val Bricolage = FontFamily(
    Font(R.font.bricolage_grotesque_bold, FontWeight.Bold),
    Font(R.font.bricolage_grotesque_extrabold, FontWeight.ExtraBold),
)

private val Nunito = FontFamily(
    Font(R.font.nunito_regular, FontWeight.Normal),
    Font(R.font.nunito_bold, FontWeight.Bold),
    Font(R.font.nunito_extrabold, FontWeight.ExtraBold),
)

internal val KeveOQTypography = Typography(
    displaySmall = TextStyle(fontFamily = Bricolage, fontWeight = FontWeight.ExtraBold, fontSize = 36.sp, lineHeight = 44.sp, letterSpacing = (-0.5).sp),
    headlineSmall = TextStyle(fontFamily = Bricolage, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, lineHeight = 32.sp),
    titleLarge = TextStyle(fontFamily = Bricolage, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, lineHeight = 24.sp),
    titleSmall = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, lineHeight = 20.sp),
    bodyLarge = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.Normal, fontSize = 13.sp, lineHeight = 18.sp),
    labelLarge = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.sp, lineHeight = 18.sp),
)
```

### Theme.kt

```kotlin
package com.radierdev.keveoq.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = PrimaryLight, onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight, onPrimaryContainer = OnPrimaryContainerLight,
    inversePrimary = InversePrimaryLight,
    secondary = SecondaryLight, onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight, onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = TertiaryLight, onTertiary = OnTertiaryLight,
    tertiaryContainer = TertiaryContainerLight, onTertiaryContainer = OnTertiaryContainerLight,
    background = BackgroundLight, onBackground = OnBackgroundLight,
    surface = BackgroundLight, onSurface = OnBackgroundLight,
    surfaceVariant = SurfaceVariantLight, onSurfaceVariant = OnSurfaceVariantLight,
    surfaceTint = PrimaryLight,
    surfaceDim = SurfaceDimLight, surfaceBright = BackgroundLight,
    surfaceContainerLowest = SurfaceContainerLowestLight,
    surfaceContainerLow = SurfaceContainerLowLight,
    surfaceContainer = SurfaceContainerLight,
    surfaceContainerHigh = SurfaceContainerHighLight,
    surfaceContainerHighest = SurfaceContainerHighestLight,
    inverseSurface = InverseSurfaceLight, inverseOnSurface = InverseOnSurfaceLight,
    outline = OutlineLight, outlineVariant = OutlineVariantLight,
    error = ErrorLight, onError = OnErrorLight,
    errorContainer = ErrorContainerLight, onErrorContainer = OnErrorContainerLight,
)

private val DarkColors = darkColorScheme(
    primary = PrimaryDark, onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark, onPrimaryContainer = OnPrimaryContainerDark,
    inversePrimary = InversePrimaryDark,
    secondary = SecondaryDark, onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark, onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiaryDark, onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark, onTertiaryContainer = OnTertiaryContainerDark,
    background = BackgroundDark, onBackground = OnBackgroundDark,
    surface = BackgroundDark, onSurface = OnBackgroundDark,
    surfaceVariant = SurfaceVariantDark, onSurfaceVariant = OnSurfaceVariantDark,
    surfaceTint = PrimaryDark,
    surfaceDim = BackgroundDark, surfaceBright = SurfaceBrightDark,
    surfaceContainerLowest = SurfaceContainerLowestDark,
    surfaceContainerLow = SurfaceContainerLowDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = SurfaceContainerHighDark,
    surfaceContainerHighest = SurfaceContainerHighestDark,
    inverseSurface = InverseSurfaceDark, inverseOnSurface = InverseOnSurfaceDark,
    outline = OutlineDark, outlineVariant = OutlineVariantDark,
    error = ErrorDark, onError = OnErrorDark,
    errorContainer = ErrorContainerDark, onErrorContainer = OnErrorContainerDark,
)

private val KeveOQShapes = Shapes(
    small = RoundedCornerShape(12.dp),   // pôsteres e chips
    medium = RoundedCornerShape(20.dp),  // cards e itens de lista destacados
    large = RoundedCornerShape(28.dp),   // painéis, banners e bottom sheets
)

@Composable
fun KeveOQTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = KeveOQTypography,
        shapes = KeveOQShapes,
        content = content,
    )
}
```

Na `MainActivity`, chame `enableEdgeToEdge()` antes de `setContent { KeveOQTheme { ... } }` e trate os insets com `Scaffold` ou `WindowInsets`.

## Onde usar cada cor

| Elemento | Papel |
|---|---|
| Fundo das telas | `background` |
| Botão principal (ex.: "Ver filmes", "Quero assistir") | `Button` padrão → `primary` / `onPrimary` |
| Botão secundário (ex.: "Já assisti") | `OutlinedButton` → borda `outline`, texto `primary` |
| Card de humor não selecionado | `surfaceContainerLowest` com borda `outlineVariant` |
| Card de humor selecionado | `primaryContainer` com borda `primary` (2dp) |
| Chip de gênero | `secondaryContainer` / `onSecondaryContainer` |
| Estrelas de nota | `tertiary` (estrela vazia: `outline`) |
| Banner "sem internet" | `tertiaryContainer` / `onTertiaryContainer` |
| Itens da lista "Quero assistir" | `surfaceContainer` |
| Pôster indisponível offline | `surfaceContainer` com borda `outline` e ícone de filme em `outline` |
| Textos secundários (ano, "atualizado há…") | `onSurfaceVariant` |
| Mensagens de erro | `errorContainer` / `onErrorContainer` |

## Onde usar cada estilo de texto

| Uso | Estilo |
|---|---|
| "Como você está hoje?" | `displaySmall` |
| Título de tela (nome do humor, "Quero assistir") | `headlineSmall` |
| Nome do filme (lista e detalhes) | `titleLarge` |
| Nome do humor no card | `titleMedium` |
| Sinopse e textos corridos | `bodyLarge` |
| Subtítulos e informações secundárias | `bodyMedium` |
| Ano, legendas, dicas do card de humor | `bodySmall` |
| Botões | `labelLarge` (padrão dos botões M3) |
| Chips | `labelMedium` |

## Espaçamento e toque

- Margem lateral das telas: 20dp.
- Espaço entre itens de lista: 16dp; entre seções: 24dp.
- Alvo de toque mínimo: 48dp (botões de ícone usam `IconButton`, que já garante isso).
- Pôster na lista: 72×108dp, forma `small`. Na tela de detalhes, ocupa a largura total com 320dp de altura.

## Ícones

- Use os ícones do conjunto core (`Icons.Rounded`) quando existirem.
- Para ícones que não estão no core (ex.: bookmark, nuvem cortada), importe vetores individuais pelo *Vector Asset* do Android Studio para `res/drawable`. Não adicione a dependência `material-icons-extended` sem perguntar.
- Botões só com ícone devem ter `contentDescription` em português (ex.: "Voltar", "Salvar em Quero assistir").

## Ícone do app

O ícone adaptativo (pipoca em forma de balão de fala, com relevo) será fornecido pelo responsável do projeto. Até lá, mantenha o ícone padrão do template. Não crie nem desenhe um ícone.

## Estados das telas

- **Carregando:** `CircularProgressIndicator` centralizado em `primary`.
- **Vazio:** frase curta explicando o motivo e uma ação (ex.: "Sua lista está vazia." + botão "Escolher um humor").
- **Erro:** o que aconteceu e como resolver, sem pedir desculpas (ex.: "Não foi possível carregar os filmes. Verifique sua conexão." + botão "Tentar novamente").
