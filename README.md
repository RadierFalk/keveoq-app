# KeveOQ

<img src="icone/keveoq-icone-circular.svg" alt="Ícone do KeveOQ" width="96" align="right">

**KeveOQ** ("o que eu vejo?") é um aplicativo Android que recomenda filmes de acordo com o humor do usuário. A pessoa responde "Como você está hoje?", e o app traduz o humor em critérios de busca na API do The Movie Database (TMDB), mostra onde assistir cada filme e guarda o que ela quer ver e o que já viu, mesmo sem internet.

Projeto acadêmico desenvolvido com **Spec-Driven Development (SDD)**: especificação, planejamento e decomposição em tarefas foram aprovados antes do código. Os documentos estão em [`docs/`](docs/):

| Documento | Conteúdo |
|---|---|
| [`01-especificacao.md`](docs/01-especificacao.md) | Proposta, requisitos funcionais (RF) e não funcionais (RNF), regras de negócio (RN) e critérios de aceite (CA) |
| [`02-planejamento.md`](docs/02-planejamento.md) | Arquitetura, stack, modelo de dados, estratégia offline, erros, testes e decisões registradas |
| [`03-tarefas.md`](docs/03-tarefas.md) | Tarefas numeradas, cada uma com critério de pronto e commit |
| [`identidade-visual.md`](docs/identidade-visual.md) | Cores, tipografia, formas e uso de cada papel de cor |

## Objetivo e problema resolvido

Escolher um filme costuma levar mais tempo do que assisti-lo: os catálogos são enormes, organizados por gênero, e nem sempre a pessoa sabe que gênero quer. O KeveOQ inverte a pergunta: parte de algo que o usuário sabe responder (como se sente) e traduz isso em gêneros e filtros. Para quem não quer nem escolher o humor, há o sorteio "Me surpreenda". O app também diz onde o filme pode ser assistido, guarda a lista "Quero assistir" com avaliações pessoais e mantém tudo o que já foi carregado disponível offline.

## Funcionalidades implementadas

- **Seleção de humor (tela inicial):** a pergunta "Como você está hoje?" e oito humores em cards com uma dica de gênero. O botão "Ver filmes" é habilitado ao escolher um humor, e a escolha é mantida ao girar o aparelho. A barra superior tem o logo e atalhos para "Quero assistir" e "Sobre".
- **Sorteio de filme ("Me surpreenda"):** para quem não quer escolher um humor, o botão sorteia um humor, pega as recomendações dele (salvas ou da internet) e abre os detalhes de um filme aleatório que ainda não foi assistido. Sem internet, sorteia entre os humores que já têm filmes salvos; se não houver nenhum, avisa.
- **Recomendações por humor:** lista de filmes do TMDB (pôster, título, ano e nota) para o humor escolhido, sem filmes já assistidos nem repetidos, com "Atualizado há…". Os dados ficam salvos no aparelho e valem 24 horas; puxar a lista para baixo força uma nova busca. Sem internet, a lista salva continua aparecendo e um aviso informa a falha; sem lista salva e sem internet, aparece uma mensagem com "Tentar novamente".
- **Detalhes do filme:** pôster, título, ano, duração, nota, gêneros e sinopse. Os detalhes ficam salvos no aparelho ao serem abertos, para consulta sem internet. Conforme a situação do filme, a tela oferece "Quero assistir", "Já assisti" ou as estrelas para avaliar (1 a 5, podendo trocar a nota).
- **Onde assistir:** nos detalhes, os serviços do Brasil onde o filme está disponível, agrupados em "Incluso na assinatura", "Alugar" e "Comprar", com logo e nome; o selo "Em cartaz nos cinemas" para filmes que estrearam no Brasil há até 6 semanas; e o botão "Ver opções no JustWatch", que abre a página com os links de cada serviço. Os serviços ficam salvos e aparecem offline; o botão só aparece com internet.
- **Trailer:** com internet, os detalhes mostram o trailer num player do YouTube incorporado (prioridade ao trailer oficial, em português ou, se não houver, em inglês). Sem internet ou sem trailer, a seção não aparece. Se o vídeo não puder ser reproduzido no app, aparece o botão "Assistir no YouTube".
- **Lista "Quero assistir":** filmes salvos, do mais recente para o mais antigo, com filtro (Todos, Quero assistir, Assistidos), botão "Já assisti" e estrelas para avaliar os assistidos. Funciona sem internet, porque fica só no aparelho. Lista vazia mostra o botão "Escolher um humor".
- **Indicador de modo offline:** o banner "Sem internet. Mostrando o que já está salvo." aparece no topo de todas as telas enquanto o aparelho está sem conexão e some quando ela volta.
- **Notificação periódica:** a cada 4 dias, a notificação "Que tal um filme hoje?" sugere usar o app; a primeira é agendada para a próxima janela entre 19h e 21h. Tocar nela abre a seleção de humor. No Android 13 ou superior, a permissão é pedida uma única vez, na primeira abertura; se for negada, o app funciona normalmente, sem notificações.
- **Tela "Sobre":** nome e versão do app, descrição, atribuição ao TMDB e créditos das fontes.
- **Temas claro e escuro:** seguem o sistema, com as cores e fontes da identidade visual.

