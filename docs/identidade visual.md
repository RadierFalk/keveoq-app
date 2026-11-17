
/
Voce será um desenvolvedor android que irá me ajudar a criar um aplicativo. O contexto do aplicativo e a identidade visual já foi enviado para voce. Agora voce deve me orientar passo a passo detalhadamente cada etapa que devo seguir para poder criar esse aplicativo. Deve me orientar desde a preparação do ambiente até o final do projeto. Agora me explique o contexto do projeto que voce entendeu

Vou ler os arquivos de identidade visual do projeto antes de te explicar o que entendi.

Li o CLAUDE.md que você enviou e o arquivo identidade_visual.md do projeto. O HTML de identidade visual é um canvas empacotado (com as fontes embutidas), então tomei o .md como referência escrita. Segue o que entendi.

O que é o KeveOQ
É um aplicativo Android acadêmico que recomenda filmes conforme o humor do usuário. A pessoa escolhe como está se sentindo (Feliz, Triste, Ansioso etc.), o app converte esse humor em filtros de busca (gêneros incluídos, gêneros excluídos, data de lançamento) e consulta o endpoint discover/movie do TMDB. A partir das recomendações, o usuário vê detalhes, salva na lista “Quero assistir”, marca como assistido e dá uma nota de 1 a 5 estrelas. O requisito mais marcante é que as funções principais funcionem sem internet.

Como vamos trabalhar
O projeto segue Spec-Driven Development em cinco fases: Especificação → Planejamento → Decomposição → Implementação → Validação. Na prática, antes de qualquer linha de código de produção, vamos produzir documentos (a especificação, o plano técnico e a lista de tarefas em docs/03-tarefas.md), e você aprova cada um antes de avançarmos. Na implementação, faço uma tarefa por vez, na ordem do arquivo, explico o que vou mudar e por quê, espero sua confirmação e marco a tarefa como concluída no fim. Nada de bibliotecas ou telas fora da especificação sem perguntar.

Arquitetura
Clean Architecture num único módulo :app, separada por pacotes, com a regra de dependência presentation → domain ← data. O ponto central é o domain ser Kotlin puro: sem android.*, Retrofit, Room ou Koin. O motivo didático é que as regras de negócio (o mapeamento de humor, o mínimo de 100 votos, a validação da nota) podem ser testadas com JUnit rodando na JVM, sem emulador, e não dependem de nenhuma tecnologia que possa ser trocada. O data implementa as interfaces do domain e mantém DTOs e entidades Room escondidos lá dentro; o presentation só conversa com casos de uso; e o di (Koin) é o único pacote que enxerga tudo, porque é ele que “monta” as peças.

Regras de negócio que entendi
O mapeamento humor → filtros vira parâmetros da API: por exemplo, Ansioso gera with_genres=35|16|10751 e without_genres=27,53, e Nostálgico gera with_genres=12|10751 com primary_release_date.lte=1999-12-31. O detalhe do | é importante: com vírgula, o TMDB exigiria que o filme tivesse todos os gêneros ao mesmo tempo, o que esvaziaria os resultados. Toda busca leva vote_count.gte=100, e o resultado ainda passa por dois filtros locais: remover os já assistidos e remover duplicados (que podem surgir ao juntar páginas diferentes). Na lista salva, o status só avança de WANT_TO_WATCH para WATCHED, o mesmo filme não entra duas vezes e a nota só vale para filmes assistidos, entre 1 e 5.

Offline First
O Room é a única fonte de verdade: a tela observa um Flow do banco, e a rede nunca fala direto com a UI, ela apenas grava no Room. Ao abrir um humor, o app mostra o cache na hora; se o cache estiver vazio ou com mais de 24 h e houver conexão, busca na API e grava, e o Flow reemite sozinho. Se a requisição falhar mas houver cache, os dados continuam na tela com um aviso de erro. Isso combina com o banner “sem internet” e com as fontes embutidas em res/font/ (sem downloadable fonts, justamente por causa do modo offline).

Identidade visual
Tema “sofá e pipoca”, com laranja e azul-petróleo, claro e escuro seguindo o sistema, sem Material You. Cores só via MaterialTheme.colorScheme, tipografia só via MaterialTheme.typography (Bricolage Grotesque nos títulos, Nunito no corpo), formas de 12/20/28 dp, margem lateral de 20 dp, @PreviewLightDark em toda tela, e KeveOQBrand restrito a ilustrações. Os arquivos .ttf e o ícone do app serão fornecidos por você.

