# 03 — Tarefas do KeveOQ

> Fase 3 do Spec-Driven Development. Decomposição de `02-planejamento.md` em tarefas pequenas, executadas uma por vez, nesta ordem. Cada tarefa termina com um commit aprovado.

## Definição de pronto (vale para todas as tarefas)

- Compila sem erros (`./gradlew assembleDebug`).
- Testes passando (`./gradlew testDebugUnitTest`).
- Regra de dependência entre camadas respeitada (domain sem `android.*`, Retrofit, Room ou Koin).
- Interface conforme `docs/identidade-visual.md`, com preview `@PreviewLightDark` (tarefas com tela).
- Documentação afetada atualizada: esta lista (checkbox), `README.md` (o que de fato existe) e, se houver testes, `TEST_RESULTS.md`.
- Commit aprovado e realizado.

## Legenda

- **RF/RN/CA:** códigos de `01-especificacao.md`.
- **Seção:** seção de `02-planejamento.md` que detalha a tarefa.

---

## Configuração

### [x] T01 — Renomear o pacote
- **Descrição:** trocar `com.example.keveoq` por `com.radierdev.keveoq` em `namespace`, `applicationId`, pastas de código e declarações `package`/`import`.
- **Pronto quando:** não há mais nenhuma ocorrência de `com.example` no projeto, e o app compila e abre no emulador.
- **Relacionado:** RNF02, RNF11 · Seção 1
- **Commit:** `chore: renomeia pacote para com.radierdev.keveoq`

### [x] T02 — Configurar build, dependências e README inicial
- **Descrição:** atualizar `gradle/libs.versions.toml` com as versões da seção 4; aplicar os plugins `kotlin.serialization` e `ksp`; `minSdk` 26; ligar `buildConfig` e criar `BuildConfig.TMDB_API_KEY` a partir de `local.properties`; remover `ExampleUnitTest`, `ExampleInstrumentedTest`, `androidTest`, Espresso e dependências de teste de UI. Criar o `README.md` com nome, descrição e a seção de configuração (como obter o token do TMDB e onde colocá-lo).
- **Pronto quando:** `./gradlew assembleDebug` passa com todas as bibliotecas da stack declaradas; `BuildConfig.TMDB_API_KEY` existe; a chave não aparece em nenhum arquivo versionado.
- **Relacionado:** RNF03, RNF04 · Seções 1, 3.2, 4
- **Commit:** `chore: configura dependencias e build do projeto`

### [x] T03 — Aplicar o tema da identidade visual
- **Descrição:** criar `Color.kt`, `Type.kt` e `Theme.kt` em `presentation/theme/` com o código de `docs/identidade-visual.md`; criar `Dimens`; remover `ui/theme/` e `colors.xml`; tema da janela com variante noturna (`values-night/themes.xml`); `app_name` = "KeveOQ"; `MainActivity` usando `KeveOQTheme`.
- **Pronto quando:** o app abre com as cores e fontes da identidade nos temas claro e escuro, sem "flash" branco no escuro; não existe cor dinâmica.
- **Relacionado:** RNF07, RNF12 · Seções 1, 9.3
- **Commit:** `feat: aplica tema da identidade visual`

### [x] T04 — Criar o pacote core
- **Descrição:** `AppResult` (`Success`/`Failure`), `AppError` (`Network`, `Http(code)`, `Serialization`, `Unknown`), `AppDispatchers` e a interface `NetworkMonitor`. Tudo em Kotlin puro.
- **Pronto quando:** os tipos compilam e não importam `android.*`.
- **Relacionado:** RNF02, RNF09 · Seções 2.1, 7.1
- **Commit:** `feat: cria tipos de resultado, erros e dispatchers`

### [x] T05 — Application, Koin, manifesto e monitor de conexão
- **Descrição:** criar `KeveOQApp` (inicia o Koin), registrá-lo no manifesto; declarar `INTERNET`, `ACCESS_NETWORK_STATE` e `POST_NOTIFICATIONS`; implementar `AndroidNetworkMonitor` (`callbackFlow`); criar os módulos Koin (ainda quase vazios) com `appModule` (`AppDispatchers`, `Clock`, `NetworkMonitor`).
- **Pronto quando:** o app abre com o Koin iniciado, sem erros no Logcat.
- **Relacionado:** RF13, RNF02 · Seções 6.6, 10
- **Commit:** `feat: configura application, koin e monitor de conexao`

