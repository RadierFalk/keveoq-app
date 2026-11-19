# Resultados dos testes — KeveOQ

Registro das execuções dos testes unitários (JVM). Cada entrada reflete uma execução real.

- **Comando:** `./gradlew testDebugUnitTest`
- **Relatório HTML:** `app/build/reports/tests/testDebugUnitTest/index.html`

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
