# 01 — Especificação do KeveOQ

> Fase 1 do Spec-Driven Development. Este documento descreve **o que** o app faz, sem decidir **como** (isso fica em `02-planejamento.md`).

## 1. Proposta

O **KeveOQ** ("o que eu vejo?") é um aplicativo Android que recomenda filmes de acordo com o humor do usuário. Em vez de digitar uma busca ou rolar catálogos intermináveis, a pessoa responde a uma pergunta simples, "Como você está hoje?", e recebe uma lista de filmes compatíveis com aquele estado de espírito. Os dados de filmes vêm da API pública do The Movie Database (TMDB).

## 2. Problema que resolve

Escolher um filme costuma levar mais tempo do que assisti-lo: os catálogos são enormes, organizados por gênero, e o usuário nem sempre sabe que gênero quer. O KeveOQ inverte a pergunta: parte de algo que o usuário sabe responder (como se sente) e traduz isso em critérios de busca. Além disso, guarda os filmes que a pessoa quer ver e os que já viu, e funciona mesmo sem internet com o que já foi carregado.

## 3. Objetivos

1. Recomendar filmes a partir de 8 humores pré-definidos.
2. Permitir montar uma lista pessoal "Quero assistir", marcar filmes como assistidos e avaliá-los.
3. Manter as funcionalidades principais disponíveis sem conexão (Offline First).
4. Incentivar o uso recorrente com uma notificação periódica.
5. Servir como projeto acadêmico demonstrando Clean Architecture, Jetpack Compose, persistência local, consumo de API REST e testes unitários.

## 4. Público-alvo

Pessoas que assistem filmes em casa e querem uma sugestão rápida, sem precisar conhecer gêneros ou pesquisar títulos. Usuários de smartphones Android, falantes de português do Brasil. Não é necessário ter conta em nenhum serviço.

## 5. Funcionalidades (visão geral)

| # | Funcionalidade |
|---|---|
| F1 | Seleção de humor (tela inicial) |
| F2 | Recomendações do humor escolhido, com pull-to-refresh |
| F3 | Detalhes do filme (pôster, título, sinopse, nota, ano, duração, gêneros) |
| F4 | Lista "Quero assistir", com filtro por status, marcação como assistido e avaliação |
| F5 | Indicador de modo offline |
| F6 | Tela "Sobre" com a atribuição ao TMDB |
| F7 | Notificação periódica sugerindo o uso do app |
| F8 | Trailer do filme na tela de detalhes (somente com conexão) |

**Fora do escopo:** login e cadastro, contas do TMDB, busca livre por texto e remoção de filmes da lista "Quero assistir".

## 6. Requisitos funcionais (RF)