## Tecnologias

| Área | Tecnologia | Versão |
|---|---|---|
| Linguagem | Kotlin | 2.4.21 |
| Interface | Jetpack Compose + Material 3 (Compose BOM) | 2026.09.00 |
| Navegação | Navigation Compose com rotas type-safe (kotlinx.serialization) | 2.10.2 |
| Injeção de dependências | Koin (`koin-android`, `koin-androidx-compose`) | 4.2.2 |
| Assíncrono | Kotlin Coroutines, Flow e StateFlow | 1.11.0 |
| Rede | Retrofit + OkHttp + conversor kotlinx.serialization | 3.0.0 / 5.5.1 |
| Persistência | Room (com KSP) | 2.8.5 |
| Imagens | Coil 3 (com cache em disco) | 3.6.3 |
| Agendamento | WorkManager (`work-runtime-ktx`) | 2.12.0 |
| Player de trailer | android-youtube-player (`core`), no Compose via `AndroidView` | 13.1.0 |
| Testes | JUnit 4, MockK, kotlinx-coroutines-test, Turbine | 4.13.2 / 1.14.12 / 1.11.0 / 1.2.1 |
| Build | Gradle Kotlin DSL + Version Catalog (`gradle/libs.versions.toml`) | AGP 9.5 |

Android mínimo: 8.0 (API 26), para usar `java.time` sem *desugaring*. `compileSdk` e `targetSdk`: 37.

## API usada: The Movie Database (TMDB)