Pontos que precisam da sua decisão
Encontrei algumas lacunas e conflitos que afetam a especificação, então prefiro levantar agora em vez de supor:

O CLAUDE.md chegou cortado em “mostrar o erro de...”. Faltam o fim da regra offline e o que vier depois (critérios de testes, validação, commits?). Pode me enviar o restante?
Login/Cadastro com Firebase (item 7) não aparece na tabela de stack nem na árvore de pacotes. Preciso saber: o login é obrigatório para entrar no app? Será só e-mail/senha? A lista “Quero assistir” continua apenas local (Room) ou deve sincronizar por usuário na nuvem (o que exigiria Firestore e mudaria a ideia de “Room como única fonte de verdade”)?
Notificação (item 8): para agendar lembretes de forma confiável no Android, o caminho usual é o WorkManager, que também não está na stack. Além disso, no Android 13+ é preciso pedir a permissão POST_NOTIFICATIONS. Qual a frequência desejada (diária? após X dias sem abrir o app?) e posso incluir o WorkManager?
Trailer (item 9): não precisamos de outra API, porque o próprio TMDB tem GET movie/{movie_id}/videos, que devolve as chaves dos vídeos no YouTube. A dúvida é como exibir: abrir o app do YouTube/navegador por um Intent (zero bibliotecas novas) ou tocar dentro do app (exigiria uma biblioteca de player). E o trailer, naturalmente, não funcionará offline.
Nome do pacote: qual será o com.<seunome>.keveoq?
Sobre a ordem: a preparação do ambiente (instalar Android Studio, JDK, emulador, criar a conta e a chave do TMDB, configurar o Git) não é código de produção, então podemos fazê-la em paralelo à fase de Especificação, se você preferir.

Meu entendimento está correto, e como você quer resolver os pontos de 1 a 5 antes de começarmos?


# CLAUDE.md — KeveOQ ## Contexto do projeto Este repositório contém o **KeveOQ**, um aplicativo Android que recomenda filmes de acordo com o humor do usuário. É um trabalho acadêmico, e os critérios de avaliação estão refletidos nestas instruções. Siga-as em todas as sessões. O usuário esco

PASTED
1.Segue claude.md

Esqueça o login, coloque essa opção dentro da parte de nao aplicar no escopo
Pode incluir, a frequencia é a cada 4 dias na parte da noite
Adicione uma lib de Player, na versao offiline nao deixe o trailer disponivel
radierdev.keveoq
Recebi o CLAUDE.md completo. Ele fecha as lacunas que eu tinha apontado (o fim da regra offline, os padrões de código, testes, commits e entregáveis), mas gera um conflito com duas das suas respostas, que explico abaixo.

Decisões registradas
O login sai do escopo, o que já está de acordo com o novo CLAUDE.md. O pacote será radierdev.keveoq. As notificações (a cada 4 dias, à noite) e o trailer (com uma biblioteca de player, indisponível offline) entram no escopo.

O conflito com o CLAUDE.md
A seção “Escopo” do arquivo diz: “Fora do escopo: login, contas do TMDB, trailers, notificações e busca livre por texto.” Como o CLAUDE.md é a regra que vou seguir em todas as sessões (e reflete os critérios de avaliação), ele precisa ser atualizado para refletir sua decisão. Senão, numa sessão futura, eu mesmo teria de recusar as duas funcionalidades pela regra 5. As mudanças que proponho no texto são:

Escopo: incluir “7. Notificação periódica sugerindo o uso do app” e “8. Trailer do filme na tela de detalhes (somente online)”, e deixar em “Fora do escopo” apenas login, contas do TMDB e busca livre.
Stack: acrescentar WorkManager (agendamento) e a biblioteca de player do YouTube.
API: acrescentar o endpoint GET movie/{movie_id}/videos.
Offline First: acrescentar a regra “o trailer não é exibido sem conexão”.
Arquitetura: acrescentar um pacote para o agendamento da notificação (sugiro notification/, ao lado de core/).
Tem também um detalhe de nome: o CLAUDE.md aponta para docs/identidade-visual.md (com hífen), mas o arquivo no projeto se chama identidade_visual.md (com sublinhado). Quando criarmos o repositório, ele deve ir para docs/ com o nome do hífen.

