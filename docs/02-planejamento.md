# 02 — Planejamento técnico do KeveOQ

> Fase 2 do Spec-Driven Development. Este documento descreve **como** o app atende a `01-especificacao.md`. Os códigos RF, RNF, RN e CA referem-se àquele documento.

## 1. Ponto de partida

O projeto foi criado pelo template **Empty Activity** do Android Studio (AGP 9.5.1, Gradle 9.6.0, Compose BOM, Kotlin DSL e Version Catalog). Nenhum código de produção foi escrito. As divergências entre o template e o `CLAUDE.md` serão corrigidas nas primeiras tarefas da implementação:

| Divergência no template | Correção planejada |
|---|---|
| Pacote `com.example.keveoq` (namespace, applicationId e pastas) | Renomear para `com.radierdev.keveoq` |
| Tema em `ui/theme/`, função `KeveoqTheme`, cor dinâmica ligada, cores roxas, `FontFamily.Default` | Substituir pelo código de `docs/identidade-visual.md` em `presentation/theme/` |
| Fontes em `res/fonts/` (nome inválido para o Android) | Mover para `res/font/` |
| `colors.xml` sem uso; `themes.xml` com tema claro fixo | Remover `colors.xml`; tema da janela com variante noturna (`values-night`), evitando o "flash" branco no tema escuro |
| `app_name` = "keveoq" | "KeveOQ" |
| `minSdk` 24 | 26 (ver seção 4) |
| BuildConfig desligado | Ligar e ler `TMDB_API_KEY` do `local.properties` |
| Bibliotecas e plugins da stack ausentes; versões antigas no catálogo | Atualizar o catálogo (seção 4) |
| Manifesto sem `Application`, sem permissões | Criar `KeveOQApp` e declarar `INTERNET`, `ACCESS_NETWORK_STATE` e `POST_NOTIFICATIONS` |
| `MainActivity` com `Greeting` e `@Preview` | Substituir pela navegação do app |
| `ExampleUnitTest`, `ExampleInstrumentedTest`, Espresso e testes de UI | Remover (só testes unitários, em `src/test`) |

## 2. Arquitetura

### 2.1 Camadas e regra de dependência

Clean Architecture em um único módulo `:app`, separada por pacotes:

```
presentation  →  domain  ←  data
                   ↑
            notification (usa o domain)
di: conhece todas as camadas e as "monta"
```

A seta significa "conhece". O **domain** não conhece ninguém: ele declara o que precisa (interfaces de repositório) e a camada **data** implementa. Isso é a *inversão de dependência*: a regra de negócio não depende de Retrofit nem de Room, então pode ser testada na JVM, sem emulador, e as tecnologias podem ser trocadas sem tocar nas regras.

| Pacote | Responsabilidade | Pode importar |
|---|---|---|
| `domain` | Modelos, interfaces de repositório, casos de uso e regras de negócio | Kotlin, coroutines/Flow, `java.time`, `core` puro |
| `data` | Implementa os repositórios: Retrofit, Room, DTOs, entidades e mappers | `domain`, `core`, bibliotecas de dados |
| `presentation` | Telas Compose, ViewModels, UiState, navegação e tema | `domain` (apenas casos de uso e modelos), `core` |
| `notification` | Worker, canal e agendamento | `domain`, Android, WorkManager |
| `core` | `AppResult`, `AppError`, dispatchers e monitor de conectividade | Kotlin; só a implementação do monitor usa Android |
| `di` | Módulos do Koin | Tudo |

**Sobre o `core`:** o domain precisa de `AppResult` e `AppError` para declarar os contratos dos repositórios, então esses tipos são Kotlin puro. O monitor de conectividade é dividido em uma interface pura (`NetworkMonitor`) e uma implementação Android (`AndroidNetworkMonitor`). O domain só enxerga a interface.

### 2.2 Estrutura de pacotes