| ID | Requisito | Func. |
|---|---|---|
| RF01 | A tela inicial deve exibir a pergunta "Como você está hoje?" e os 8 humores (Feliz, Triste, Animado, Ansioso, Entediado, Romântico, Nostálgico, Corajoso) em cards selecionáveis. | F1 |
| RF02 | Ao selecionar um humor e confirmar no botão "Ver filmes", o app deve abrir a tela de recomendações daquele humor. | F1 |
| RF03 | A tela de recomendações deve listar filmes do humor escolhido com pôster, título, ano e nota, aplicando as regras RN01 a RN05 e RN11, e informar há quanto tempo os dados foram atualizados ("Atualizado há…"). | F2 |
| RF04 | A tela de recomendações deve exibir imediatamente os dados em cache e atualizá-los pela API quando o cache estiver vazio ou expirado e houver conexão. | F2 |
| RF05 | O usuário deve poder forçar a atualização das recomendações com o gesto pull-to-refresh, ignorando a validade do cache. | F2 |
| RF06 | Ao tocar em um filme, o app deve abrir a tela de detalhes com pôster, título, sinopse, nota, ano, duração e gêneros. | F3 |
| RF07 | Os detalhes de um filme devem ser guardados localmente quando a tela for aberta ou quando o filme for salvo, para consulta offline. | F3 |
| RF08 | Na tela de detalhes, o usuário deve poder adicionar o filme à lista "Quero assistir" (status `WANT_TO_WATCH`), respeitando RN06. | F4 |
| RF09 | A tela "Quero assistir" deve listar os filmes salvos e permitir filtrar por status: todos, "Quero assistir" e "Assistidos". | F4 |
| RF10 | O usuário deve poder marcar um filme salvo como assistido (`WANT_TO_WATCH` → `WATCHED`), respeitando RN07. | F4 |
| RF11 | O usuário deve poder avaliar com 1 a 5 estrelas um filme assistido e alterar essa avaliação depois, respeitando RN08. | F4 |
| RF12 | Cada item da lista "Quero assistir" deve exibir pôster, título, ano, status e, se houver, a avaliação; tocar no item abre os detalhes do filme. | F4 |
| RF13 | O app deve exibir um banner "sem internet" enquanto o aparelho estiver sem conexão, e escondê-lo quando a conexão voltar. | F5 |
| RF14 | Se a atualização falhar e houver cache, os dados devem permanecer na tela e o erro deve ser informado de forma não bloqueante (Snackbar). | F2, F5 |
| RF15 | Se não houver cache nem conexão, a tela deve exibir uma mensagem de erro com o botão "Tentar novamente". | F2, F5 |
| RF16 | A tela "Sobre" deve exibir o texto "This product uses the TMDB API but is not endorsed or certified by TMDB." | F6 |
| RF17 | O app deve agendar uma notificação sugerindo seu uso, conforme RN10, em português. | F7 |
| RF18 | Tocar na notificação deve abrir a tela de seleção de humor. | F7 |
| RF19 | No Android 13 ou superior, o app deve pedir a permissão de notificações na primeira abertura; se negada, o app funciona normalmente, sem notificações. | F7 |
| RF20 | Com conexão, a tela de detalhes deve exibir o trailer do filme em um player incorporado, selecionado conforme RN09. Sem trailer válido, a seção não aparece. | F8 |
| RF21 | Sem conexão, a seção de trailer não deve aparecer. | F8 |
| RF22 | Se o player incorporado não conseguir reproduzir o vídeo, o app deve exibir o botão "Assistir no YouTube", que abre o app do YouTube ou o navegador. | F8 |
| RF23 | Toda tela deve representar os estados de carregamento, conteúdo, vazio e erro, com mensagens em português, amigáveis e sem detalhes técnicos. | Todas |
| RF24 | A barra superior da tela inicial deve ter botões de ícone que abrem as telas "Quero assistir" e "Sobre". | F1, F4, F6 |

## 7. Requisitos não funcionais (RNF)

| ID | Categoria | Requisito |
|---|---|---|
| RNF01 | Disponibilidade offline | O banco local (Room) é a única fonte de verdade: a interface observa o banco e a rede apenas o atualiza. Seleção de humor, recomendações já carregadas, detalhes já abertos, lista "Quero assistir" e avaliações funcionam sem internet. |
| RNF02 | Arquitetura | Clean Architecture em um módulo `:app`, com a regra de dependência `presentation → domain ← data`. A camada domain é Kotlin puro. |
| RNF03 | Tecnologia | Usar exclusivamente a stack definida no `CLAUDE.md` (Kotlin, Compose + Material 3, Koin, Coroutines/Flow, Retrofit, Room, Coil, WorkManager, android-youtube-player). |
| RNF04 | Segurança | A chave do TMDB fica apenas em `local.properties` (fora do Git) e nunca aparece em código, commits ou documentação. |
| RNF05 | Privacidade | A lista "Quero assistir" e as avaliações são dados exclusivamente locais e não são enviados a nenhum servidor. |
| RNF06 | Desempenho | Recomendações em cache valem 24 h, evitando requisições repetidas. Pôsteres usam cache em disco. Listas evitam recomposições desnecessárias. |
| RNF07 | Usabilidade e identidade visual | Toda interface segue `docs/identidade-visual.md`: temas claro e escuro conforme o sistema, sem cor dinâmica, alvos de toque de no mínimo 48 dp e descrição de acessibilidade em português nos botões com ícone. |
| RNF08 | Idioma | Interface, notificação e mensagens em português do Brasil. Dados de filmes buscados em `pt-BR`. |
| RNF09 | Confiabilidade | Erros de rede, HTTP e de leitura dos dados não derrubam o app; são convertidos em mensagens ao usuário. |
| RNF10 | Testabilidade | Regras de negócio, casos de uso, repositórios e ViewModels são cobertos por testes unitários executados na JVM (`./gradlew testDebugUnitTest`). |
| RNF11 | Manutenibilidade | Código em inglês, documentação em português, commits pequenos no padrão Conventional Commits. |
| RNF12 | Conformidade | Atribuição ao TMDB na tela "Sobre"; licença OFL das fontes em `docs/licencas/` e citada no README. |

### Limitações conhecidas (aceitas nesta especificação)