- Documentação: [developer.themoviedb.org/docs](https://developer.themoviedb.org/docs)
- Base: `https://api.themoviedb.org/3/`

**Por que o TMDB:** é gratuito para uso não comercial e acadêmico; o endpoint `discover/movie` filtra por gêneros (inclusão e exclusão), número de votos e data, exatamente o que o mapeamento de humor precisa; tem títulos e sinopses em português; traz os trailers do YouTube e a disponibilidade em serviços de streaming (dados do JustWatch) sem uma segunda API.

| Uso | Requisição |
|---|---|
| Recomendações | `GET discover/movie` com `with_genres` (gêneros unidos por `\|`, que significa OU), `without_genres`, `vote_count.gte=100`, `primary_release_date.lte` (Nostálgico), `sort_by=popularity.desc`, `language=pt-BR` e `page` 1 e 2 |
| Detalhes, estreia e onde assistir | `GET movie/{id}?language=pt-BR&append_to_response=release_dates,watch/providers` (uma única requisição) |
| Trailer | `GET movie/{id}/videos?language=pt-BR`; sem trailer válido, repete com `language=en-US` |
| Pôsteres e logos | `https://image.tmdb.org/t/p/w500{poster_path}` e `.../w92{logo_path}` |

**Mapeamento humor → busca:**

| Humor | Gêneros (IDs TMDB) | Filtro extra |
|---|---|---|
| Feliz | Comédia (35), Animação (16), Família (10751) | — |
| Triste | Drama (18), Romance (10749) | — |
| Animado | Ação (28), Aventura (12) | — |
| Ansioso | Comédia (35), Animação (16), Família (10751) | exclui Terror (27) e Thriller (53) |
| Entediado | Mistério (9648), Ficção científica (878), Thriller (53) | — |
| Romântico | Romance (10749) | — |
| Nostálgico | Aventura (12), Família (10751) | lançados até 31/12/1999 |
| Corajoso | Terror (27), Thriller (53) | — |

Todas as buscas exigem pelo menos 100 votos, e filmes assistidos ou repetidos são removidos.

**Atribuições:** "This product uses the TMDB API but is not endorsed or certified by TMDB." (exibida na tela "Sobre"). Os dados de onde assistir são fornecidos pelo **JustWatch**, com atribuição na própria seção.

## Arquitetura e camadas

Clean Architecture em um único módulo `:app`, separada por pacotes, com regra de dependência rígida:

```
presentation  →  domain  ←  data
                   ↑
            notification (usa o domain)
di: conhece todas as camadas e as "monta"
```

| Pacote | Responsabilidade |
|---|---|
| `domain` | Modelos, interfaces de repositório, casos de uso e regras de negócio, em **Kotlin puro** (sem `android.*`, Retrofit, Room ou Koin), testáveis na JVM |
| `data` | Implementa os repositórios: Retrofit (`TmdbApi`), Room (`AppDatabase`, DAOs), DTOs, entidades e mappers. DTOs e entidades nunca saem desta camada |
| `presentation` | Telas Compose, ViewModels, `UiState`, navegação e tema. Acessa apenas casos de uso |
| `notification` | Worker, canal e agendamento com WorkManager; o horário vem do domain |
| `core` | `AppResult`, `AppError`, dispatchers e monitor de conectividade |
| `di` | Módulos do Koin (`appModule`, `dataModule`, `domainModule`, `presentationModule`) |

Padrões usados:

- **Inversão de dependência:** o domain declara as interfaces (`MovieRepository`, `SavedMovieRepository`) e a camada data as implementa.
- **Erros como valores:** repositórios devolvem `AppResult` (`Success` ou `Failure(AppError)`) e nunca lançam exceções; `CancellationException` é sempre relançada. Recusas de regra de negócio têm tipos próprios (por exemplo, `AddToWatchlistResult.AlreadyInList`).
- **Telas sem estado:** cada tela tem `XRoute` (liga o ViewModel) e `XScreen` (recebe estado e lambdas), um único `StateFlow<XUiState>` e eventos únicos (Snackbar, navegação) por `Channel`.
- **Relógio, dispatchers e sorteio injetados** (`Clock`, `AppDispatchers`, `Random`), substituídos por versões fixas nos testes.

## Estratégia Offline First

- O **Room é a única fonte de verdade.** As telas observam `Flow` do banco; a rede apenas atualiza o banco, e o `Flow` emite os dados novos sozinho.
- **Recomendações:** ao abrir um humor, o cache aparece na hora. Se estiver vazio ou com 24 horas ou mais, e houver conexão, as páginas 1 e 2 são buscadas e gravadas numa única transação ("tudo ou nada": uma falha nunca apaga o cache). Falha com cache: a lista continua e um Snackbar avisa. Sem cache e sem conexão: mensagem com "Tentar novamente". O pull-to-refresh ignora a validade do cache.
- **Detalhes, trailer e onde assistir:** buscados e gravados ao abrir o filme, com a mesma validade de 24 horas. Offline, os detalhes e os serviços continuam visíveis; o trailer e o botão do JustWatch só aparecem com conexão.
- **Lista "Quero assistir" e avaliações:** dados exclusivamente locais, sem servidor; portanto não há conflito de sincronização.
- **Indicador de conexão:** `ConnectivityManager` convertido em `Flow` (`callbackFlow`), considerando apenas redes validadas com acesso real à internet.
- **Banco:** versão 2, com migração (`MIGRATION_1_2`) que acrescenta "onde assistir" sem apagar a lista "Quero assistir".

## Configuração

### Requisitos

- Android Studio atualizado (o projeto usa Android Gradle Plugin 9.5).
- JDK: o embutido no Android Studio.
- Aparelho ou emulador com Android 8.0 (API 26) ou superior. Para testar a permissão de notificação, Android 13 ou superior.

### Chave da API do TMDB

O app precisa de uma credencial gratuita do TMDB. Ela **não** está no repositório.

1. Crie uma conta em [themoviedb.org](https://www.themoviedb.org/signup) e confirme o e-mail.
2. Acesse **Configurações → API** ([themoviedb.org/settings/api](https://www.themoviedb.org/settings/api)) e solicite uma chave do tipo *Developer*, descrevendo o uso como projeto acadêmico.
3. Copie o **API Read Access Token** (o texto longo, não a "API Key" curta).
4. Abra o arquivo `local.properties`, na raiz do projeto, e acrescente a linha:

   ```properties
   TMDB_API_KEY=cole_aqui_o_read_access_token
   ```

5. Sincronize o Gradle (**File → Sync Project with Gradle Files**).

O `local.properties` está no `.gitignore`, então a chave fica apenas na sua máquina. Durante o build, o Gradle a copia para a classe gerada `BuildConfig.TMDB_API_KEY`, e um `Interceptor` do OkHttp a envia no cabeçalho `Authorization: Bearer` (nunca na URL; no log de depuração o cabeçalho é ocultado). Sem a chave, o app compila, mas não consegue carregar filmes.

## Execução

Pelo Android Studio, selecione o módulo `app` e clique em **Run**. Pelo terminal:

```bash
./gradlew assembleDebug
```

O APK de depuração é gerado em `app/build/outputs/apk/debug/app-debug.apk`.

## Testes

Os testes unitários rodam na JVM, sem emulador:

```bash
./gradlew testDebugUnitTest
```

O relatório HTML fica em `app/build/reports/tests/testDebugUnitTest/index.html`.

**Última execução registrada:** 263 testes, 263 aprovados, em 36 classes.

| Camada | Classes | Testes | O que cobre |
|---|---|---|---|
| Domain | 16 | 115 | Mapeamento humor → busca; regras de recomendação (votos, assistidos, duplicados); validade de 24 h do cache; seleção de trailer; nota de 1 a 5; janela das 19h às 21h; regra de "em cartaz"; todos os casos de uso, com repositórios falsos (cache válido e expirado, offline, fallback do trailer para inglês, lista "Quero assistir", sorteio) |
| Data | 10 | 82 | Conversão de exceções em `AppError`; leitura do JSON do TMDB; mappers (pôster, ano, gêneros, onde assistir, estreia); conversores do Room; repositórios com API e DAO falsos (páginas 1 e 2, falha com e sem cache, transação "tudo ou nada"); token só no cabeçalho |
| Presentation | 8 | 60 | ViewModels com Turbine e `StandardTestDispatcher`: transições Loading → Success / Empty / Error, Snackbar, pull-to-refresh, filtros, trailer e botão do JustWatch só online, sorteio; mensagens de erro e "Atualizado há…" |
| Notification | 1 | 5 | Agendamento: 4 dias, atraso até as 19h, política `KEEP` (sem duplicar) |
| DI | 1 | 1 | O Koin constrói todos os casos de uso |

Os testes usam **fakes** (classes simples em memória) para repositórios, API e DAOs, e **relógio e sorteio com valores fixos**, para resultados reproduzíveis. Telas Compose, player do YouTube, Worker, permissão e o Room real não têm testes automatizados (testes instrumentados estão fora do escopo) e foram verificados manualmente.

## Evidências

- **Testes:** o histórico de cada execução, com data, comando, total, aprovados, falhas e correções aplicadas, está em [`TEST_RESULTS.md`](TEST_RESULTS.md). Ele registra também as falhas encontradas pelos testes durante o desenvolvimento e como foram corrigidas.
- **Capturas de tela:** as capturas do app online e offline serão reunidas na pasta `evidencias/` e referenciadas aqui na etapa final de validação (tarefa T28).

## Limitações conhecidas

- **Notificação sem horário exato:** o WorkManager não garante o horário; o Android pode atrasar a notificação para economizar bateria. Apenas a primeira execução mira a janela das 19h às 21h.
- **Pôsteres e logos offline:** dependem do cache de disco do Coil; imagens nunca carregadas com internet não aparecem offline (no lugar do pôster aparece um marcador de "pôster indisponível").
- **Trailer:** só com conexão. Alguns vídeos são bloqueados pelo YouTube para reprodução fora do site; nesses casos aparece o botão "Assistir no YouTube".
- **"Em cartaz nos cinemas" é uma estimativa:** o TMDB não informa salas nem quando o filme sai de cartaz; o selo aparece até 6 semanas após a estreia no Brasil.
- **Onde assistir:** apenas serviços do Brasil, conforme os dados do JustWatch no TMDB, que podem estar desatualizados. O TMDB não fornece o link direto de cada serviço, só a página com todos.
- **Dados só no aparelho:** não há login nem sincronização; desinstalar o app apaga a lista "Quero assistir" e as avaliações.
- **Fora do escopo:** busca livre por texto, contas do TMDB e remoção de filmes da lista "Quero assistir".

## Créditos

- **Fontes:** [Bricolage Grotesque](https://github.com/ateliertriay/bricolage) e [Nunito](https://github.com/googlefonts/nunito), distribuídas sob a SIL Open Font License 1.1 (textos das licenças em [`docs/licencas/`](docs/licencas/)). Ficam embutidas no app, sem download.
- **Dados de filmes:** [The Movie Database (TMDB)](https://www.themoviedb.org/). This product uses the TMDB API but is not endorsed or certified by TMDB.
- **Dados de onde assistir:** [JustWatch](https://www.justwatch.com/), via TMDB.
- **Ícones:** Material Icons (Google, Apache 2.0). Ícone do app (pipoca em forma de balão de fala) criado para o projeto, em [`icone/`](icone/).
- **Player do trailer:** [android-youtube-player](https://github.com/PierfrancescoSoffritti/android-youtube-player) (Apache 2.0).