```
com.radierdev.keveoq/
├── KeveOQApp.kt                 # Application: inicia o Koin, cria o canal e agenda a notificação
├── MainActivity.kt              # enableEdgeToEdge + KeveOQTheme + navegação + pedido de permissão
├── di/                          # dataModule, domainModule, presentationModule, appModule
├── core/
│   ├── result/                  # AppResult, AppError
│   ├── dispatcher/              # AppDispatchers
│   └── network/                 # NetworkMonitor (interface) e AndroidNetworkMonitor
├── notification/                # MovieSuggestionWorker, NotificationChannels, NotificationScheduler,
│                                # NotificationPermissionPrefs
├── domain/
│   ├── model/                   # Mood, MoodSearchCriteria, Movie, Recommendations, MovieDetails,
│   │                            # Video, SavedMovie, WatchStatus
│   ├── rule/                    # MoodCriteriaMapper, RecommendationRules, CachePolicy,
│   │                            # TrailerSelector, RatingRules, NotificationWindow
│   ├── repository/              # MovieRepository, SavedMovieRepository
│   └── usecase/                 # um caso de uso por ação (seção 2.4)
├── data/
│   ├── remote/                  # TmdbApi, AuthInterceptor, dto/
│   ├── local/                   # AppDatabase, dao/, entity/, Converters
│   ├── mapper/                  # DTO ↔ entidade ↔ modelo de domínio
│   └── repository/              # MovieRepositoryImpl, SavedMovieRepositoryImpl, safeApiCall
└── presentation/
    ├── KeveOQAppContent.kt      # Scaffold raiz com banner offline e NavHost
    ├── navigation/              # rotas type-safe e NavHost
    ├── theme/                   # Color.kt, Type.kt, Theme.kt
    ├── components/              # MovieListItem, PosterImage, RatingBar, OfflineBanner,
    │                            # ErrorState, EmptyState, LoadingState, TrailerPlayer
    ├── util/                    # UpdatedAtFormatter, ErrorMessageMapper
    └── screens/
        ├── mood/                # MoodRoute, MoodScreen, MoodViewModel, MoodUiState
        ├── recommendations/
        ├── details/
        ├── watchlist/
        └── about/
```

### 2.3 Modelos de domínio

| Modelo | Campos principais |
|---|---|
| `Mood` (enum) | `HAPPY`, `SAD`, `EXCITED`, `ANXIOUS`, `BORED`, `ROMANTIC`, `NOSTALGIC`, `BRAVE` |
| `MoodSearchCriteria` | `withGenres: Set<Int>`, `withoutGenres: Set<Int>`, `releasedUntil: LocalDate?`, `minVoteCount: Int = 100` |
| `Movie` | `id`, `title`, `posterUrl?`, `releaseYear?`, `voteAverage`, `voteCount` |
| `Recommendations` | `movies: List<Movie>`, `updatedAt: Instant?` |
| `MovieDetails` | `id`, `title`, `overview`, `posterUrl?`, `voteAverage`, `releaseYear?`, `runtimeMinutes?`, `genres: List<String>`, `trailerKey?`, `updatedAt` |
| `Video` | `key`, `site`, `type`, `official` |
| `SavedMovie` | `movieId`, `title`, `posterUrl?`, `releaseYear?`, `status: WatchStatus`, `rating: Int?`, `savedAt` |
| `WatchStatus` (enum) | `WANT_TO_WATCH`, `WATCHED` |

Os nomes dos humores ficam em inglês no código; o texto em português ("Feliz", "Triste"…) fica em `strings.xml`, porque é apresentação.

### 2.4 Regras e casos de uso

**Regras (funções puras em `domain/rule`)**

| Regra | Responsabilidade | RN |
|---|---|---|
| `MoodCriteriaMapper` | `Mood` → `MoodSearchCriteria` (tabela do `CLAUDE.md`) | RN01, RN02 |
| `RecommendationRules` | Remover duplicados (por `id`), remover assistidos e filmes com menos de 100 votos, mantendo a ordem | RN02, RN03, RN04 |
| `CachePolicy` | `isExpired(updatedAt, now)`: verdadeiro se nulo ou com 24 h ou mais | RN05 |
| `TrailerSelector` | Escolhe a chave: `site = YouTube` e `type = Trailer`, oficial primeiro | RN09 |
| `RatingRules` | Valida nota inteira de 1 a 5 | RN08 |
| `NotificationWindow` | Calcula o atraso até a próxima janela 19h–21h a partir de um `LocalDateTime` | RN10 |

**Casos de uso** (classes com `operator fun invoke`)

