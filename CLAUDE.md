# CLAUDE.md — KeveOQ

## Contexto do projeto

Este repositório contém o **KeveOQ**, um aplicativo Android que recomenda filmes de acordo com o humor do usuário. É um trabalho acadêmico, e os critérios de avaliação estão refletidos nestas instruções. Siga-as em todas as sessões.

O usuário escolhe como está se sentindo; o app traduz esse humor em critérios de busca (gêneros e filtros), consulta a API do TMDB e apresenta recomendações. O usuário pode ver detalhes e o trailer, salvar filmes na lista "Quero assistir", marcá-los como assistidos e avaliá-los. O app envia periodicamente uma notificação sugerindo seu uso. As funcionalidades principais devem funcionar sem internet.

## Regras de trabalho (leia antes de qualquer ação)

1. Siga o Spec-Driven Development (SDD), nesta ordem: Especificação → Planejamento → Decomposição → Implementação → Validação. Não escreva código de produção antes de os documentos de especificação, planejamento e tarefas estarem aprovados por mim.
2. Ao final de cada fase do SDD, pare e peça minha aprovação antes de seguir.
3. Antes de qualquer ação que crie ou altere arquivos, explique o que vai fazer e por quê, e aguarde minha confirmação.
4. Trabalhe uma tarefa por vez, na ordem de `docs/03-tarefas.md`. Ao concluir, marque a tarefa como concluída no arquivo.
5. Não adicione funcionalidades, bibliotecas ou telas fora da especificação. Se achar que algo é necessário, pergunte.
6. Em caso de dúvida ou ambiguidade, pergunte em vez de supor.
7. Explique as decisões técnicas de forma didática: este é um projeto de aprendizado e preciso entender e conseguir defender cada parte do código.
8. Escreva respostas e documentação em português do Brasil. Nomes de classes, funções e variáveis em inglês.
9. Toda interface deve seguir `docs/identidade-visual.md`. Não crie cores, fontes ou formas fora dele.

## Stack obrigatória

| Área | Tecnologia |
|---|---|
| Linguagem | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Navegação | Navigation Compose com rotas type-safe (kotlinx.serialization) |
| Injeção de dependências | Koin (koin-android, koin-androidx-compose) |
| Assíncrono | Kotlin Coroutines, Flow e StateFlow |
| Rede | Retrofit + OkHttp + conversor kotlinx.serialization |
| Persistência | Room (com KSP) |
| Imagens | Coil (com cache em disco) |
| Agendamento | WorkManager (`work-runtime-ktx`) |
| Player de trailer | android-youtube-player (`core`), usado no Compose via `AndroidView` |
| Testes | JUnit 4, MockK, kotlinx-coroutines-test, Turbine |
| Build | Gradle Kotlin DSL + Version Catalog (`gradle/libs.versions.toml`) |

Use as versões estáveis mais recentes e compatíveis entre si. Não use Hilt, Dagger, RxJava, LiveData, layouts XML nem Gson.

## API: The Movie Database (TMDB)

- Documentação: https://developer.themoviedb.org/docs
- Base URL: `https://api.themoviedb.org/3/`
- Imagens: `https://image.tmdb.org/t/p/w500{poster_path}`
- Endpoints usados:
  - `GET discover/movie`: recomendações (`with_genres`, `without_genres`, `sort_by=popularity.desc`, `vote_count.gte`, `primary_release_date.lte`, `language=pt-BR`, `page`). Em `with_genres`, use `|` (OU) entre gêneros; vírgula significa E.
  - `GET movie/{movie_id}`: detalhes (`language=pt-BR`).
  - `GET movie/{movie_id}/videos`: trailer. Buscar com `language=pt-BR`; se não houver trailer, repetir com `language=en-US`.
- Link externo do trailer: `https://www.youtube.com/watch?v={key}`
- Autenticação: a chave fica em `local.properties` (`TMDB_API_KEY=...`), é exposta via `BuildConfig` e adicionada às requisições por um Interceptor do OkHttp. **Nunca** escreva a chave em código, commits ou documentação. Confirme que `local.properties` está no `.gitignore`.
- Atribuição obrigatória: a tela "Sobre" deve exibir "This product uses the TMDB API but is not endorsed or certified by TMDB."

