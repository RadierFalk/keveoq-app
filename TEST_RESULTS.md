# Resultados dos testes — KeveOQ

Registro das execuções dos testes unitários (JVM). Cada entrada reflete uma execução real.

- **Comando:** `./gradlew testDebugUnitTest`
- **Relatório HTML:** `app/build/reports/tests/testDebugUnitTest/index.html`

---

## 2026-11-19 14:52 — T20: tela de seleção de humor

| Item | Valor |
|---|---|
| Comando | `./gradlew testDebugUnitTest` |
| Total | 188 |
| Aprovados | 188 |
| Falhas | 0 |
| Ignorados | 0 |
| Correções aplicadas | Nenhuma |

**Classe nova:**

| Classe de teste | Testes | Cobre |
|---|---|---|
| `MoodViewModelTest` | 5 | 8 humores sem seleção inicial (RF01); "Ver filmes" desabilitado até escolher (RF02); seleção guardada e trocada; nome e dica distintos para cada humor |

**Verificação manual:** app instalado em aparelho físico (tema escuro): título, grade com os 8 cards, ícones "Quero assistir" e "Sobre" na barra superior e botão "Ver filmes" desabilitado exibidos corretamente; nenhum erro do app no Logcat.

---

## 2026-11-19 14:47 — T19: navegação e componentes base

| Item | Valor |
|---|---|
| Comando | `./gradlew testDebugUnitTest` |
| Total | 183 |
| Aprovados | 183 |
| Falhas | 0 |
| Ignorados | 0 |
| Correções aplicadas | Opt-in de `ExperimentalCoroutinesApi` na `MainDispatcherRule`, para remover dois avisos de compilação (sem mudança de comportamento) |

**Classes novas (presentation):**

| Classe de teste | Testes | Cobre |
|---|---|---|
| `ErrorMessageMapperTest` | 5 | `Network`, 401, 500/503, outros HTTP, `Serialization` e `Unknown` → mensagem em português, sem termos técnicos (CA13) |
| `UpdatedAtFormatterTest` | 8 | Faixas de "Atualizado há…": 59 s, 1 min, 59 min, 3 h (CA32), 23 h 59 min, 24 h, 3 dias e data futura |
| `KeveOQAppViewModelTest` | 2 | Começa online; online → offline → online, para o banner (RF13, CA11) |

---

## 2026-11-19 14:40 — T18: repositório da lista "Quero assistir" e injeção de dependências (camada Data completa)

| Item | Valor |
|---|---|
| Comando | `./gradlew testDebugUnitTest` |
| Total | 168 |
| Aprovados | 168 |
| Falhas | 0 |
| Ignorados | 0 |
| Correções aplicadas | Nenhuma |

**Classes novas:**

| Classe de teste | Testes | Cobre |
|---|---|---|
| `SavedMovieRepositoryImplTest` | 9 | Salvar e ler; filme não salvo; duplicado vira falha sem exceção (RN06); marcar como assistido; gravar e trocar a nota (CA24); ordem da lista; IDs assistidos em tempo real; falha do banco em cada escrita |
| `DomainModuleTest` | 1 | O Koin constrói os 12 casos de uso do `domainModule` |

**Resumo por camada após esta execução:**

| Camada | Classes de teste | Testes |
|---|---|---|
| Domain (regras e casos de uso) | 14 | 101 |
| Data (API, banco, mappers, repositórios) | 9 | 66 |
| DI (Koin) | 1 | 1 |
| **Total** | **24** | **168** |

---

## 2026-11-19 14:39 — T17: repositório de filmes

| Item | Valor |
|---|---|
| Comando | `./gradlew testDebugUnitTest` |
| Total | 158 |
| Aprovados | 158 |
| Falhas | 0 |
| Ignorados | 0 |
| Correções aplicadas | Nenhuma |

**Classes novas** (com `TmdbApi` e `MovieDao` falsos, sem rede nem banco reais):