| Caso de uso | O que faz | RF |
|---|---|---|
| `GetMoodsUseCase` | Lista os humores na ordem da tela | RF01 |
| `ObserveRecommendationsUseCase` | Combina as recomendações do banco com os IDs assistidos e aplica `RecommendationRules` | RF03, RN03, RN04 |
| `RefreshRecommendationsUseCase(mood, force)` | Decide se precisa atualizar (vazio, expirado ou `force`); sem conexão devolve `Failure(Network)` sem chamar a API | RF04, RF05, RN05 |
| `ObserveMovieDetailsUseCase` | Observa os detalhes guardados | RF06, RF07 |
| `RefreshMovieDetailsUseCase` | Busca detalhes e trailer (pt-BR e, sem trailer válido, en-US) e grava | RF07, RN09 |
| `ObserveSavedMovieUseCase` | Status e nota de um filme (para a tela de detalhes) | RF08 |
| `ObserveWatchlistUseCase(filter)` | Lista salva, filtrada por status | RF09 |
| `AddToWatchlistUseCase` | Salva como `WANT_TO_WATCH`; recusa duplicado | RF08, RN06 |
| `MarkAsWatchedUseCase` | `WANT_TO_WATCH` → `WATCHED` | RF10, RN07 |
| `RateMovieUseCase` | Valida e grava a nota (também para trocar a nota) | RF11, RN08 |
| `ObserveConnectivityUseCase` | Expõe o estado da conexão | RF13 |
| `CalculateNotificationDelayUseCase` | Usa `NotificationWindow` com o relógio injetado | RN10 |

**Resultados de regra de negócio.** `AppError` descreve falhas de infraestrutura (rede, HTTP, leitura). Uma recusa de regra de negócio não é uma falha técnica, então esses casos de uso devolvem tipos próprios, por exemplo:

```kotlin
sealed interface RateMovieResult {
    data object Rated : RateMovieResult
    data object InvalidRating : RateMovieResult
    data object NotWatched : RateMovieResult
}
```

Do mesmo jeito: `AddToWatchlistResult` (`Added`, `AlreadyInList`) e `MarkAsWatchedResult` (`Marked`, `AlreadyWatched`, `NotInList`). Cada caso fica explícito, o compilador obriga a tratar todos, e os testes verificam cada um.

**Relógio injetado.** As regras que dependem de hora recebem um `java.time.Clock` (via Koin). Em produção é `Clock.systemDefaultZone()`; nos testes, `Clock.fixed(...)`, o que permite testar "agendou às 22h" sem esperar o relógio real.

## 3. API: The Movie Database (TMDB)

### 3.1 Por que o TMDB

- Gratuito para uso não comercial e acadêmico, com cadastro simples.
- O endpoint `discover/movie` aceita filtros por gênero (inclusão e exclusão), número de votos e data, exatamente o que o mapeamento de humor precisa.
- Traduções em pt-BR de títulos e sinopses.
- Vídeos (trailers do YouTube) no próprio TMDB, sem uma segunda API.
- Documentação pública e estável.

### 3.2 Autenticação

- Credencial: **API Read Access Token** (v4), enviado no cabeçalho `Authorization: Bearer <token>` por um `Interceptor` do OkHttp (`AuthInterceptor`).
  - Por que o token, e não a API Key: é o método recomendado pela documentação atual do TMDB e, por estar no cabeçalho, não aparece na URL, então não vaza em logs de requisição.
- O valor fica em `local.properties` com o nome definido no `CLAUDE.md`: `TMDB_API_KEY=<read access token>`.
- O `app/build.gradle.kts` lê esse valor e cria `BuildConfig.TMDB_API_KEY`. Se a propriedade não existir, o build continua (valor vazio) e a API responde 401, tratado como erro.
- `local.properties` já está no `.gitignore` (verificado com `git check-ignore`).

### 3.3 Endpoints

| Uso | Requisição |
|---|---|
| Recomendações | `GET discover/movie?with_genres=35\|16\|10751&without_genres=27,53&sort_by=popularity.desc&vote_count.gte=100&language=pt-BR&page=1` (e `page=2`). Nostálgico acrescenta `primary_release_date.lte=1999-12-31`. |
| Detalhes | `GET movie/{movie_id}?language=pt-BR` |
| Trailer | `GET movie/{movie_id}/videos?language=pt-BR`; se `TrailerSelector` não achar trailer, `GET movie/{movie_id}/videos?language=en-US` |
| Pôster | `https://image.tmdb.org/t/p/w500{poster_path}` |
| Trailer externo | `https://www.youtube.com/watch?v={key}` |

Em `with_genres`, `|` significa OU e `,` significa E. A interface do Retrofit recebe os IDs já unidos como `String`; a união é feita no mapper do data, e a lista de IDs vem do domain.

## 4. Stack e versões

Ver `CLAUDE.md` para a lista obrigatória. Configurações do projeto:

| Item | Valor | Motivo |
|---|---|---|
| `minSdk` | 26 (Android 8.0) | `java.time` nativo (janela das 19h, "Atualizado há…") sem *desugaring*; cobre a grande maioria dos aparelhos em uso |
| `compileSdk` / `targetSdk` | 37 | Mantido do template |
| Java/Kotlin JVM target | 11 | Mantido do template |

Versões estáveis confirmadas em 17/11/2026 nos repositórios oficiais (Google Maven e Maven Central), excluindo alfa, beta e RC:

