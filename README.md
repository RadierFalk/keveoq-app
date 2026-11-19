# KeveOQ

**KeveOQ** ("o que eu vejo?") é um aplicativo Android que recomenda filmes de acordo com o humor do usuário. A pessoa responde "Como você está hoje?", e o app traduz o humor em critérios de busca na API do The Movie Database (TMDB).

> Projeto acadêmico em desenvolvimento, seguindo Spec-Driven Development. A documentação da especificação, do planejamento e das tarefas está em [`docs/`](docs/).

## Status

Em implementação. Este README descreve apenas o que já existe e é atualizado a cada funcionalidade concluída. Acompanhe o andamento em [`docs/03-tarefas.md`](docs/03-tarefas.md).

## Funcionalidades implementadas

- **Seleção de humor (tela inicial):** a pergunta "Como você está hoje?" e oito humores em cards com uma dica de gênero. O botão "Ver filmes" é habilitado ao escolher um humor, e a escolha é mantida ao girar o aparelho. A barra superior tem atalhos para "Quero assistir" e "Sobre".
- **Sorteio de filme ("Me surpreenda"):** para quem não quer escolher um humor, o botão sorteia um humor, pega as recomendações dele (salvas ou da internet) e abre os detalhes de um filme aleatório que ainda não foi assistido. Sem internet, sorteia entre os humores que já têm filmes salvos; se não houver nenhum, avisa.
- **Recomendações por humor:** lista de filmes do TMDB (pôster, título, ano e nota) para o humor escolhido, sem filmes já assistidos nem repetidos, com "Atualizado há…". Os dados ficam salvos no aparelho e valem 24 horas; puxar a lista para baixo força uma nova busca. Sem internet, a lista salva continua aparecendo e um aviso informa a falha; sem lista salva e sem internet, aparece uma mensagem com "Tentar novamente".
- **Detalhes do filme:** pôster, título, ano, duração, nota, gêneros e sinopse. Os detalhes ficam salvos no aparelho ao serem abertos, para consulta sem internet. Conforme a situação do filme, a tela oferece "Quero assistir", "Já assisti" ou as estrelas para avaliar (1 a 5, podendo trocar a nota).
- **Onde assistir:** nos detalhes, os serviços do Brasil onde o filme está disponível, agrupados em "Incluso na assinatura", "Alugar" e "Comprar", com logo e nome; o selo "Em cartaz nos cinemas" para filmes que estrearam no Brasil há até 6 semanas (estimativa, porque o TMDB não informa salas); e o botão "Ver opções no JustWatch", que abre a página com os links de cada serviço. Os serviços ficam salvos e aparecem offline; o botão só aparece com internet. Dados de disponibilidade: JustWatch.
- **Trailer:** com internet, os detalhes mostram o trailer do filme num player do YouTube incorporado (prioridade ao trailer oficial, em português ou, se não houver, em inglês). Sem internet ou sem trailer, a seção não aparece. Se o vídeo não puder ser reproduzido no app, aparece o botão "Assistir no YouTube".
- **Lista "Quero assistir":** filmes salvos, do mais recente para o mais antigo, com filtro (Todos, Quero assistir, Assistidos), botão "Já assisti" e estrelas para avaliar os assistidos. Funciona sem internet, porque fica só no aparelho. Lista vazia mostra o botão "Escolher um humor".
- **Tela "Sobre":** nome e versão do app, descrição, atribuição ao TMDB ("This product uses the TMDB API but is not endorsed or certified by TMDB.") e créditos das fontes.
- **Notificação periódica:** a cada 4 dias, a notificação "Que tal um filme hoje?" sugere usar o app; a primeira é agendada para a próxima janela entre 19h e 21h. Tocar nela abre a seleção de humor. No Android 13 ou superior, a permissão é pedida uma única vez, na primeira abertura; se for negada, o app funciona normalmente, sem notificações. O horário não é exato: o Android pode atrasar a notificação para economizar bateria.
- **Indicador de modo offline:** um banner "Sem internet. Mostrando o que já está salvo." aparece no topo de todas as telas enquanto o aparelho está sem conexão e some quando ela volta.
- **Base de dados e rede prontas:** recomendações por humor, detalhes, trailer e lista "Quero assistir" já funcionam nas camadas de domínio e dados (com testes), aguardando as telas.