| Classe de teste | Testes | Cobre |
|---|---|---|
| `MovieRepositoryImplTest` | 16 | Páginas 1 e 2 consultadas e gravadas em ordem (RN11, CA33); critérios enviados à API; duplicados entre páginas; substituição do cache; humores independentes; **falha de rede com cache preserva o cache** (CA09); **falha sem cache** mantém cache vazio (CA10); falha na página 2 não grava nada; falha do banco vira `Unknown`; **cache válido não chama a API** e **cache expirado chama e atualiza a data** (caso de uso + repositório, CA06, CA07); detalhes com data da busca; erro 404; vídeos por idioma; detalhes gravados e observados |
| `SafeDbCallTest` | 3 | Sucesso; falha do banco → `Unknown`; `CancellationException` relançada |

---

## 2026-11-19 14:37 — T16: banco de dados local (Room)

| Item | Valor |
|---|---|
| Comando | `./gradlew testDebugUnitTest` |
| Total | 139 |
| Aprovados | 139 |
| Falhas | 0 |
| Ignorados | 0 |
| Correções aplicadas | Nenhuma |

**Classes novas:**

| Classe de teste | Testes | Cobre |
|---|---|---|
| `ConvertersTest` | 4 | Gêneros gravados e lidos como JSON; lista vazia; acentos e aspas |
| `LocalMappersTest` | 6 | DTO → entidade; recomendações com posição e data; linhas do banco → recomendações ordenadas; cache vazio sem data; detalhes e filme salvo vão e voltam sem perda |

O KSP gerou `AppDatabase_Impl`, `MovieDao_Impl` e `SavedMovieDao_Impl` sem erros. O banco real (Room em aparelho) não é testado automaticamente: testes instrumentados estão fora do escopo.

---

## 2026-11-19 14:28 — T15: cliente da API do TMDB

| Item | Valor |
|---|---|
| Comando | `./gradlew testDebugUnitTest` |
| Total | 129 |
| Aprovados | 129 |
| Falhas | 0 |
| Ignorados | 0 |
| Correções aplicadas | Nenhuma |

**Classes novas (camada data):**

| Classe de teste | Testes | Cobre |
|---|---|---|
| `SafeApiCallTest` | 9 | Sucesso; `IOException` e timeout → `Network`; 401, 404 e 500 → `Http(code)`; `SerializationException` → `Serialization`; outra exceção → `Unknown`; `CancellationException` relançada |
| `RemoteMappersTest` | 14 | URL do pôster e pôster ausente; ano a partir da data e data vazia; filme, detalhes (gêneros, sinopse e duração ausentes) e vídeo; `with_genres` com `\|`, `without_genres` com `,` ou nulo; data ISO do Nostálgico; mínimo de votos |
| `TmdbJsonParsingTest` | 3 | Leitura de JSON no formato do TMDB com campos desconhecidos e valores nulos (discover, detalhes e vídeos) |
| `AuthInterceptorTest` | 2 | Token no cabeçalho `Authorization: Bearer`; token fora da URL |

Nenhum teste faz requisição real à API.

---

## 2026-11-19 14:21 — Correção: falhas do banco como `AppResult` na lista "Quero assistir"

| Item | Valor |
|---|---|
| Comando | `./gradlew testDebugUnitTest` |
| Total | 101 |
| Aprovados | 101 |
| Falhas | 0 |
| Ignorados | 0 |
| Correções aplicadas | Refatoração do `SavedMovieRepository` e do `saveMovieDetails` para devolver `AppResult` (decisão 12 do planejamento); nenhum teste falhou |

**Testes novos:**

| Classe de teste | Novos | Cobre |
|---|---|---|
| `WatchlistUseCasesTest` | +6 (total 23) | Falha de leitura e de gravação ao adicionar, marcar como assistido e avaliar: o caso de uso devolve `Failure` e nada é alterado |
| `RefreshMovieDetailsUseCaseTest` | +1 (total 10) | Falha ao gravar os detalhes é repassada |

---

## 2026-11-19 14:17 — T14: caso de uso do atraso da notificação (domain completo)

| Item | Valor |
|---|---|
| Comando | `./gradlew testDebugUnitTest` |
| Total | 94 |
| Aprovados | 94 |
| Falhas | 0 |
| Ignorados | 0 |
| Correções aplicadas | Nenhuma |

**Classe nova:**