| Área | Coordenadas (resumo) | Versão | Para que serve |
|---|---|---|---|
| Kotlin + plugins `compose` e `serialization` | `org.jetbrains.kotlin.*` | 2.4.21 | Linguagem, compilador do Compose e geração de serializadores |
| KSP | `com.google.devtools.ksp` | 2.3.14 | Processa as anotações do Room em tempo de compilação |
| kotlinx.serialization | `kotlinx-serialization-json` | 1.13.0 | Converte JSON (API, rotas, gêneros no Room) |
| Compose BOM | `androidx.compose:compose-bom` | 2026.09.00 | Alinha as versões do Compose (UI 1.12.1, Material 3 1.4.0) |
| Ícones core | `material-icons-core` | definida pelo BOM | `Icons.Rounded` (a partir do Material 3 1.4.0, precisa ser declarada explicitamente) |
| Activity Compose | `androidx.activity:activity-compose` | 1.13.0 | `setContent`, `enableEdgeToEdge`, pedido de permissão |
| Core KTX | `androidx.core:core-ktx` | 1.19.1 | Extensões do Android (`NotificationCompat` etc.) |
| Lifecycle | `lifecycle-runtime-compose`, `lifecycle-viewmodel-compose` | 2.11.0 | `collectAsStateWithLifecycle`, ViewModel no Compose |
| Navigation | `androidx.navigation:navigation-compose` | 2.10.2 | Navegação com rotas type-safe |
| Koin | `koin-bom`, `koin-android`, `koin-androidx-compose` | 4.2.2 | Injeção de dependências |
| Coroutines | `kotlinx-coroutines-android` | 1.11.0 | Assíncrono, Flow, StateFlow |
| Retrofit + conversor | `retrofit`, `converter-kotlinx-serialization` | 3.0.0 | Cliente HTTP declarativo |
| OkHttp | `okhttp`, `logging-interceptor` | 5.5.1 | HTTP, interceptor da chave, log apenas no debug |
| Room | `room-runtime`, `room-ktx`, `room-compiler` | 2.8.5 | Banco local (fonte de verdade) |
| Coil 3 | `coil-compose`, `coil-network-okhttp` | 3.6.3 | Pôsteres com cache em disco |
| WorkManager | `work-runtime-ktx` | 2.12.0 | Notificação periódica |
| YouTube player | `androidyoutubeplayer:core` | 13.1.0 | Player do trailer |
| Testes | `junit` 4.13.2, `mockk` 1.14.12, `kotlinx-coroutines-test` 1.11.0, `turbine` 1.2.1 | — | Testes unitários |

Notas de compatibilidade:

- **`compileSdk` 37 é obrigatório:** `core-ktx` 1.19.1 e o Compose UI 1.12.1 exigem compilar contra a API 37 e AGP 9.1 ou superior. O template já usa 37 e AGP 9.5.1.
- **Kotlin embutido do AGP 9:** o AGP 9 compila Kotlin sozinho, sem o plugin `kotlin-android`. Declarar os plugins `compose` e `serialization` na versão 2.4.21 coloca o Kotlin 2.4.21 no build; o catálogo terá uma única versão `kotlin` para os três.
- **KSP 2.3.14:** desde a série 2.3 a versão do KSP não acompanha mais a do Kotlin. A 2.3.10 corrigiu a compatibilidade com o Kotlin embutido do AGP 9. Há relato de aviso de depreciação na configuração `ksp(...)`; se aparecer, não quebra o build e será tratado na tarefa de configuração.
- **Room 2.8.5, não Room 3:** o Room 3 é estável, mas usa outro grupo de artefatos (`androidx.room3`) e uma API diferente, com menos material de estudo. O 2.8.5 atende todo o escopo.
- **Logging do OkHttp** só é adicionado ao cliente em builds de debug e nunca registra o cabeçalho `Authorization` (`redactHeader`).

**Bibliotecas não usadas, de propósito:** `material-icons-extended` (ícones fora do conjunto core vêm do Vector Asset), `kotlinx-collections-immutable` (as classes de UI usam `@Immutable`), DataStore (a única preferência, "permissão já pedida", usa `SharedPreferences` do próprio Android), `koin-androidx-workmanager` (o Worker não precisa de dependências injetadas).

## 5. Modelo de dados (Room)

Banco `keveoq.db`, versão 1.