## Domain

### [x] T06 — Modelos de domínio
- **Descrição:** `Mood`, `MoodSearchCriteria`, `Movie`, `Recommendations`, `MovieDetails`, `Video`, `SavedMovie`, `WatchStatus` e os resultados `AddToWatchlistResult`, `MarkAsWatchedResult`, `RateMovieResult`.
- **Pronto quando:** compilam, em Kotlin puro, com os campos da seção 2.3.
- **Relacionado:** RNF02 · Seções 2.3, 2.4
- **Commit:** `feat: cria modelos de dominio`

### [x] T07 — Mapeamento humor → critérios
- **Descrição:** `MoodCriteriaMapper` com a tabela do `CLAUDE.md` e mínimo de 100 votos. Testes unitários para os 8 humores. Criar o `TEST_RESULTS.md` com a primeira execução registrada.
- **Pronto quando:** testes passam e cobrem Ansioso (exclusões) e Nostálgico (data).
- **Relacionado:** RF03, RN01, RN02 · CA02, CA03
- **Commit:** `feat: implementa mapeamento de humor para generos`

### [x] T08 — Regras de recomendação e de cache
- **Descrição:** `RecommendationRules` (duplicados, assistidos, mínimo de votos, mantendo a ordem) e `CachePolicy` (24 h, com relógio fixo nos testes).
- **Pronto quando:** testes passam, incluindo os limites de 23 h 59 min, 24 h e 25 h.
- **Relacionado:** RN02, RN03, RN04, RN05 · CA04–CA07
- **Commit:** `feat: implementa regras de recomendacao e validade do cache`

### [x] T09 — Regras de trailer e de avaliação
- **Descrição:** `TrailerSelector` (YouTube + Trailer, oficial primeiro) e `RatingRules` (1 a 5).
- **Pronto quando:** testes passam para oficial × não oficial, teaser, outro site, lista vazia e notas 0, 1, 5 e 6.
- **Relacionado:** RN08, RN09 · CA15, CA17, CA24
- **Commit:** `feat: implementa selecao de trailer e validacao de nota`

### [x] T10 — Janela da notificação
- **Descrição:** `NotificationWindow`: atraso até a próxima janela 19h–21h.
- **Pronto quando:** testes passam para 14h, 19h, 20h59, 21h, 22h e virada de mês.
- **Relacionado:** RN10 · CA29
- **Commit:** `feat: calcula atraso ate a janela da notificacao`

### [x] T11 — Repositórios e casos de uso de recomendações
- **Descrição:** interfaces `MovieRepository` e `SavedMovieRepository`; `GetMoodsUseCase`, `ObserveRecommendationsUseCase`, `RefreshRecommendationsUseCase`, `ObserveConnectivityUseCase`. Testes com repositórios e monitor falsos.
- **Pronto quando:** testes cobrem cache válido (não chama API), expirado, `force`, offline (`Network`) e a remoção de assistidos em tempo real.
- **Relacionado:** RF01, RF03–RF05, RF13 · CA04, CA06–CA08
- **Commit:** `feat: cria casos de uso de recomendacoes`

### [x] T12 — Casos de uso de detalhes e trailer
- **Descrição:** `ObserveMovieDetailsUseCase` e `RefreshMovieDetailsUseCase` (detalhes + trailer em pt-BR, com fallback en-US; validade de 24 h).
- **Pronto quando:** testes cobrem fallback para en-US, filme sem trailer, cache válido e offline.
- **Relacionado:** RF06, RF07, RF20, RN09 · CA12, CA15–CA17
- **Commit:** `feat: cria casos de uso de detalhes e trailer`

### [x] T13 — Casos de uso da lista "Quero assistir"
- **Descrição:** `ObserveSavedMovieUseCase`, `ObserveWatchlistUseCase`, `AddToWatchlistUseCase`, `MarkAsWatchedUseCase`, `RateMovieUseCase`.
- **Pronto quando:** testes cobrem cada resultado (`AlreadyInList`, `AlreadyWatched`, `NotInList`, `NotWatched`, `InvalidRating`), troca de nota e filtro por status.
- **Relacionado:** RF08–RF11, RN06–RN08 · CA20–CA24
- **Commit:** `feat: cria casos de uso da lista quero assistir`