Como cada nova funcionalidade funciona, e por quê
Notificação a cada 4 dias, à noite. O caminho recomendado pelo Android é o WorkManager com um PeriodicWorkRequest de 4 dias. Ele sobrevive a reinícios do aparelho e respeita a economia de bateria. A contrapartida, que você precisa saber para defender o trabalho, é que o WorkManager não garante horário exato: o sistema pode atrasar a execução para poupar bateria. Para cair “à noite”, calculamos um atraso inicial até o próximo horário noturno e usamos uma janela de flexibilidade, o que costuma bastar. A alternativa de horário exato seria o AlarmManager com alarmes exatos, mas desde o Android 12/14 isso exige uma permissão especial (SCHEDULE_EXACT_ALARM/USE_EXACT_ALARM) que o Google restringe a apps de despertador e calendário, ou seja, seria exagero aqui. Além disso, no Android 13+ o app precisa pedir a permissão POST_NOTIFICATIONS em tempo de execução, e o ícone da notificação precisa ser monocromático, que podemos importar pelo Vector Asset (a identidade visual permite isso e proíbe desenhar um ícone).

Trailer. Os trailers vêm do próprio TMDB, pelo movie/{id}/videos, que devolve uma lista de vídeos com a chave do YouTube; filtramos por site = YouTube e type = Trailer, dando preferência ao oficial. Um ponto técnico importante: players genéricos como o Media3/ExoPlayer não tocam YouTube, porque o YouTube não fornece o arquivo de vídeo direto, e extraí-lo viola os termos de uso. Por isso a biblioteca adequada é a android-youtube-player (de Pierfrancesco Soffritti), que embute o player oficial do YouTube (IFrame API) e pode ser usada no Compose via AndroidView. A versão exata eu confirmo na fase de Planejamento, porque não consigo consultar a internet daqui. Dois cuidados: com language=pt-BR, muitos filmes não têm trailer em português, então convém buscar em pt-BR e, se vier vazio, em inglês; e alguns donos de vídeo bloqueiam a reprodução fora do YouTube, então precisamos de um plano para esse caso.

O que preciso que você decida
Posso considerar aprovadas as mudanças 1 a 5 no CLAUDE.md? Se sim, te mostro aqui no chat o texto das seções alteradas para você conferir antes de aplicarmos no arquivo.
O pacote é literalmente radierdev.keveoq, ou com.radierdev.keveoq (o padrão com.<seunome>.keveoq do arquivo)? Os dois funcionam no Android; o segundo segue a convenção de domínio invertido.
Notificação: qual faixa de horário conta como “noite” (ex.: entre 19h e 21h)? Ela deve aparecer a cada 4 dias sempre, ou só se o usuário ficou 4 dias sem abrir o app? E em que momento pedimos a permissão de notificação (na primeira abertura ou após o usuário salvar o primeiro filme)?
Trailer: quando o vídeo não puder ser reproduzido dentro do app, posso oferecer um botão “Assistir no YouTube” que abre o app/navegador? E a chave do trailer deve ser guardada no Room junto com os detalhes do filme, ou buscada só na hora em que a tela abre (já que ele não aparece offline de qualquer forma)?
Assim que fecharmos esses pontos, o próximo passo é a preparação do ambiente (Android Studio, JDK, emulador, conta e chave do TMDB, Git), que pode correr em paralelo à fase de Especificação.

Sim, mas leve em consideração o primeiro escopo enviado, tire somente o login do escopo inicial
é com.radierdev.keveoq
entre 19h e 21h, sempre a cada 4 dias, Na primeira abertura do aplicativo
Sim, pode encaminhar para o youtube. Deixe guardando junto com os detalhes do filme.
Perfeito. Abaixo está o texto das seções alteradas do CLAUDE.md, para você conferir antes de aplicarmos no arquivo. Mantive o escopo original de 9 itens, removendo apenas o login, e deixei o restante do arquivo intacto. Depois do texto, explico as escolhas que não são óbvias.

Seções alteradas
1. Escopo (substitui a seção inteira)

markdown
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
2. Stack (duas linhas novas na tabela)

markdown
| Agendamento | WorkManager (`work-runtime-ktx`) |
| Player de trailer | android-youtube-player (`core`), usado no Compose via `AndroidView` |
3. API (acréscimos na lista de endpoints)