```
movies                       mood_recommendations              saved_movies
──────────────────────       ───────────────────────────       ─────────────────────────
id (PK)                 ◄──  movieId (PK, FK)                  movieId (PK)
title                        mood    (PK)                      title
posterPath?                  position                          posterPath?
releaseDate?                 updatedAt (epoch ms)              releaseDate?
voteAverage                                                    status  (WANT_TO_WATCH|WATCHED)
voteCount                                                      rating? (1..5)
                                                               savedAt (epoch ms)
movie_details
─────────────────────────
id (PK)
title, overview, posterPath?, voteAverage, releaseDate?, runtime?
genres (texto JSON: ["Drama","Romance"])
trailerKey?
updatedAt (epoch ms)
```

Decisões:

- **`mood_recommendations` com chave `(mood, movieId)`**: o mesmo filme pode aparecer em vários humores, mas não duas vezes no mesmo humor (reforça RN04 no banco). `position` guarda a ordem de popularidade.
- **`updatedAt` em cada linha de `mood_recommendations`**: todas as linhas de um humor recebem o mesmo instante; o mais recente é a data do cache (RN05 e "Atualizado há…").
- **`saved_movies` com `movieId` como chave primária**: impede duplicidade no banco, além da checagem no caso de uso (RN06).
- **`saved_movies` copia título, pôster e data**: a lista funciona mesmo se o cache de recomendações for substituído.
- **Gêneros como texto JSON** via `TypeConverter` com kotlinx.serialization: são só exibidos, nunca filtrados no banco, então não justificam uma tabela à parte.
- **Sem migrações**: o banco nasce na versão 1. Se o esquema mudar durante o desenvolvimento, usaremos `fallbackToDestructiveMigration` (documentado como decisão de projeto acadêmico).

DAOs: `MovieDao` (recomendações e detalhes) e `SavedMovieDao`. A substituição das recomendações de um humor é uma única transação (`@Transaction`): apaga as linhas antigas do humor, grava os filmes e as novas posições. Assim a tela nunca vê uma lista pela metade.

## 6. Estratégia Offline First e sincronização

### 6.1 Princípio

O **Room é a única fonte de verdade**. As telas observam `Flow` do Room; a rede só grava no Room, e o Flow emite os dados novos sozinho. Nenhuma tela recebe dados direto da API.

### 6.2 Recomendações

```
Tela abre ──► observa Flow do Room ──► mostra o cache (ou Loading, se vazio)
          └─► RefreshRecommendationsUseCase(mood, force = false)
                 ├─ cache válido (< 24 h)  → não faz nada
                 ├─ sem conexão            → Failure(Network)
                 └─ busca páginas 1 e 2 → aplica regras → grava em transação
                                           └─► Flow emite os dados novos
```

Resultado na tela (ViewModel):

| Cache | Atualização | Estado na tela |
|---|---|---|
| Com dados | não precisou / sucesso | `Success` |
| Com dados | falhou | `Success` + Snackbar com a mensagem (RF14) |
| Vazio | sucesso com filmes | `Success` |
| Vazio | sucesso sem filmes (tudo filtrado) | `Empty` |
| Vazio | falhou | `Error` com "Tentar novamente" (RF15) |

- Pull-to-refresh chama o mesmo caso de uso com `force = true` (RF05).
- As duas páginas são buscadas em sequência; se qualquer uma falhar, nada é gravado (tudo ou nada), para o cache nunca ficar com só metade da atualização.
- Marcar um filme como assistido faz ele sumir das recomendações na hora, porque o caso de uso combina os dois Flows (RN03).
- "Atualizado há…" é calculado na apresentação a partir de `updatedAt`: "Atualizado agora" (menos de 1 min), "Atualizado há X min", "Atualizado há X h", "Atualizado há X dias".

### 6.3 Detalhes e trailer

- A tela observa `movie_details`. Se não houver registro, ou ele tiver 24 h ou mais (mesma `CachePolicy`), e houver conexão, busca detalhes e vídeos e grava (RF07).
- Sem registro e sem conexão: `Error` com "Tentar novamente".
- A seção de trailer aparece só se `trailerKey != null` **e** houver conexão (RF20, RF21). O estado da conexão vem do `NetworkMonitor` e é atualizado em tempo real.
- O player é a `YouTubePlayerView` da android-youtube-player dentro de um `AndroidView`, ligada ao ciclo de vida da tela. No callback de erro, a seção troca o player pelo botão "Assistir no YouTube", que abre o link com `LocalUriHandler` (app do YouTube ou navegador). Assim o ViewModel não precisa de `Context`.

### 6.4 Lista "Quero assistir"

Dados só locais (RNF05). Toda operação é uma escrita no Room e a tela se atualiza pelo Flow. Não há sincronização com servidor, logo não há conflito.

### 6.5 Imagens