### [x] T14 — Caso de uso do agendamento da notificação
- **Descrição:** `CalculateNotificationDelayUseCase` usando `NotificationWindow` e o `Clock` injetado.
- **Pronto quando:** testes com relógio fixo passam.
- **Relacionado:** RF17, RN10 · CA29
- **Commit:** `feat: cria caso de uso do atraso da notificacao`

## Data

### [x] T15 — API do TMDB
- **Descrição:** `TmdbApi` (discover, details, videos), DTOs com kotlinx.serialization, `AuthInterceptor` (Bearer), `logging-interceptor` só em debug com `Authorization` ocultado, `safeApiCall` e mappers DTO → domínio; registrar no `dataModule`.
- **Pronto quando:** testes de `safeApiCall` (cada exceção → `AppError`, `CancellationException` relançada) e dos mappers (URL do pôster, ano, `with_genres` com `|`) passam.
- **Relacionado:** RNF04, RNF09 · Seções 3, 7.1
- **Commit:** `feat: implementa cliente da api do tmdb`

### [x] T16 — Banco Room
- **Descrição:** `AppDatabase` (v1), entidades `movies`, `movie_details`, `mood_recommendations`, `saved_movies`, `MovieDao`, `SavedMovieDao`, `Converters` (gêneros em JSON) e mappers entidade ↔ domínio; registrar no `dataModule`.
- **Pronto quando:** compila (KSP gera o código do Room) e os testes dos mappers e do `Converters` passam.
- **Relacionado:** RNF01 · Seção 5
- **Commit:** `feat: cria banco de dados local`

### [x] T17 — Repositório de filmes
- **Descrição:** `MovieRepositoryImpl`: observar recomendações e detalhes; atualizar recomendações (páginas 1 e 2, tudo ou nada, em transação); atualizar detalhes e vídeos.
- **Pronto quando:** testes com API e DAO falsos cobrem: cache válido, cache expirado, falha de rede com cache (cache preservado), falha sem cache, duas páginas consultadas.
- **Relacionado:** RF04, RF07, RF14, RF15, RN11 · CA09, CA10, CA33
- **Commit:** `feat: implementa repositorio de filmes`

### [x] T18 — Repositório da lista salva
- **Descrição:** `SavedMovieRepositoryImpl`; registrar repositórios e casos de uso nos módulos Koin.
- **Pronto quando:** testes com DAO falso cobrem salvar, marcar como assistido, avaliar, observar por status e IDs assistidos.
- **Relacionado:** RF08–RF11, RNF05 · CA20–CA25
- **Commit:** `feat: implementa repositorio da lista quero assistir`

## Presentation

### [x] T19 — Navegação, Scaffold raiz e componentes base
- **Descrição:** rotas type-safe, `NavHost`, `KeveOQAppContent` com `OfflineBanner`; componentes `LoadingState`, `EmptyState`, `ErrorState`, `PosterImage` (Coil, com marcador offline), `MovieListItem`, `RatingBar`; `ErrorMessageMapper` e `UpdatedAtFormatter` com testes; ícones via Vector Asset (`bookmark`, `cloud_off`, `movie`).
- **Pronto quando:** previews claro/escuro de cada componente; banner aparece ao desligar a internet; testes dos formatadores passam.
- **Relacionado:** RF13, RF23 · CA11, CA13, CA32 · Seções 6.6, 7.2, 9
- **Commit:** `feat: cria navegacao e componentes base`

### [x] T20 — Tela de seleção de humor
- **Descrição:** `MoodRoute`/`MoodScreen`/`MoodViewModel`: título, grade de 8 cards com dicas, botão "Ver filmes", ícones "Quero assistir" e "Sobre" na barra superior.
- **Pronto quando:** seleção sobrevive à rotação; "Ver filmes" abre as recomendações do humor; teste do ViewModel passa.
- **Relacionado:** RF01, RF02, RF24 · CA01, CA34
- **Commit:** `feat: implementa tela de selecao de humor`