markdown
  - `GET movie/{movie_id}/videos`: trailer. Buscar com `language=pt-BR`; se não houver trailer, repetir com `language=en-US`.
- Link externo do trailer: `https://www.youtube.com/watch?v={key}`
4. Regras de negócio (acréscimos em “Demais regras”)

markdown
- Trailer: considerar apenas vídeos com `site = YouTube` e `type = Trailer`, priorizando `official = true`. Sem nenhum vídeo válido, o filme não tem trailer.
- Notificação: a cada 4 dias, com a primeira execução no próximo intervalo entre 19h e 21h. O cálculo do atraso inicial é uma regra do domain, testável com relógio fixo.
5. Nova seção “Notificações” (depois de “Offline First”)

markdown
## Notificações

- Agendada com WorkManager (`PeriodicWorkRequest` de 4 dias) e `ExistingPeriodicWorkPolicy.KEEP`, para não duplicar o agendamento a cada abertura do app.
- O WorkManager não garante horário exato: a notificação pode atrasar por economia de bateria (documentar como limitação).
- No Android 13+, a permissão `POST_NOTIFICATIONS` é pedida na primeira abertura. Se negada, o app funciona normalmente, sem notificações.
- Tocar na notificação abre a tela de seleção de humor.
- Textos da notificação em português. Ícone monocromático importado pelo Vector Asset.
6. Offline First (acréscimos)

markdown
- A chave do trailer é gravada em `movie_details` junto com os detalhes, mas o trailer só é exibido com conexão; offline, a seção não aparece.
- Se o player incorporado não conseguir reproduzir o vídeo, exibir o botão "Assistir no YouTube", que abre o app do YouTube ou o navegador.
7. Arquitetura (pacote raiz e nova pasta na árvore)

markdown
com.radierdev.keveoq/
├── ...
├── notification/          # Worker, canal de notificação e agendamento (WorkManager)
8. Testes (acréscimos na cobertura mínima)

markdown
  - regra de seleção do trailer;
  - cálculo do atraso até a próxima janela de 19h às 21h.
Por que essas escolhas
ExistingPeriodicWorkPolicy.KEEP. O agendamento será feito toda vez que o app abrir, para garantir que ele exista. Sem essa política, cada abertura criaria um novo ciclo e o usuário receberia várias notificações. Com KEEP, o WorkManager vê que já existe um trabalho com aquele nome e mantém o ciclo original.

Cálculo do horário no domain. “A cada 4 dias, entre 19h e 21h” é uma regra de negócio, e a parte que calcula o atraso (“agora são 14h, faltam 5h para as 19h”) é Kotlin puro. Colocá-la no domain, recebendo a hora atual como parâmetro, permite testar casos como “abriu às 22h, deve agendar para amanhã às 19h” sem esperar o relógio real. O Worker, que depende do Android, fica no pacote notification/ e só executa.

Chave do trailer guardada em movie_details. O trailer em si não fica disponível offline, mas guardar a chave evita uma nova requisição quando o usuário reabre o filme com internet, e mantém coerência com a regra de que o Room é a fonte de verdade. Na Planejamento, vale avaliarmos o parâmetro append_to_response=videos do TMDB, que traz detalhes e vídeos numa só requisição.

Identidade visual do trailer. O identidade_visual.md não define a área do player. Pela regra 9, não vou inventar nada: na fase de Especificação, proponho usar apenas papéis já existentes (por exemplo, o botão “Assistir no YouTube” como OutlinedButton) e você confirma.

Próximo passo
Se aprovar este texto, a sequência é: você atualiza o CLAUDE.md (ou me autoriza a te entregar a versão completa num arquivo), e então começamos a preparação do ambiente: instalação do Android Studio, verificação do JDK, criação do emulador, obtenção da chave do TMDB e configuração do Git e do repositório. Vou te guiar em cada etapa, explicando o que cada uma faz.

O texto está aprovado como está, ou quer ajustar alguma seção?

Ok, poderia me enviar o arquivo claude.md completo de agora?

O CLAUDE.md completo está pronto. Ele contém todas as alterações que você aprovou e, para manter o arquivo coerente, fiz dois pequenos acréscimos além do texto que te mostrei: o parágrafo de “Contexto do projeto” agora menciona o trailer e a notificação, e a lista de camadas em “Arquitetura” ganhou uma linha explicando o pacote notification (que usa o domain para calcular o horário, mas não contém regra de negócio). Se preferir sem esses acréscimos, é só falar.