O Coil guarda os pôsteres em cache de disco. Pôster nunca carregado não aparece offline; no lugar, é exibido o marcador definido na identidade visual (`surfaceContainer`, borda e ícone de filme em `outline`). Limitação registrada no README.

### 6.6 Indicador de conexão

`AndroidNetworkMonitor` usa `ConnectivityManager.registerDefaultNetworkCallback` dentro de um `callbackFlow` e emite `true`/`false` (rede com `NET_CAPABILITY_VALIDATED`). O banner "sem internet" fica no `Scaffold` raiz, acima do `NavHost`, então aparece em todas as telas (RF13).

## 7. Tratamento de erros

### 7.1 Conversão de exceções

Os repositórios nunca propagam exceções. Toda chamada de rede passa por `safeApiCall`:

| Exceção | `AppError` |
|---|---|
| `IOException` (sem conexão, timeout, DNS) | `Network` |
| `HttpException` | `Http(code)` |
| `SerializationException` | `Serialization` |
| `CancellationException` | **relançada**: cancelar a coroutine (sair da tela) não é erro |
| Qualquer outra | `Unknown` |

### 7.2 Mensagens ao usuário

Feitas em `ErrorMessageMapper`, na apresentação, com textos de `strings.xml`. Seguem a identidade visual: dizem o que aconteceu e como resolver, sem pedir desculpas.

| Erro | Mensagem |
|---|---|
| `Network` | "Não foi possível carregar os filmes. Verifique sua conexão." |
| `Http(401)` | "Não foi possível acessar o catálogo de filmes." |
| `Http(5xx)` | "O catálogo de filmes está fora do ar. Tente mais tarde." |
| `Http(outros)`, `Serialization`, `Unknown` | "Algo deu errado ao carregar os filmes. Tente novamente." |
| `AddToWatchlistResult.AlreadyInList` | "Este filme já está na sua lista." |
| `RateMovieResult.InvalidRating` / `NotWatched` | Não aparecem na prática: a interface só oferece estrelas de 1 a 5 e só para assistidos. Mesmo assim o domain recusa, e os testes cobrem. |

## 8. Notificação

| Item | Decisão |
|---|---|
| Agendamento | `PeriodicWorkRequest` de 4 dias, `enqueueUniquePeriodicWork("movie_suggestion", KEEP, ...)`, chamado em `KeveOQApp.onCreate` |
| Primeira execução | `setInitialDelay` com o valor de `CalculateNotificationDelayUseCase` |
| Cálculo (RN10) | antes das 19h → hoje às 19h; entre 19h e 21h → imediato (atraso zero); a partir das 21h → amanhã às 19h |
| Worker | `MovieSuggestionWorker` (`CoroutineWorker`): verifica a permissão e publica a notificação; não tem regra de negócio |
| Canal | "Sugestões de filmes", criado em `KeveOQApp.onCreate` |
| Texto | Título "Que tal um filme hoje?"; texto "Conte como você está e o KeveOQ escolhe um filme para você." |
| Toque | `PendingIntent` para a `MainActivity`, que abre na seleção de humor (destino inicial) |
| Ícone | Vetor monocromático importado pelo Vector Asset (`ic_notification`) |
| Permissão (RF19) | Android 13+: pedida uma vez na primeira abertura, pela `MainActivity`. Uma flag em `SharedPreferences` (`NotificationPermissionPrefs`) registra que já foi pedida, para não pedir a cada abertura |

**Por que não há "janela" (flex) no WorkManager:** a combinação de atraso inicial com janela flexível tem regras de cálculo pouco intuitivas, e as execuções seguintes acabam deslocadas de qualquer forma pela economia de bateria. Mantemos o atraso inicial até as 19h e o período de 4 dias; o horário exato das repetições é uma limitação documentada.

## 9. Interface e navegação

### 9.1 Rotas (type-safe, kotlinx.serialization)

| Rota | Argumentos | Tela |
|---|---|---|
| `MoodRoute` (`object`) | — | Seleção de humor (destino inicial) |
| `RecommendationsRoute` | `mood: String` (nome do enum) | Recomendações |
| `DetailsRoute` | `movieId: Int` | Detalhes |
| `WatchlistRoute` (`object`) | — | Quero assistir |
| `AboutRoute` (`object`) | — | Sobre |

O humor vai como `String` (nome do enum) porque tipos simples na rota não exigem `NavType` personalizado; o ViewModel converte de volta com `Mood.valueOf`.

### 9.2 Telas