- Pôsteres nunca carregados com internet não aparecem offline; no lugar, é exibido um marcador de "pôster indisponível".
- O WorkManager não garante horário exato: a notificação pode atrasar por economia de bateria.
- O trailer depende de conexão e do dono do vídeo permitir reprodução fora do YouTube.

## 8. Regras de negócio (RN)

**RN01 — Mapeamento humor → critérios de busca.** Entre os gêneros de um humor vale o operador OU.

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

As recomendações são ordenadas por popularidade (decrescente).

**RN02 — Mínimo de votos.** Recomendar apenas filmes com pelo menos 100 votos.

**RN03 — Remover assistidos.** Filmes com status `WATCHED` não aparecem nas recomendações.

**RN04 — Sem duplicados.** Um mesmo filme não aparece mais de uma vez na lista de recomendações.

**RN05 — Validade do cache.** As recomendações de cada humor valem 24 horas a partir da última atualização bem-sucedida. O pull-to-refresh ignora essa validade.

**RN06 — Sem duplicidade na lista.** Um filme não pode ser adicionado duas vezes à lista "Quero assistir".

**RN07 — Ciclo de status.** O status de um filme salvo só avança de `WANT_TO_WATCH` para `WATCHED`.

**RN08 — Avaliação.** A avaliação pessoal (inteiro de 1 a 5 estrelas) só é permitida para filmes `WATCHED` e pode ser alterada a qualquer momento, sempre dentro do intervalo. Valores fora do intervalo são rejeitados.

**RN09 — Seleção do trailer.** Considerar apenas vídeos com `site = YouTube` e `type = Trailer`, dando prioridade aos marcados como oficiais. A busca é feita primeiro em `pt-BR` e, se não houver trailer válido, repetida em `en-US`. Sem nenhum vídeo válido, o filme não tem trailer.

**RN10 — Agenda da notificação.** A notificação se repete a cada 4 dias. A primeira execução ocorre no próximo intervalo entre 19h e 21h a partir do momento do agendamento (se ainda for antes das 19h, no mesmo dia; se for depois das 21h, no dia seguinte). Abrir o app de novo não duplica o agendamento.

**RN11 — Volume por atualização.** Cada atualização das recomendações de um humor busca as 2 primeiras páginas da API (até 40 filmes) antes de aplicar RN02 a RN04.

## 9. Critérios de aceite

Cada critério está no formato *Dado / Quando / Então* e indica os requisitos que verifica.

**Seleção de humor e recomendações**
- **CA01** (RF01, RF02) Dado que o app foi aberto, quando a tela inicial aparece, então os 8 humores são exibidos e, ao escolher "Triste" e tocar em "Ver filmes", abre-se a tela de recomendações de "Triste".
- **CA02** (RF03, RN01, RN02) Dado o humor "Ansioso", quando as recomendações são buscadas, então a consulta pede os gêneros 35, 16 ou 10751, exclui 27 e 53 e exige pelo menos 100 votos.
- **CA03** (RN01) Dado o humor "Nostálgico", quando as recomendações são buscadas, então só são pedidos filmes lançados até 31/12/1999.
- **CA04** (RN03) Dado um filme marcado como assistido, quando as recomendações de qualquer humor são exibidas, então esse filme não aparece.
- **CA05** (RN04) Dado que a API devolve o mesmo filme duas vezes, quando a lista é exibida, então ele aparece uma única vez.
- **CA06** (RF04, RN05) Dado um cache de "Feliz" atualizado há menos de 24 h, quando a tela é aberta, então os filmes aparecem sem nova requisição à API.
- **CA07** (RF04, RN05) Dado um cache com mais de 24 h e conexão disponível, quando a tela é aberta, então o cache é exibido imediatamente e substituído pelos dados novos ao fim da requisição.
- **CA08** (RF05) Dado um cache válido e conexão, quando o usuário faz pull-to-refresh, então uma nova requisição é feita.
- **CA32** (RF03) Dado um cache atualizado há 3 horas, quando a tela de recomendações é aberta, então ela informa há quanto tempo os dados foram atualizados (o texto exato é definido no planejamento).
- **CA33** (RN11) Dado uma atualização com conexão, quando a busca é feita, então as páginas 1 e 2 da API são consultadas.