Quando criarmos o repositório, esse arquivo fica na raiz, e o identidade_visual.md vai para docs/identidade-visual.md.

Podemos começar a preparação do ambiente? O primeiro passo é verificar o que você já tem instalado: qual sistema operacional você usa (Windows, macOS ou Linux) e se já tem o Android Studio instalado?

CLAUDE
Documento
·MD
me guie passo a passo agora como vou fazer, ja instalei o android studio no pc, quais os proximos passos ?

Ótimo, com o Android Studio instalado, a preparação do ambiente tem seis etapas. Faça na ordem e me conte o resultado de cada uma (ou se travar em alguma), porque algumas telas variam conforme a versão e o sistema operacional.

Etapa 1 — Atualizar o Android Studio e conferir o JDK
Abra o Android Studio e vá em Help → Check for Updates (no macOS, Android Studio → Check for Updates). Se houver versão estável nova, atualize. Isso importa porque o CLAUDE.md exige “as versões estáveis mais recentes e compatíveis entre si”, e versões recentes do Android Gradle Plugin exigem um Android Studio recente.

Sobre o JDK: você não precisa instalar Java separadamente. O Android Studio já traz o seu próprio JDK (o JetBrains Runtime), e o Gradle usa ele por padrão. Quando criarmos o projeto, vamos conferir em Settings → Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK que a opção selecionada é a embutida.

Etapa 2 — Instalar os componentes do SDK
Vá em Settings → Languages & Frameworks → Android SDK (ou no ícone de SDK Manager na tela inicial). Na aba SDK Platforms, marque a versão estável mais recente do Android. Na aba SDK Tools, confirme que estão instalados: Android SDK Build-Tools, Android SDK Platform-Tools e Android Emulator. Clique em Apply e aguarde o download.

O SDK Platform é o “manual” de cada versão do Android que o compilador consulta; o Platform-Tools traz o adb, que é a ferramenta que conversa com o celular ou emulador; e o Emulator é o programa que roda o celular virtual.

Etapa 3 — Criar o emulador
Vá em Device Manager → Create Virtual Device, escolha um Pixel e, na imagem do sistema, escolha uma versão recente que tenha Google Play (o ícone da sacolinha aparece ao lado). Duas razões para essa escolha no nosso projeto: precisamos de Android 13 ou superior para testar o pedido de permissão de notificação, e precisamos da Google Play para que o botão “Assistir no YouTube” encontre o app do YouTube.

Depois de criar, clique no ▶ para ligar o emulador. Se ele ficar muito lento ou reclamar de aceleração, a virtualização do processador provavelmente está desligada: no Windows, ela se ativa na BIOS (opção “Intel VT-x” ou “AMD-V/SVM”) e no recurso “Windows Hypervisor Platform”; no macOS e Linux costuma funcionar direto. Se o seu PC não aguentar o emulador, um celular Android real com Depuração USB ativada (em Opções do desenvolvedor) serve perfeitamente.

Etapa 4 — Instalar e configurar o Git
Abra o terminal (no Windows, o “Git Bash” que vem com o Git, ou o terminal do próprio Android Studio) e rode:

bash
git --version
Se aparecer um número de versão, o Git já está instalado. Se não, baixe em git-scm.com. Depois, configure sua identidade, que vai aparecer em cada commit:

bash
git config --global user.name "Seu Nome"
git config --global user.email "seu-email@exemplo.com"
git config --global init.defaultBranch main
A terceira linha faz o repositório começar no ramo main, que é o padrão atual do GitHub. Se o seu professor pede o código no GitHub, crie também uma conta lá (ou confirme que você já tem uma).

Etapa 5 — Criar a conta e a chave do TMDB
Crie uma conta gratuita em themoviedb.org, confirme o e-mail, e depois vá em Configurações da conta → API e solicite uma chave do tipo Developer. No formulário, descreva o uso como projeto acadêmico, não comercial.

Ao final, o TMDB mostra duas credenciais: a API Key (um código curto, da versão 3 da API, enviado como parâmetro api_key na URL) e o API Read Access Token (um texto longo, enviado no cabeçalho Authorization: Bearer). As duas dão acesso aos mesmos endpoints que vamos usar. Qual delas o Interceptor vai usar é uma decisão que tomaremos na fase de Planejamento, com prós e contras explicados. Por enquanto, guarde as duas num lugar seguro.

