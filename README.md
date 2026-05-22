# MovieFlux 🎬

MovieFlux é um aplicativo Android nativo que consome a API do [TheMovieDB (TMDB)](https://www.themoviedb.org/) para listar filmes populares, permitindo busca, visualização de detalhes e gerenciamento de favoritos com suporte offline. O projeto utiliza as tecnologias mais modernas do ecossistema Android, como **Jetpack Compose**, **Material 3** e **Modularização**.

---

## 🚀 Como configurar o projeto

Para rodar o projeto, você precisará de uma chave de API do TMDB.

1. Acesse o portal do desenvolvedor do [TMDB](https://www.themoviedb.org/settings/api) e gere sua chave.
2. No diretório raiz do projeto, localize ou crie o arquivo `local.properties`.
3. Adicione a seguinte linha ao arquivo:
   ```properties
   tmdb.api.key="SUA_CHAVE_AQUI"
   ```
4. Sincronize o projeto com o Gradle e execute no seu dispositivo ou emulador.

---

## 🔐 Fluxo de Biometria e Segurança

O aplicativo implementa um fluxo de autenticação robusto e seguro:

*   **Primeiro Acesso:** O usuário realiza o login mockado (**Usuário:** `admin` | **Senha:** `123456`). Após o sucesso, o app oferece a ativação da biometria.
*   **Oferta Inteligente:** Se o dispositivo suportar biometria mas não houver digitais cadastradas, o app oferece um atalho para as configurações do sistema.
*   **Segundo Acesso:** Com a biometria ativa, o usuário pode acessar o app instantaneamente sem digitar a senha.
*   **Recusa:** Se o usuário recusar a biometria, o app respeita a decisão e não pergunta novamente, a menos que uma nova biometria seja cadastrada no sistema.
*   **Segurança Avançada:** Todas as credenciais e preferências sensíveis são armazenadas utilizando **EncryptedSharedPreferences**, garantindo que os dados não fiquem expostos no armazenamento do aparelho.

---

## 🏗️ Arquitetura e Decisões Técnicas

O projeto foi construído utilizando **Clean Architecture** e **Modularização por Features**, visando escalabilidade e facilidade de manutenção.

### UI Moderna com Jetpack Compose:
O projeto foi totalmente migrado para **Jetpack Compose**, utilizando o **Material Design 3** para uma interface moderna, reativa e de alta performance.

### Estrutura de Módulos:
*   **`:app`**: Módulo orquestrador. Gerencia a navegação global e a composição final do grafo de dependências.
*   **`:core`**: Módulo agnóstico de infraestrutura. Contém utilitários de rede (Retrofit), segurança (Criptografia), Tematização (Compose Theme) e componentes de UI base.
*   **`:data:movies`**: Módulo de dados compartilhado. Gerencia o banco de dados **Room** e os repositórios de persistência de favoritos.
*   **`:feature:auth`**: Gerencia o login e a integração com a **AndroidX Biometric Library**.
*   **`:feature:movies`**: Responsável pela listagem de filmes populares (com Infinite Scroll) e detalhes do filme.
*   **`:feature:favorites`**: Aba dedicada para consulta offline de filmes favoritados.
*   **`:feature:search`**: Implementação da busca funcional com mapeamento de gêneros.

---

## 🧪 Estratégia de Testes

O projeto adota uma estratégia de testes rigorosa para garantir estabilidade:

*   **Testes Unitários:** ViewModels e Repositories são testados utilizando **MockK** e **Turbine** (para Flow).
*   **Testes de UI com Robolectric:** Utilizamos o **Robolectric** em conjunto com as APIs de teste do **Jetpack Compose** para rodar testes de interface diretamente na JVM, garantindo rapidez e confiabilidade sem a necessidade de um emulador para a maioria dos cenários de UI.
*   **Padrão Robot:** Implementação do padrão Robot para testes de UI, tornando-os mais legíveis e fáceis de manter.

---

## ♿ Acessibilidade

O MovieFlux foi desenvolvido seguindo as diretrizes de acessibilidade:

*   **Descrições Dinâmicas:** Uso de `contentDescription` em todos os elementos visuais.
*   **Feedback de Estado:** As ações de favoritos utilizam anúncios de acessibilidade para informar verbalmente o sucesso da operação.
*   **Contraste:** Paleta de cores **Teal Green** (#4DB6AC) testada para garantir legibilidade de acordo com os padrões WCAG.

---

## 🛠 Tech Stack

*   **Linguagem:** Kotlin
*   **UI:** Jetpack Compose + Material 3
*   **Arquitetura:** MVVM + Modularização (Clean)
*   **Injeção de Dependência:** Hilt
*   **Network:** Retrofit + OkHttp + Gson
*   **Persistência:** Room (Offline First)
*   **Segurança:** AndroidX Biometric + Security-Crypto
*   **Image Loading:** Coil

---

## 🤖 Uso de IA (Prompt Engineering)

Ferramentas de IA foram utilizadas para:
*   **Migração para Compose:** Otimização na criação de layouts reativos e componentes Material 3.
*   **Testes:** Implementação acelerada de cenários de teste com Robolectric e Compose Test Rule.
*   **Arquitetura:** Refatoração modular e implementação segura de criptografia.

---

## ✨ Funcionalidades Principais

✅ Autenticação biométrica completa com fallback seguro.
✅ UI 100% Jetpack Compose com Material 3.
✅ Mapeamento dinâmico de gêneros via API TMDB.
✅ Sincronização reativa de favoritos entre módulos.
✅ Suporte offline com banco de dados local.
✅ Paginação infinita e busca funcional.
✅ Testes de UI rápidos rodando na JVM com Robolectric.