## Regras de negócio (camada Domain)

Estas regras ficam na camada Domain, em Kotlin puro, e devem ser cobertas por testes unitários.

### Mapeamento humor → critérios de busca

| Humor | Gêneros (IDs TMDB) | Filtro extra |
|---|---|---|
| Feliz | Comédia (35), Animação (16), Família (10751) | — |
| Triste | Drama (18), Romance (10749) | — |
| Animado | Ação (28), Aventura (12) | — |
| Ansioso | Comédia (35), Animação (16), Família (10751) | excluir Terror (27) e Thriller (53) |
| Entediado | Mistério (9648), Ficção científica (878), Thriller (53) | — |
| Romântico | Romance (10749) | — |
| Nostálgico | Aventura (12), Família (10751) | lançados até 31/12/1999 |
| Corajoso | Terror (27), Thriller (53) | — |

### Demais regras

- Recomendar apenas filmes com pelo menos 100 votos, para evitar notas distorcidas.
- Remover das recomendações os filmes já marcados como assistidos.
- Não exibir filmes duplicados.
- O cache de recomendações de cada humor vale 24 horas.
- Um filme não pode ser adicionado duas vezes à lista "Quero assistir".
- Status de filme salvo: `WANT_TO_WATCH` → `WATCHED`.
- Avaliação pessoal (1 a 5 estrelas) só é permitida para filmes `WATCHED`; valores fora do intervalo são rejeitados.
- Trailer: considerar apenas vídeos com `site = YouTube` e `type = Trailer`, priorizando `official = true`. Sem nenhum vídeo válido, o filme não tem trailer.
- Notificação: a cada 4 dias, com a primeira execução no próximo intervalo entre 19h e 21h. O cálculo do atraso inicial é uma regra do domain, testável com relógio fixo.

## Escopo

Dentro do escopo:
1. Seleção de humor (tela inicial).
2. Recomendações do humor escolhido, com pull-to-refresh.
3. Detalhes do filme (pôster, título, sinopse, nota, ano, duração, gêneros).
4. Lista "Quero assistir", com filtro por status, marcação como assistido e avaliação.
5. Indicador de modo offline.
6. Tela "Sobre" com a atribuição ao TMDB.
7. Notificação periódica sugerindo o uso do app.
8. Trailer do filme na tela de detalhes (somente com conexão).

Fora do escopo: login e cadastro, contas do TMDB e busca livre por texto.

## Arquitetura

Clean Architecture em um único módulo `:app`, com separação por pacotes e regra de dependência rígida:

```
presentation  →  domain  ←  data
```

- **domain**: modelos, interfaces de repositório, casos de uso e regras de negócio. Kotlin puro: é **proibido** importar `android.*`, Retrofit, Room ou Koin.
- **data**: implementa os repositórios do domain. Contém DTOs, entidades Room, DAOs, API Retrofit e mappers. DTOs e entidades nunca saem desta camada.
- **presentation**: telas Compose, ViewModels, UiState e navegação. Acessa apenas casos de uso.
- **notification**: Worker, canal de notificação e agendamento com WorkManager. Usa o domain para calcular o horário; não contém regra de negócio.
- **di**: módulos Koin; é o único lugar que conhece todas as camadas.

```
com.radierdev.keveoq/
├── KeveOQApp.kt           # Application: inicializa o Koin
├── MainActivity.kt
├── di/                    # dataModule, domainModule, presentationModule
├── core/                  # AppResult, AppError, dispatchers, monitor de conectividade
├── notification/          # Worker, canal de notificação e agendamento (WorkManager)
├── domain/
│   ├── model/             # Mood, Movie, MovieDetails, SavedMovie, WatchStatus
│   ├── repository/        # interfaces
│   └── usecase/
├── data/
│   ├── remote/            # TmdbApi, DTOs, interceptor da chave
│   ├── local/             # AppDatabase, DAOs, entidades
│   ├── mapper/
│   └── repository/        # implementações
└── presentation/
    ├── navigation/
    ├── theme/             # Color.kt, Type.kt, Theme.kt (ver docs/identidade-visual.md)
    ├── components/
    └── screens/           # mood/, recommendations/, details/, watchlist/, about/
```