| Classe de teste | Testes | Cobre |
|---|---|---|
| `CalculateNotificationDelayUseCaseTest` | 4 | Relógio fixo no fuso de São Paulo: 14h (5 h), 20h (imediato), 22h (21 h); horário local do fuso, e não UTC (RN10, CA29) |

**Resumo do domain após esta execução:**

| Classe de teste | Testes |
|---|---|
| `MoodCriteriaMapperTest` | 14 |
| `RecommendationRulesTest` | 6 |
| `CachePolicyTest` | 6 |
| `TrailerSelectorTest` | 8 |
| `RatingRulesTest` | 5 |
| `NotificationWindowTest` | 8 |
| `GetMoodsUseCaseTest` | 1 |
| `ObserveRecommendationsUseCaseTest` | 5 |
| `RefreshRecommendationsUseCaseTest` | 9 |
| `ObserveConnectivityUseCaseTest` | 1 |
| `ObserveMovieDetailsUseCaseTest` | 1 |
| `RefreshMovieDetailsUseCaseTest` | 9 |
| `WatchlistUseCasesTest` | 17 |
| `CalculateNotificationDelayUseCaseTest` | 4 |
| **Total** | **94** |

---

## 2026-11-19 14:16 — T13: casos de uso da lista "Quero assistir"

| Item | Valor |
|---|---|
| Comando | `./gradlew testDebugUnitTest` |
| Total | 90 |
| Aprovados | 90 |
| Falhas | 0 |
| Ignorados | 0 |
| Correções aplicadas | Nenhuma |

**Classe nova:**

| Classe de teste | Testes | Cobre |
|---|---|---|
| `WatchlistUseCasesTest` | 17 | Adicionar como `WANT_TO_WATCH`; `AlreadyInList` (RN06, CA20), inclusive para filme já assistido; marcar como assistido, `AlreadyWatched` e `NotInList` (RN07, CA22); avaliar, trocar a nota, `NotWatched` para filme não assistido ou fora da lista (CA23) e `InvalidRating` para 0 e 6 (RN08, CA24); acompanhamento do status; filtros todos, quero assistir e assistidos (CA21); lista vazia |

As classes anteriores foram executadas novamente, sem falhas.

---

## 2026-11-19 14:15 — T12: casos de uso de detalhes e trailer

| Item | Valor |
|---|---|
| Comando | `./gradlew testDebugUnitTest` |
| Total | 73 |
| Aprovados | 73 |
| Falhas | 0 |
| Ignorados | 0 |
| Correções aplicadas | Nenhuma |

**Classes novas:**

| Classe de teste | Testes | Cobre |
|---|---|---|
| `ObserveMovieDetailsUseCaseTest` | 1 | Emite nulo sem cache e depois os detalhes gravados |
| `RefreshMovieDetailsUseCaseTest` | 9 | Trailer em pt-BR; fallback para en-US (CA16); sem trailer válido (CA17); cache de detalhes válido não chama a API; expirado busca de novo; offline sem cache devolve `Network`; offline com cache; falha nos detalhes ou nos vídeos não grava nada |

As classes anteriores foram executadas novamente, sem falhas.

---

## 2026-11-19 14:14 — T11: casos de uso de recomendações

| Item | Valor |
|---|---|
| Comando | `./gradlew testDebugUnitTest` |
| Total | 63 |
| Aprovados | 63 |
| Falhas | 0 |
| Ignorados | 0 |
| Correções aplicadas | Nenhuma |

**Classes novas** (com repositórios e monitor de conexão falsos):

| Classe de teste | Testes | Cobre |
|---|---|---|
| `GetMoodsUseCaseTest` | 1 | Os 8 humores na ordem da tela (RF01) |
| `ObserveRecommendationsUseCaseTest` | 5 | Cache com data; sem cache; regras aplicadas; filme some ao ser marcado como assistido, em tempo real (RN03, CA04); filme só salvo continua |
| `RefreshRecommendationsUseCaseTest` | 9 | Cache válido não chama a API (CA06); expirado e vazio chamam (CA07); `force` ignora a validade (CA08); offline devolve `Network` sem chamar a API; offline com cache válido; `force` offline; falha da API repassada; critérios do humor |
| `ObserveConnectivityUseCaseTest` | 1 | Emissões online → offline → online (RF13) |

