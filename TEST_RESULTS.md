# Resultados dos testes — KeveOQ

Registro das execuções dos testes unitários (JVM). Cada entrada reflete uma execução real.

- **Comando:** `./gradlew testDebugUnitTest`
- **Relatório HTML:** `app/build/reports/tests/testDebugUnitTest/index.html`

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