## Offline First e sincronização

- O **Room é a única fonte de verdade**. A UI observa `Flow` vindos do Room; a rede apenas atualiza o banco.
- Ao abrir um humor:
  1. Emitir imediatamente o que estiver em cache.
  2. Se o cache estiver vazio ou tiver mais de 24 h e houver conexão, buscar na API e gravar no Room (o Flow emite os dados novos sozinho).
  3. Se a requisição falhar e houver cache, manter os dados na tela e mostrar o erro de forma não bloqueante (Snackbar).
  4. Sem cache e sem conexão: estado de erro com botão "Tentar novamente".
- Pull-to-refresh força a atualização, ignorando a validade do cache.
- Detalhes do filme são gravados no Room quando abertos ou salvos, para ficarem disponíveis offline.
- A chave do trailer é gravada em `movie_details` junto com os detalhes, mas o trailer só é exibido com conexão; offline, a seção não aparece.
- Se o player incorporado não conseguir reproduzir o vídeo, exibir o botão "Assistir no YouTube", que abre o app do YouTube ou o navegador.
- A lista "Quero assistir" e as avaliações são dados exclusivamente locais: não são enviados ao servidor, logo não há conflito de sincronização.
- Tabelas sugeridas: `movies`, `movie_details` (incluindo a chave do trailer), `mood_recommendations` (mood, movieId, position, updatedAt), `saved_movies`.
- Pôsteres dependem do cache de disco do Coil; imagens nunca carregadas não aparecem offline (documentar como limitação).

## Notificações

- Agendada com WorkManager (`PeriodicWorkRequest` de 4 dias) e `ExistingPeriodicWorkPolicy.KEEP`, para não duplicar o agendamento a cada abertura do app.
- O WorkManager não garante horário exato: a notificação pode atrasar por economia de bateria (documentar como limitação).
- No Android 13+, a permissão `POST_NOTIFICATIONS` é pedida na primeira abertura. Se negada, o app funciona normalmente, sem notificações.
- Tocar na notificação abre a tela de seleção de humor.
- Textos da notificação em português. Ícone monocromático importado pelo Vector Asset.

## Padrões de código

### Estado e Compose
- Cada tela tem um ViewModel que expõe um único `StateFlow<XUiState>`; eventos únicos (como Snackbar) via `Channel` exposto como `Flow`.
- O UiState deve representar Loading, Success, Empty e Error.
- Coletar estado com `collectAsStateWithLifecycle()`.
- Separar cada tela em `XRoute` (conecta o ViewModel) e `XScreen` (stateless, recebe estado e lambdas).
- Evitar recomposições: modelos de UI imutáveis (`@Immutable` ou `kotlinx.collections.immutable`), `key` em listas lazy, `remember`/`derivedStateOf` para cálculos derivados.
- Toda tela deve ter preview nos dois temas com `@PreviewLightDark`, envolvida em `KeveOQTheme`.
- Cores, tipografia e formas vêm apenas de `MaterialTheme` (`colorScheme`, `typography`, `shapes`). Nunca use `Color(0xFF...)`, `fontSize` ou `fontFamily` diretamente em telas.

### Erros
- Repositórios não propagam exceções: retornam `AppResult<T>` (`Success` / `Failure(AppError)`).
- `AppError`: `Network` (sem conexão/timeout), `Http(code)`, `Serialization`, `Unknown`.
- Mensagens ao usuário em português, amigáveis, sem detalhes técnicos.
- Não usar `!!`. Nunca engolir `CancellationException`: relance-a.