## Configuração

### Requisitos

- Android Studio atualizado (o projeto usa Android Gradle Plugin 9.5).
- JDK: o embutido no Android Studio.
- Aparelho ou emulador com Android 8.0 (API 26) ou superior. Para testar a notificação, Android 13 ou superior.

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

O `local.properties` está no `.gitignore`, então a chave fica apenas na sua máquina. Durante o build, o Gradle a copia para a classe gerada `BuildConfig.TMDB_API_KEY`, que também não é versionada. Sem a chave, o app compila normalmente, mas não consegue carregar filmes.

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

O relatório HTML fica em `app/build/reports/tests/testDebugUnitTest/index.html`, e o histórico das execuções está em [`TEST_RESULTS.md`](TEST_RESULTS.md).

Cobertura atual:

- mapeamento de cada humor para os critérios de busca do TMDB (gêneros, exclusões, data limite e mínimo de 100 votos);
- regras de recomendação: mínimo de votos, remoção de filmes assistidos e de duplicados, mantendo a ordem;
- validade de 24 horas do cache de recomendações, testada com relógio fixo;
- seleção do trailer (somente YouTube do tipo "Trailer", com prioridade ao oficial);
- validação da avaliação pessoal (1 a 5 estrelas);
- cálculo do atraso até a próxima janela da notificação (19h às 21h), incluindo viradas de mês e de ano e o uso do fuso horário local;
- casos de uso de recomendações, com repositórios falsos: uso do cache válido sem chamar a API, atualização com cache vazio ou expirado, pull-to-refresh, erro de rede quando offline e remoção imediata de filmes marcados como assistidos;
- casos de uso de detalhes: validade de 24 horas, trailer em português com alternativa em inglês e atualização "tudo ou nada" em caso de falha;
- casos de uso da lista "Quero assistir": adicionar sem duplicar, marcar como assistido, avaliar (só assistidos, de 1 a 5) e filtrar por status;
- cliente da API do TMDB: conversão de cada tipo de falha de rede em erro do app, leitura do JSON da API, conversão para os modelos do app e envio do token apenas no cabeçalho;
- banco local: gravação dos gêneros como JSON e conversão entre as tabelas e os modelos do app;
- repositório de filmes, com API e banco falsos: busca das páginas 1 e 2, cache válido e expirado, falha de rede com e sem cache (o cache nunca é apagado por uma falha) e gravação dos detalhes;
- repositório da lista "Quero assistir": salvar sem duplicar, marcar como assistido, avaliar e tratar falhas do banco sem encerrar o app;
- injeção de dependências: o Koin consegue construir todos os casos de uso;
- apresentação: mensagens de erro em português, texto "Atualizado há…" e estado de conexão usado pelo banner "sem internet";
- tela de humor: lista dos humores, seleção e habilitação do botão "Ver filmes";
- tela de recomendações: carregando, lista, vazio, erro com "Tentar novamente", aviso de falha com dados salvos e pull-to-refresh;
- tela de detalhes: carregando, conteúdo, erro, consulta offline, salvar, marcar como assistido e avaliar;
- trailer: exibido só com conexão e com trailer válido, escondido ao perder a conexão;
- lista "Quero assistir": vazio, conteúdo, filtros, marcar como assistido e avaliar;
- tela "Sobre": versão e texto exato de atribuição ao TMDB;
- agendamento da notificação: intervalo de 4 dias, primeira execução na janela das 19h às 21h e política que evita agendamentos duplicados;
- sorteio de filme: humor aleatório, sem filmes assistidos, funcionamento offline com cache e aviso quando não há filme para sortear;
- botão "Me surpreenda": abre o filme sorteado, avisa quando não há filme e ignora toques repetidos durante o sorteio;
- onde assistir: leitura dos serviços do Brasil (assinatura, aluguel e compra) e da estreia nos cinemas, regra de "em cartaz" (até 42 dias após a estreia) e gravação no banco;
- seção "Onde assistir": serviços exibidos também offline, botão do JustWatch só com conexão e selo de cinema dentro e fora do prazo.