Um cuidado importante: nunca cole a chave aqui no chat, nem em documentos ou commits. Ela vai existir apenas no local.properties da sua máquina.

Etapa 6 — Baixar as fontes
A identidade visual exige que você adicione as fontes manualmente. Em fonts.google.com, baixe as famílias Bricolage Grotesque e Nunito. Cada download vem num .zip com uma pasta static/: use os arquivos dessa pasta (fontes com um peso fixo cada), e não o arquivo “variable” da raiz. Separe estes pesos e renomeie assim:

Arquivo original (aproximado)	Novo nome
BricolageGrotesque-Bold.ttf	bricolage_grotesque_bold.ttf
BricolageGrotesque-ExtraBold.ttf	bricolage_grotesque_extrabold.ttf
Nunito-Regular.ttf	nunito_regular.ttf
Nunito-Bold.ttf	nunito_bold.ttf
Nunito-ExtraBold.ttf	nunito_extrabold.ttf
A renomeação não é capricho: o Android só aceita nomes de recurso com letras minúsculas, números e sublinhado, e o Type.kt procura exatamente esses nomes. Guarde também o arquivo OFL.txt de cada família, porque ele irá para docs/licencas/. Por enquanto, deixe tudo numa pasta separada; os arquivos entram no projeto só na fase de Implementação.

E depois?
Com o ambiente pronto, o passo seguinte é criar a pasta do repositório com o CLAUDE.md e o docs/identidade-visual.md, e então iniciar a fase de Especificação. Repare que ainda não vamos criar o projeto Android no Android Studio: pela regra 1 do SDD, isso vira a primeira tarefa da Implementação, depois de especificação, planejamento e tarefas aprovados.

Para eu te orientar com precisão nas próximas etapas: qual sistema operacional você usa, e você vai trabalhar comigo por este chat ou pelo Claude Code dentro da pasta do projeto (que é onde o CLAUDE.md é lido automaticamente)?

Estou no Windows 11, vou trabalhar com voce pelo claude code dentro do android studio

Consultar informações atualizadas sobre instalação do Claude Code no Windows e integração com JetBrains
Ótimo, isso define bem o fluxo. Uma coisa importante para entender desde já: o Claude Code no Android Studio é uma sessão separada desta conversa. Ele não verá nada do que discutimos aqui; o que ele vai ler automaticamente é o CLAUDE.md na raiz da pasta. Por isso foi tão importante deixar todas as decisões registradas nesse arquivo.

Seguem os ajustes específicos do Windows e os próximos passos.

Ajustes do Windows nas etapas anteriores
Emulador (Etapa 3). Antes de criar o emulador, confira se a virtualização está ligada: abra o Gerenciador de Tarefas → Desempenho → CPU e veja se “Virtualização” aparece como Habilitado. Se estiver desabilitado, é preciso ativar na BIOS do computador (a opção costuma se chamar “Intel VT-x”, “Intel Virtualization Technology” ou “SVM Mode” na AMD). Depois, procure no menu Iniciar por “Ativar ou desativar recursos do Windows”, marque Plataforma do Hipervisor do Windows e reinicie. É esse recurso que permite ao emulador usar o processador diretamente, em vez de simular cada instrução, o que faz a diferença entre um emulador fluido e um inutilizável.

Git (Etapa 4). Ao instalar o Git for Windows (git-scm.com), mantenha as opções padrão do instalador; ele traz o Git Bash, que o Claude Code também usa no Windows. Além das três configurações que te passei, rode estas duas:

bash
git config --global core.autocrlf true
git config --global core.longpaths true
A primeira resolve uma diferença histórica: o Windows termina as linhas de texto com dois caracteres (CRLF) e Linux/macOS com um (LF). Com autocrlf, o Git converte automaticamente, evitando que todo arquivo apareça como “alterado” sem motivo. A segunda libera caminhos de arquivo com mais de 260 caracteres, um limite antigo do Windows que projetos Android, com pastas muito aninhadas, às vezes ultrapassam.