As classes anteriores foram executadas novamente, sem falhas.

---

## 2026-11-19 14:11 — T10: janela da notificação

| Item | Valor |
|---|---|
| Comando | `./gradlew testDebugUnitTest` |
| Total | 47 |
| Aprovados | 47 |
| Falhas | 0 |
| Ignorados | 0 |
| Correções aplicadas | Nenhuma |

**Classe nova:**

| Classe de teste | Testes | Cobre |
|---|---|---|
| `NotificationWindowTest` | 8 | 14h (5 h), 19h e 20h59 (imediato), 21h e 22h (dia seguinte), meia-noite, virada de mês e de ano (RN10, CA29) |

As demais classes (`MoodCriteriaMapperTest`, `RecommendationRulesTest`, `CachePolicyTest`, `TrailerSelectorTest`, `RatingRulesTest`) foram executadas novamente, sem falhas.

---

## 2026-11-19 14:03 — T09: seleção de trailer e validação de nota

| Item | Valor |
|---|---|
| Comando | `./gradlew testDebugUnitTest` |
| Total | 39 |
| Aprovados | 39 |
| Falhas | 0 |
| Ignorados | 0 |
| Correções aplicadas | Nenhuma |

**Classes testadas:**

| Classe de teste | Testes | Cobre |
|---|---|---|
| `MoodCriteriaMapperTest` | 14 | Gêneros dos 8 humores; Ansioso exclui 27 e 53 (CA02); Nostálgico até 31/12/1999 (CA03); mínimo de 100 votos (RN02) |
| `RecommendationRulesTest` | 6 | Mínimo de votos (RN02); assistidos (RN03, CA04); duplicados (RN04, CA05); ordem preservada; listas vazias |
| `CachePolicyTest` | 6 | Sem data, agora, 23 h 59 min, 24 h, 25 h e data no futuro (RN05, CA06, CA07) |
| `TrailerSelectorTest` | 8 | Oficial primeiro (CA15); não oficial como alternativa; ignora teaser, outro site e chave em branco; sem vídeo válido e lista vazia (CA17); primeiro entre vários oficiais |
| `RatingRulesTest` | 5 | Notas 0, 1, 5, 6 e negativa (RN08, CA24) |

---

## 2026-11-19 13:56 — T08: regras de recomendação e validade do cache

| Item | Valor |
|---|---|
| Comando | `./gradlew testDebugUnitTest` |
| Total | 26 |
| Aprovados | 26 |
| Falhas | 0 |
| Ignorados | 0 |
| Correções aplicadas | Nenhuma |

**Classes testadas:**

| Classe de teste | Testes | Cobre |
|---|---|---|
| `MoodCriteriaMapperTest` | 14 | Gêneros dos 8 humores; Ansioso exclui 27 e 53 (CA02); Nostálgico até 31/12/1999 (CA03); mínimo de 100 votos (RN02) |
| `RecommendationRulesTest` | 6 | Mínimo de votos com limite 99/100 (RN02); remoção de assistidos (RN03, CA04); duplicados mantendo a 1ª ocorrência (RN04, CA05); ordem preservada; listas vazias |
| `CachePolicyTest` | 6 | Relógio fixo: sem data, agora, 23 h 59 min (válido, CA06), exatamente 24 h e 25 h (expirado, CA07), data no futuro (expirado) |

---

## 2026-11-19 13:51 — T07: mapeamento humor → critérios

| Item | Valor |
|---|---|
| Comando | `./gradlew testDebugUnitTest` |
| Total | 14 |
| Aprovados | 14 |
| Falhas | 0 |
| Ignorados | 0 |
| Correções aplicadas | Nenhuma |

**Classes testadas:**

| Classe de teste | Testes | Cobre |
|---|---|---|
| `MoodCriteriaMapperTest` | 14 | Gêneros dos 8 humores; Ansioso exclui 27 e 53 (CA02); Nostálgico até 31/12/1999 (CA03); mínimo de 100 votos em todos os humores (RN02); humores sem filtro extra |