| Tela | Conteúdo | Estados |
|---|---|---|
| Humor | `displaySmall` "Como você está hoje?"; grade de 8 cards (nome em `titleMedium`, dica em `bodySmall`); botão "Ver filmes" habilitado após a seleção; barra superior com ícones "Quero assistir" e "Sobre" | Só `Success` (conteúdo fixo) |
| Recomendações | Título com o humor (`headlineSmall`), "Atualizado há…", lista com `MovieListItem`, pull-to-refresh, Snackbar | Loading, Success, Empty, Error |
| Detalhes | Pôster em largura total (320 dp), título, ano, duração, nota, chips de gênero, sinopse, trailer, ação conforme o status: "Quero assistir" (`Button`), "Já assisti" (`OutlinedButton`) ou estrelas | Loading, Success, Error |
| Quero assistir | Filtro por status (chips: Todos, Quero assistir, Assistidos), itens com "Já assisti" ou estrelas | Loading, Success, Empty, Error |
| Sobre | Nome, versão do app, descrição curta, atribuição ao TMDB, créditos das fontes | Só `Success` (conteúdo fixo) |

As telas Humor e Sobre não dependem de rede nem de banco, por isso seu `UiState` só tem o estado de conteúdo. Mesmo assim, ambas têm ViewModel, para manter o padrão do `CLAUDE.md` ("cada tela tem um ViewModel"): o de Humor guarda a seleção ao girar o aparelho; o de Sobre expõe a versão do app.

Dicas propostas para os cards de humor (`bodySmall`), sujeitas à sua aprovação:

| Humor | Dica |
|---|---|
| Feliz | Comédias e animações para manter o astral |
| Triste | Dramas e romances para sentir junto |
| Animado | Ação e aventura para gastar energia |
| Ansioso | Histórias leves, sem sustos |
| Entediado | Mistério e ficção para prender a atenção |
| Romântico | Romances para aquecer o coração |
| Nostálgico | Aventuras clássicas de antes dos anos 2000 |
| Corajoso | Terror e suspense para quem aguenta |

### 9.3 Padrões de implementação

- Cada tela tem `XRoute` (pega o ViewModel do Koin com `koinViewModel()`, coleta com `collectAsStateWithLifecycle()`) e `XScreen` (sem estado, recebe `UiState` e lambdas). Previews com `@PreviewLightDark` chamam o `XScreen` dentro de `KeveOQTheme`.
- ViewModel expõe um único `StateFlow<XUiState>` (via `stateIn(viewModelScope, WhileSubscribed(5_000), ...)`); eventos únicos (Snackbar) por `Channel` exposto como `Flow`.
- Modelos de UI anotados com `@Immutable`; listas lazy com `key = { it.id }`.
- Cores, tipografia e formas apenas de `MaterialTheme`. Espaçamentos (20, 16, 24 dp) e tamanhos (pôster 72×108 dp e 320 dp) ficam em um objeto `Dimens` no pacote `theme`, para não repetir números nas telas.
- Ícones core: `ArrowBack` (auto-espelhado), `Info`, `Star`, `Refresh`. Via Vector Asset: `bookmark` (Quero assistir), `cloud_off` (banner), `movie` (pôster indisponível), ícone da notificação.
- Textos visíveis ao usuário em `strings.xml` (português).

## 10. Injeção de dependências (Koin)

| Módulo | Fornece |
|---|---|
| `appModule` | `AppDispatchers`, `Clock`, `NetworkMonitor`, `NotificationPermissionPrefs` |
| `dataModule` | `Json`, `OkHttpClient` (com `AuthInterceptor`), `Retrofit`, `TmdbApi`, `AppDatabase`, DAOs, repositórios |
| `domainModule` | Casos de uso (`factory`) |
| `presentationModule` | ViewModels (`viewModel`) |

O Koin é iniciado em `KeveOQApp.onCreate`. Como os casos de uso e regras não conhecem o Koin, os testes os constroem diretamente.

**Dispatchers injetados:** `AppDispatchers(io, default, main)`. Os repositórios usam `withContext(dispatchers.io)` para mapeamentos; nos testes, todos viram o `StandardTestDispatcher`.

## 11. Estratégia de testes

Todos em `app/src/test` (JVM), com JUnit 4, MockK, kotlinx-coroutines-test e Turbine. Nomes em português com crases.