Etapa 7 — Criar a pasta do repositório
Crie a pasta em C:\dev\KeveOQ. A escolha do local tem motivo: caminhos com espaços, acentos (como “Área de Trabalho”) ou dentro do OneDrive causam erros estranhos no Gradle, e o OneDrive ainda tenta sincronizar milhares de arquivos de build. Dentro dela, monte esta estrutura:

C:\dev\KeveOQ\
├── CLAUDE.md                  ← o arquivo que te enviei
└── docs\
    └── identidade-visual.md   ← o seu identidade_visual.md, renomeado com hífen
Depois, abra o Git Bash dentro da pasta (clique com o botão direito nela → “Open Git Bash here”) e rode:

bash
git init
git add CLAUDE.md docs/identidade-visual.md
git commit -m "docs: adiciona instrucoes do projeto e identidade visual"
O git init transforma a pasta num repositório, o git add escolhe quais arquivos entram no próximo “retrato” do projeto, e o git commit grava esse retrato no histórico, já no padrão Conventional Commits que o CLAUDE.md exige.

Repare que ainda não criamos o projeto Android. Decisões como versão mínima do Android suportada (minSdk) pertencem à fase de Planejamento, e a criação do projeto será a primeira tarefa da Implementação.

Etapa 8 — Instalar o Claude Code e o plugin do Android Studio
Instale o Claude Code seguindo o guia oficial para Windows, em https://docs.claude.com/en/docs/claude-code/overview. Prefiro te mandar para a documentação em vez de passar o comando de memória, porque o método de instalação muda com alguma frequência e não consigo conferir a versão atual daqui. Depois de instalado, rode claude num terminal para fazer o login com sua conta.

Em seguida, no Android Studio, vá em Settings → Plugins → Marketplace, procure por “Claude Code”, instale o plugin oficial da Anthropic e reinicie. O plugin conecta o Claude Code ao editor, o que permite, por exemplo, que ele mostre as alterações propostas na tela de comparação (diff) do Android Studio, para você aprovar com calma.

Etapa 9 — Primeira sessão no Claude Code
No Android Studio, use File → Open e selecione a pasta C:\dev\KeveOQ. Abra a aba Terminal (na parte de baixo) e digite claude. Uma boa primeira mensagem para ele seria:

Leia o CLAUDE.md e o docs/identidade-visual.md e me explique o que entendeu do projeto. Não crie nem altere arquivos ainda.

Assim você confere se ele entendeu as regras antes de começar a Especificação. Um alerta: não rode o comando /init. Ele serve para gerar um CLAUDE.md a partir do código existente, e poderia sobrescrever ou bagunçar o arquivo que construímos com tanto cuidado.

Quando terminar essas etapas, me conte como foi. Uma última decisão para fechar o ambiente: o trabalho precisa ser entregue num repositório no GitHub (ou outra plataforma)? Se sim, te mostro como conectar a pasta ao GitHub antes de começarmos a Especificação.

Eu ja tenho um repositorio com um programa em branco(Empty Activity) e um repositorio criado, mas so fiz criar o projeto mesmo e fiz commit dele no github, quero usar o claude code no terminal (CLI) poderia me orientar como seguir ?

Planning the setup steps and checks for the project
Ótimo, isso adianta parte do caminho. Como o projeto já existe, em vez de criar uma pasta nova vamos verificar e ajustar o que você já tem. Não tem problema o projeto ter sido criado antes da Especificação: o template “Empty Activity” é só um esqueleto gerado pelo Android Studio, e ninguém escreveu código de produção ainda. Na fase de Planejamento, vamos apenas registrar que o ponto de partida é esse template.

Use o Git Bash para os comandos abaixo, porque ele aceita a mesma sintaxe que o Claude Code usa no Windows.

Etapa 1 — Conferir onde o projeto está
Veja em qual pasta o projeto está no seu PC. O padrão do Android Studio é C:\Users\<seu usuário>\AndroidStudioProjects\KeveOQ. Isso funciona bem, desde que o caminho não tenha espaços, acentos nem esteja dentro do OneDrive (se seu nome de usuário do Windows for “João Silva”, por exemplo, teria os dois problemas). Se for o caso, me avise que te explico como mover o projeto com segurança.

Abra o Git Bash dentro da pasta do projeto (botão direito → “Open Git Bash here”) e rode:

bash
git status
git branch
O git status deve dizer que não há nada pendente (“nothing to commit, working tree clean”), e o git branch mostra o nome do ramo atual (provavelmente main).