### [x] T21 — Tela de recomendações
- **Descrição:** `RecommendationsRoute`/`Screen`/`ViewModel`: lista, "Atualizado há…", pull-to-refresh, Snackbar, estados Loading/Success/Empty/Error com "Tentar novamente".
- **Pronto quando:** testes do ViewModel (Turbine + `StandardTestDispatcher`) cobrem Loading → Success, Empty, Error e Snackbar com cache; tocar no filme abre os detalhes.
- **Relacionado:** RF03–RF05, RF14, RF15, RF23 · CA06–CA10, CA32
- **Commit:** `feat: implementa tela de recomendacoes`

### [x] T22 — Tela de detalhes
- **Descrição:** `DetailsRoute`/`Screen`/`ViewModel`: pôster, título, ano, duração, nota, chips de gênero, sinopse; ação conforme o status ("Quero assistir", "Já assisti" ou estrelas).
- **Pronto quando:** testes do ViewModel cobrem Loading → Success, Error offline sem cache, salvar, marcar como assistido e avaliar.
- **Relacionado:** RF06–RF08, RF10, RF11 · CA12, CA14, CA20, CA22, CA24
- **Commit:** `feat: implementa tela de detalhes do filme`

### [x] T23 — Trailer
- **Descrição:** `TrailerPlayer` (android-youtube-player em `AndroidView`, ligado ao ciclo de vida); seção visível só com conexão e trailer; botão "Assistir no YouTube" em caso de erro do player.
- **Pronto quando:** trailer toca com internet; seção some offline; erro do player mostra o botão, que abre o YouTube ou o navegador. Teste do ViewModel para a regra de visibilidade.
- **Relacionado:** RF20–RF22 · CA15, CA18, CA19
- **Commit:** `feat: adiciona trailer na tela de detalhes`

### [x] T24 — Tela "Quero assistir"
- **Descrição:** `WatchlistRoute`/`Screen`/`ViewModel`: chips de filtro, itens com "Já assisti" ou estrelas, estado vazio com "Escolher um humor".
- **Pronto quando:** testes do ViewModel cobrem filtro, Empty e Success; a lista funciona offline.
- **Relacionado:** RF09–RF12, RF23 · CA21–CA26
- **Commit:** `feat: implementa tela quero assistir`

### [x] T25 — Tela "Sobre"
- **Descrição:** `AboutRoute`/`Screen`/`ViewModel`: nome, versão, descrição, atribuição ao TMDB e créditos das fontes.
- **Pronto quando:** texto de atribuição exatamente como no `CLAUDE.md`; teste do ViewModel passa.
- **Relacionado:** RF16, RNF12 · CA27
- **Commit:** `feat: implementa tela sobre`

## Notificação

### [x] T26 — Notificação periódica
- **Descrição:** canal "Sugestões de filmes", `MovieSuggestionWorker`, `NotificationScheduler` (`KEEP`, 4 dias, atraso inicial do domain), ícone monocromático via Vector Asset, `PendingIntent` para a tela de humor, pedido de permissão na primeira abertura com `NotificationPermissionPrefs`.
- **Pronto quando:** a permissão é pedida uma única vez (Android 13+); abrir o app várias vezes mantém um só agendamento (verificado no App Inspection → Background Task Inspector); disparo manual mostra a notificação e o toque abre a seleção de humor.
- **Relacionado:** RF17–RF19, RN10 · CA28, CA30, CA31 · Seção 8
- **Commit:** `feat: adiciona notificacao periodica`

## Sorteio de filme (acrescentado após a T26)

### [x] T26A — Caso de uso do sorteio
- **Descrição:** `RandomMovieResult` (`Found(movieId, mood)`, `NoMovieAvailable`) e `PickRandomMovieUseCase`, com `Random` injetado, reaproveitando `RefreshRecommendationsUseCase` e `ObserveRecommendationsUseCase`; registrar no `domainModule` e `Random.Default` no `appModule`.
- **Pronto quando:** testes com repositórios falsos e `Random` fixo cobrem: sorteio com conexão, filme assistido nunca sorteado, humor sem filmes passa para o próximo, offline com cache em um único humor (CA36), nenhum filme disponível (CA37) e cache válido sem chamar a API.
- **Relacionado:** RF25, RN12 · CA35–CA37 · Seção 2.4, decisão 15
- **Commit:** `feat: cria caso de uso do sorteio de filme`