| Alvo | Como | Casos principais | CA |
|---|---|---|---|
| `MoodCriteriaMapper` | Teste direto | Os 8 humores; Ansioso exclui 27 e 53; Nostálgico até 1999-12-31; mínimo de 100 votos | CA02, CA03 |
| `RecommendationRules` | Teste direto | Remove duplicados mantendo a ordem; remove assistidos; remove menos de 100 votos | CA04, CA05 |
| `CachePolicy` | Relógio fixo | Nulo, 23 h 59 min, exatamente 24 h, 25 h | CA06, CA07 |
| `TrailerSelector` | Teste direto | Oficial primeiro; ignora teaser e Vimeo; lista vazia → nulo | CA15, CA17 |
| `RatingRules` | Teste direto | 0, 1, 5, 6 | CA24 |
| `NotificationWindow` | Horários fixos | 14h, 19h, 20h59, 21h, 22h, virada de mês | CA29 |
| Casos de uso | Repositórios falsos | Cada caso de uso e cada resultado (`AlreadyInList`, `NotWatched` etc.); fallback do trailer para en-US | CA16, CA20, CA22–CA24 |
| `MovieRepositoryImpl` | `TmdbApi` e DAO falsos | Busca páginas 1 e 2; grava em transação; falha não apaga o cache; conversão de exceções em `AppError` | CA09, CA10, CA33 |
| `RefreshRecommendationsUseCase` | Repositório e monitor falsos | Cache válido não chama a API; expirado chama; offline devolve `Network`; `force` ignora validade | CA06–CA08 |
| ViewModels | Turbine + `StandardTestDispatcher` (regra `MainDispatcherRule`) | Loading → Success / Empty / Error; Snackbar com cache e falha; filtro da lista | CA09, CA10, CA21, CA26 |
| `UpdatedAtFormatter`, `ErrorMessageMapper` | Teste direto | Faixas de tempo; cada `AppError` | CA13, CA32 |

**Falsos ("fakes") em vez de mocks** para repositórios, API e DAOs: são classes simples, com dados em `MutableStateFlow`, que se comportam como a coisa real e deixam os testes mais legíveis. O MockK fica para verificar chamadas pontuais (por exemplo, "a API não foi chamada").

**O que não é testado automaticamente** (verificado manualmente, com capturas em `evidencias/`): telas Compose, player do YouTube, Worker e a notificação, o pedido de permissão e o banco Room real. Testes instrumentados estão fora do escopo do `CLAUDE.md`.

Comando: `./gradlew testDebugUnitTest`. Resultados registrados em `TEST_RESULTS.md` após cada execução relevante.

## 12. Riscos e limitações

| Risco / limitação | Mitigação |
|---|---|
| Notificação atrasa por economia de bateria | Documentado no README; WorkManager é a solução recomendada para trabalho periódico |
| Trailer bloqueado para reprodução fora do YouTube | Botão "Assistir no YouTube" |
| Pôsteres não vistos não aparecem offline | Marcador de pôster indisponível; documentado |
| Chave do TMDB ausente na máquina do avaliador | README explica como obter e configurar; sem chave o app mostra erro amigável |
| Humor sem resultados após remover assistidos | Estado `Empty` com explicação |
| Mudança de esquema do banco durante o desenvolvimento | `fallbackToDestructiveMigration` (dados de teste podem ser perdidos) |

## 13. Decisões registradas

Decididas na aprovação deste planejamento:

1. **Credencial do TMDB:** API Read Access Token no cabeçalho `Authorization: Bearer` (seção 3.2).
2. **`minSdk` 26**, por causa do `java.time` nativo (seção 4).
3. **`material-icons-core` declarada explicitamente**, com versão definida pelo BOM, para usar `Icons.Rounded` conforme a identidade visual.
4. **`logging-interceptor` do OkHttp** apenas em debug, com o cabeçalho `Authorization` ocultado.
5. **Telas Humor e Sobre** têm ViewModel, mas `UiState` só com o estado de conteúdo (seção 9.2).
6. **Dicas dos cards de humor** aprovadas conforme a seção 9.2.
7. **Textos da notificação** aprovados conforme a seção 8.
8. **`SharedPreferences`** para registrar que a permissão de notificação já foi pedida.
9. **Detalhes do filme** seguem a mesma validade de 24 h das recomendações (seção 6.3).

Decididas durante a implementação:

10. **Cache com data de atualização no futuro é considerado expirado** (T08). Se o relógio do aparelho for atrasado depois de uma atualização, a idade do cache fica negativa; tratá-lo como expirado força uma nova busca, que corrige a data gravada, em vez de deixar o cache válido indefinidamente.
11. **Atualização dos detalhes é "tudo ou nada"** (T12), como a das recomendações: se a busca dos detalhes ou de qualquer lista de vídeos (pt-BR ou en-US) falhar, nada é gravado e o erro é devolvido. Assim o banco nunca guarda um filme "sem trailer" só porque a requisição de vídeos falhou; os detalhes antigos, se existirem, continuam disponíveis.