Etapa 2 — Verificação de segurança do local.properties
Esta é a mais importante. Rode:

bash
git ls-files | grep local.properties
O comando lista todos os arquivos que o Git está rastreando e filtra pelo nome. O resultado correto é não aparecer nada. Se aparecer local.properties, ele foi enviado ao GitHub, e precisamos corrigir antes de você colocar a chave do TMDB nele (por enquanto ele só tem o caminho do SDK, então não há vazamento ainda). Confira também que o arquivo .gitignore na raiz do projeto contém a linha /local.properties; o template costuma criá-la.

Etapa 3 — Conferir o pacote e o build
Abra o arquivo app/build.gradle.kts e procure duas linhas: namespace = "..." e applicationId = "...". As duas devem ser com.radierdev.keveoq. Se estiver diferente, me avise: renomear pacote envolve mais de um arquivo, e prefiro que isso vire uma tarefa explicada passo a passo.

Aproveite e confira se existe o arquivo gradle/libs.versions.toml. Os templates recentes já criam o projeto com Version Catalog e Kotlin DSL, como o CLAUDE.md exige.

Depois, teste se o projeto compila:

bash
./gradlew assembleDebug
A primeira execução demora, porque o Gradle baixa dependências. O resultado esperado é BUILD SUCCESSFUL. O ./gradlew é o “Gradle Wrapper”: um script que baixa e usa a versão exata do Gradle definida no projeto, garantindo que o build funcione igual em qualquer máquina, inclusive na do seu professor.

Etapa 4 — Adicionar o CLAUDE.md e a identidade visual
Coloque o CLAUDE.md na raiz do projeto (ao lado de settings.gradle.kts) e crie a pasta docs com o arquivo de identidade renomeado:

KeveOQ\
├── CLAUDE.md
├── docs\
│   └── identidade-visual.md
├── app\
├── gradle\
└── ...
Depois, registre e envie ao GitHub:

bash
git add CLAUDE.md docs/identidade-visual.md
git commit -m "docs: adiciona instrucoes do projeto e identidade visual"
git push
O git push envia o commit local para o repositório remoto no GitHub.

Etapa 5 — Instalar e abrir o Claude Code no terminal
Instale o Claude Code seguindo o guia oficial para Windows: https://docs.claude.com/en/docs/claude-code/overview (te mando para a documentação porque o método de instalação muda com frequência e não consigo conferir a versão atual daqui). Em seguida, no terminal, entre na pasta do projeto e inicie:

bash
cd ~/AndroidStudioProjects/KeveOQ
claude
Na primeira vez, ele pede o login com sua conta. Ele sempre trabalha na pasta onde foi aberto e lê o CLAUDE.md dali automaticamente, por isso é essencial abrir dentro da raiz do projeto. Você pode deixar o Android Studio aberto ao mesmo tempo: o Claude Code altera os arquivos e o Android Studio mostra as mudanças.

Etapa 6 — Primeira conversa com o Claude Code
Comece com uma mensagem como esta:

Leia o CLAUDE.md e o docs/identidade-visual.md. O projeto foi criado a partir do template Empty Activity e ainda não tem código de produção. Me explique o que entendeu e quais divergências encontrou entre o template e o CLAUDE.md. Não crie nem altere arquivos ainda.

Isso faz ele identificar sozinho o que precisa de ajuste (por exemplo, o tema em ui/theme/ que deve ir para presentation/theme/), e você confere o entendimento antes da Especificação. Dois lembretes: não rode /init, que poderia sobrescrever o CLAUDE.md, e, sempre que o Claude Code pedir permissão para editar um arquivo ou rodar um comando, leia o que ele vai fazer antes de aceitar.

Faça as etapas 1 a 3 primeiro e me conte o resultado: o namespace e o applicationId estão como com.radierdev.keveoq, e o local.properties ficou de fora do Git?





Claude é uma IA e pode cometer erros. Confira sempre as respostas.
Artefatos
CLAUDE
Documento
·MD
Conteúdo do projeto
App Android
Criado por você

identidade visual.md
14,7 kB

MD

KeveOQ — Identidade Visual.html
2,1 MB

HTML
Claude terminou a resposta
identidade visual.md


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
package com.seunome.keveoq.presentation.theme

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
package com.seunome.keveoq.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.seunome.keveoq.R

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
package com.seunome.keveoq.presentation.theme

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
