# MovieFlux 🎬

MovieFlux é um aplicativo Android nativo que consome a API do [TheMovieDB (TMDB)](https://www.themoviedb.org/) para listar filmes populares, permitindo busca, visualização de detalhes e gerenciamento de favoritos com suporte offline. O projeto foca em padrões de arquitetura modernos, segurança avançada e alta performance.

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

### Estrutura de Módulos:
*   **`:app`**: Módulo orquestrador. Gerencia a navegação global (DeepLinks) e a composição final do grafo de dependências.
*   **`:core`**: Módulo agnóstico de infraestrutura. Contém utilitários de rede (Retrofit), segurança (Criptografia) e componentes de UI base.
*   **`:data:movies`**: Módulo de dados compartilhado. Resolve o problema de sincronização entre as features de Filmes e Favoritos. Contém o banco de dados **Room** e os repositórios de persistência.
*   **`:feature:auth`**: Gerencia o login e a integração com a AndroidX Biometric Library.
*   **`:feature:movies`**: Responsável pela listagem de filmes populares (com Infinite Scroll) e detalhes do filme.
*   **`:feature:favorites`**: Aba dedicada para consulta offline de filmes favoritados.
*   **`:feature:search`**: Implementação da busca funcional.

### Decisões de Design:
*   **Comunicação entre Features:** As features são 100% isoladas. Elas não se conhecem e se comunicam exclusivamente através de **DeepLinks**.
*   **Sincronização Reativa:** Utilizamos **Flow** e **Coroutines** para que a interface reaja instantaneamente a mudanças no banco de dados (ex: favoritar um filme nos detalhes atualiza a estrela na Home na mesma hora).
*   **Design System:** Uso consistente de **Teal Green** (#4DB6AC) e componentes do Material Design 3.

---

## ♿ Acessibilidade

O MovieFlux foi desenvolvido seguindo as diretrizes de acessibilidade para garantir uma experiência inclusiva:

*   **Descrições Dinâmicas:** Todos os posters e elementos visuais possuem `contentDescription` contextualizados (ex: *"Poster do filme [Título]"*).
*   **Feedback de Estado:** As ações de favoritos informam verbalmente via TalkBack se o item está sendo adicionado ou removido, citando o nome do filme.
*   **Navegação Intuitiva:** Botões de retorno e itens de menu possuem etiquetas claras, facilitando a navegação por gestos ou teclado.
*   **Informativos de Carregamento:** O usuário é notificado sobre o progresso de operações (carregamento de lista, busca ou validação de login) através de anúncios de acessibilidade nas barras de progresso.
*   **Contraste e Identidade Visual:** A paleta de cores (Teal Green) foi selecionada e testada para garantir níveis de contraste que atendam aos padrões WCAG, assegurando legibilidade para usuários com baixa visão ou daltonismo, utilizando componentes do Material Design 3.

---

## 🛠 Tech Stack

*   **Linguagem:** Kotlin
*   **Arquitetura:** MVVM + Modularização (Clean)
*   **Injeção de Dependência:** Hilt
*   **Network:** Retrofit + OkHttp + Gson
*   **Persistência:** Room (Offline First)
*   **Segurança:** AndroidX Biometric + Security-Crypto
*   **Image Loading:** Glide

---

## 🤖 Uso de IA (Prompt Engineering)

Durante o desenvolvimento, ferramentas de IA foram utilizadas para otimizar:
*   **Refatoração Modular:** Auxílio na extração de lógica duplicada para o módulo `:data:movies`.
*   **Criptografia:** Implementação rápida do boilerplate de `EncryptedSharedPreferences`.
*   **UX de Biometria:** Sugestões de estados para lidar com a recusa e fallback da autenticação biométrica.

---

## ✨ Funcionalidades Principais

✅ Autenticação biométrica completa com fallback seguro.
✅ Mapeamento dinâmico de gêneros via API TMDB.
✅ Sincronização reativa de favoritos entre módulos independentes.
✅ Suporte offline (Offline First) com banco de dados local.
✅ Paginação infinita e busca funcional por títulos.
✅ Tratamento robusto de estados da UI (Loading, Erro, Empty State).
