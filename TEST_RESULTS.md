# Resultados dos testes — KeveOQ

Registro das execuções dos testes unitários (JVM). Cada entrada reflete uma execução real.

- **Comando:** `./gradlew testDebugUnitTest`
- **Relatório HTML:** `app/build/reports/tests/testDebugUnitTest/index.html`

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