**Offline e erros**
- **CA09** (RF14) Dado um cache existente e falha na requisição, quando a atualização termina, então os filmes continuam na tela e um Snackbar informa o problema.
- **CA10** (RF15) Dado que não há cache nem conexão, quando a tela de recomendações é aberta, então aparece a mensagem de erro com o botão "Tentar novamente", e tocar nele refaz a tentativa.
- **CA11** (RF13) Dado que o aparelho perde a conexão, quando qualquer tela está aberta, então o banner "sem internet" aparece, e some quando a conexão volta.
- **CA12** (RF07, RNF01) Dado um filme cujos detalhes já foram abertos com internet, quando ele é aberto de novo offline, então os detalhes são exibidos.
- **CA13** (RF23) Dado qualquer tela, quando ocorre um erro, então a mensagem está em português e não exibe códigos ou termos técnicos.

**Detalhes e trailer**
- **CA14** (RF06) Dado um filme da lista, quando o usuário toca nele, então a tela de detalhes mostra pôster, título, sinopse, nota, ano, duração e gêneros.
- **CA15** (RF20, RN09) Dado um filme com vídeos do YouTube, quando os detalhes são abertos com conexão, então é exibido um trailer do tipo "Trailer", com prioridade ao oficial.
- **CA16** (RN09) Dado um filme sem trailer em português mas com trailer em inglês, quando os detalhes são abertos, então o trailer em inglês é exibido.
- **CA17** (RF20, RN09) Dado um filme sem nenhum vídeo válido, quando os detalhes são abertos, então a seção de trailer não aparece.
- **CA18** (RF21) Dado que o aparelho está offline, quando os detalhes são abertos, então a seção de trailer não aparece.
- **CA19** (RF22) Dado um trailer que não pode ser reproduzido no app, quando o erro do player ocorre, então aparece o botão "Assistir no YouTube", que abre o vídeo no app do YouTube ou no navegador.

**Lista "Quero assistir"**
- **CA20** (RF08, RN06) Dado um filme não salvo, quando o usuário o adiciona à lista, então ele aparece com status "Quero assistir"; tentar adicioná-lo novamente não cria um segundo item.
- **CA21** (RF09) Dado filmes salvos com os dois status, quando o usuário escolhe um filtro, então só aparecem os filmes daquele status.
- **CA22** (RF10, RN07) Dado um filme "Quero assistir", quando é marcado como assistido, então passa a "Assistido"; não há ação para voltar ao status anterior.
- **CA23** (RN08) Dado um filme "Quero assistir", quando se tenta avaliá-lo, então a avaliação é rejeitada.
- **CA24** (RF11, RN08) Dado um filme assistido, quando o usuário dá 4 estrelas, então a nota é salva; trocá-la depois para 2 estrelas substitui a anterior; valores como 0 ou 6 são rejeitados.
- **CA25** (RNF01) Dado que o aparelho está offline, quando a lista é aberta, então todos os filmes salvos e suas avaliações aparecem.
- **CA26** (RF23) Dado que a lista está vazia, quando a tela é aberta, então aparece uma mensagem explicando e uma ação para escolher um humor.

**Sobre e notificação**
- **CA27** (RF16) Dado a tela "Sobre", quando aberta, então exibe exatamente o texto de atribuição ao TMDB.
- **CA28** (RF19) Dado um Android 13+ na primeira abertura, quando o app inicia, então pede a permissão de notificações; se negada, o app continua funcionando.
- **CA29** (RN10) Dado o agendamento às 14h, então a primeira execução é prevista para as 19h do mesmo dia; às 22h, para as 19h do dia seguinte; entre 19h e 21h, dentro da janela atual.
- **CA30** (RN10) Dado que o app é aberto várias vezes, quando o agendamento é feito, então existe apenas um agendamento ativo.
- **CA31** (RF17, RF18) Dado que a notificação foi exibida, quando o usuário toca nela, então o app abre na tela de seleção de humor.
- **CA34** (RF24) Dado a tela inicial, quando o usuário toca nos ícones da barra superior, então abrem-se as telas "Quero assistir" e "Sobre".

## 10. Decisões registradas

Pontos que o `CLAUDE.md` não definia, decididos durante a aprovação desta especificação:

1. **Remover filme da lista "Quero assistir":** fora do escopo.
2. **Alterar a avaliação:** permitido (RF11, RN08).
3. **Volume de recomendações:** 2 páginas por atualização, até 40 filmes (RN11).
4. **Acesso a "Quero assistir" e "Sobre":** botões de ícone na barra superior da tela inicial (RF24).
5. **"Atualizado há…":** exibido na tela de recomendações (RF03).