### Memória e threads
- Nenhum `Context`, `Activity` ou `View` em ViewModels ou na camada domain.
- Coroutines apenas em `viewModelScope` ou escopos gerenciados; nunca `GlobalScope`.
- Dispatchers injetados via Koin, para poderem ser substituídos nos testes.

## Testes

- Testes unitários em `app/src/test`, cobrindo no mínimo:
  - mapeamento humor → critérios de busca;
  - todos os casos de uso e regras de negócio acima;
  - regra de seleção do trailer;
  - cálculo do atraso até a próxima janela de 19h às 21h;
  - repositório com API e DAO falsos: cache válido, cache expirado, falha de rede com cache e falha sem cache;
  - ViewModels: transições Loading → Success / Empty / Error (Turbine + `StandardTestDispatcher`).
- Nomes descritivos em português: `` `deve remover filmes assistidos das recomendacoes`() ``.
- Comando: `./gradlew testDebugUnitTest` (relatório em `app/build/reports/tests/testDebugUnitTest/index.html`).
- Após cada execução relevante, atualizar `TEST_RESULTS.md` com data, comando, total, aprovados, falhas e correções aplicadas. Nunca registre resultado de teste que não foi realmente executado.

## Git e commits

- Commits pequenos e frequentes, um por tarefa concluída, no padrão Conventional Commits em português (`docs:`, `feat:`, `fix:`, `test:`, `refactor:`, `chore:`). Exemplo: `feat: implementa mapeamento de humor para generos`.
- Antes de cada commit, mostre os arquivos alterados e a mensagem proposta e aguarde minha aprovação.
- Nunca agrupar várias funcionalidades em um único commit nem reescrever histórico (`rebase`, `--amend` de commits enviados, `push --force`).
- Nunca commitar `local.properties`, chaves, `build/` ou `.gradle/`.

## Documentação SDD e entregáveis

| Arquivo | Conteúdo |
|---|---|
| `docs/01-especificacao.md` | proposta, objetivos, público, funcionalidades, requisitos funcionais (RF) e não funcionais (RNF), regras de negócio, critérios de aceite |
| `docs/02-planejamento.md` | arquitetura, camadas, stack, justificativa da API, modelo de dados, estratégia offline e de sincronização, tratamento de erros, estratégia de testes |
| `docs/03-tarefas.md` | tarefas numeradas em ordem lógica, cada uma com descrição, critério de pronto, RF relacionado, mensagem de commit prevista e checkbox de status |
| `docs/identidade-visual.md` | tema do app: cores, tipografia, formas, uso de cada papel de cor e código de `Color.kt`, `Type.kt` e `Theme.kt` (fornecido por mim; não altere sem pedir) |
| `docs/licencas/` | licença OFL das fontes Bricolage Grotesque e Nunito |
| `README.md` | todos os itens obrigatórios listados abaixo |
| `TEST_RESULTS.md` | relatório de execução dos testes |
| `evidencias/` | capturas de tela online e offline (feitas por mim; o agente apenas organiza e referencia) |
| `entrega/` | APK de depuração, copiado apenas no final, com minha autorização |

O README deve conter: nome e descrição; objetivo e problema resolvido; funcionalidades implementadas; tecnologias; API usada, com link e justificativa; arquitetura e camadas; estratégia Offline First; configuração (incluindo como obter e configurar a chave do TMDB); execução; como rodar os testes; evidências dos testes; limitações conhecidas; créditos das fontes. Atualize-o ao concluir cada funcionalidade, descrevendo apenas o que de fato existe.

APK de depuração: `./gradlew assembleDebug` → `app/build/outputs/apk/debug/app-debug.apk`.

## Definição de pronto (cada tarefa)

- Compila sem erros (`./gradlew assembleDebug`).
- Testes passando (`./gradlew testDebugUnitTest`).
- Regra de dependência entre camadas respeitada.
- Interface conforme `docs/identidade-visual.md`, com preview nos temas claro e escuro.
- Documentação afetada atualizada (tarefas, README e, se houver testes, TEST_RESULTS).
- Commit aprovado e realizado.