### [x] T26B — Botão "Me surpreenda"
- **Descrição:** ícone `ic_casino` (Material Icons); `OutlinedButton` "Me surpreenda" acima de "Ver filmes"; `MoodViewModel` com `isPickingRandom` e eventos únicos (abrir detalhes ou Snackbar "não há filmes para sortear"); Snackbar na tela de humor; navegação para os detalhes.
- **Pronto quando:** testes do ViewModel cobrem: navegação para o filme sorteado, Snackbar sem filme, indicador ligado durante o sorteio e toque ignorado enquanto sorteia; previews claro/escuro atualizados; verificado no aparelho online e offline.
- **Relacionado:** RF25, RF26 · CA35–CA37 · Seção 9.2
- **Commit:** `feat: adiciona botao me surpreenda na tela inicial`

## Onde assistir (acrescentado após a T26B)

### [x] T26C — Dados e regras de "onde assistir"
- **Descrição:** modelos `WatchProviders` e `WatchProvider`; `TheatricalRule` (RN14); `MovieDetails` com `watchProviders` e `theatricalReleaseBr`; DTOs de `release_dates` e `watch/providers` e `append_to_response` na chamada de detalhes; mappers (Brasil, três grupos, sem repetidos, URL do logo, data de cinema tipos 2 e 3); `movie_details` com as novas colunas, banco na versão 2 e `MIGRATION_1_2`.
- **Pronto quando:** testes passam para `TheatricalRule` (dia da estreia, 42 dias, 43 dias, antes da estreia, sem data), leitura do JSON com `append_to_response`, mappers (só `BR`, grupos vazios, repetidos, logo, tipo de lançamento) e conversão das novas colunas; a migração preserva a lista "Quero assistir" (verificado no aparelho).
- **Relacionado:** RF27, RF28, RN13, RN14 · CA38–CA41 · decisão 16
- **Commit:** `feat: busca e guarda onde assistir cada filme`

### [ ] T26D — Seção "Onde assistir" nos detalhes
- **Descrição:** seção na tela de detalhes com o selo "Em cartaz nos cinemas", os grupos "Incluso na assinatura", "Alugar" e "Comprar" (logo e nome), mensagem quando não há serviços, atribuição "Dados de disponibilidade: JustWatch" e o botão "Ver opções no JustWatch" (só com conexão e link).
- **Pronto quando:** testes do `DetailsViewModel` cobrem selo dentro e fora do período, grupos repassados, botão escondido offline; previews claro/escuro; verificado no aparelho.
- **Relacionado:** RF27–RF29 · CA38–CA41
- **Commit:** `feat: adiciona onde assistir nos detalhes do filme`

## Validação

### [ ] T27 — README completo
- **Descrição:** revisar o `README.md` com todos os itens exigidos pelo `CLAUDE.md`: objetivo, funcionalidades, tecnologias, API e justificativa, arquitetura, Offline First, configuração, execução, testes, evidências, limitações conhecidas e créditos das fontes.
- **Pronto quando:** todos os itens presentes e descrevendo apenas o que existe.
- **Relacionado:** RNF11, RNF12
- **Commit:** `docs: completa readme do projeto`

### [ ] T28 — Execução final dos testes e evidências
- **Descrição:** rodar `./gradlew testDebugUnitTest` e `./gradlew assembleDebug`, registrar em `TEST_RESULTS.md`; percorrer os critérios de aceite manualmente; organizar e referenciar em `evidencias/` as capturas online e offline feitas por você.
- **Pronto quando:** todos os testes passam, o relatório reflete a execução real e as evidências estão referenciadas no README.
- **Relacionado:** RNF10 · todos os CA
- **Commit:** `test: registra execucao final dos testes e evidencias`

### [ ] T29 — APK de entrega
- **Descrição:** com sua autorização, gerar o APK de depuração e copiá-lo para `entrega/`.
- **Pronto quando:** `entrega/app-debug.apk` instala e abre em um aparelho limpo.
- **Relacionado:** entrega acadêmica
- **Commit:** `chore: adiciona apk de depuracao para entrega`
