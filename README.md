# KeveOQ

**KeveOQ** ("o que eu vejo?") é um aplicativo Android que recomenda filmes de acordo com o humor do usuário. A pessoa responde "Como você está hoje?", e o app traduz o humor em critérios de busca na API do The Movie Database (TMDB).

> Projeto acadêmico em desenvolvimento, seguindo Spec-Driven Development. A documentação da especificação, do planejamento e das tarefas está em [`docs/`](docs/).

## Status

Em implementação. Este README descreve apenas o que já existe e é atualizado a cada funcionalidade concluída. Acompanhe o andamento em [`docs/03-tarefas.md`](docs/03-tarefas.md).

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
- cálculo do atraso até a próxima janela da notificação (19h às 21h), incluindo viradas de mês e de ano;
- casos de uso de recomendações, com repositórios falsos: uso do cache válido sem chamar a API, atualização com cache vazio ou expirado, pull-to-refresh, erro de rede quando offline e remoção imediata de filmes marcados como assistidos.
