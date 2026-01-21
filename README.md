# CatImageList

Aplicativo Android que exibe uma lista de gatos com fotos, permitindo aos usuários marcar favoritos. Desenvolvido com Clean Architecture e layouts XML.

## Sobre o Projeto

O CatImageList é um aplicativo Android moderno que busca e exibe imagens de gatos usando a API externa. Os usuários podem ver fotos de diferentes gatos, adicionar aos favoritos e navegar entre os itens.

A aplicação segue principios de Clean Architecture com separacao clara de camadas e código testável.

## Funcionalidades

- Listagem de gatos com imagens de alta qualidade
- Sistema de favoritos para salvar gatos preferidos
- Estados de loading, erro e lista vazia com feedback visual ao usuário
- Interface responsiva e moderna com layouts XML
- Animações Lottie para melhorar a experiência
- Suporte a diferentes tamanhos de tela e densidades

## Arquitetura

O projeto implementa Clean Architecture com as seguintes camadas:

### Domain Layer
Contém a lógica de negócio através de UseCases:
- `GetCatsUseCase`: Busca lista de gatos
- `ToggleFavoriteUseCase`: Adiciona/remove dos favoritos

### Data Layer
Responsável pelo acesso a dados:
- `CatRepository`: Centraliza acesso a dados, podendo vir de API ou local
- Padrão Repository para abstrair fontes de dados

### Presentation Layer
Gerencia UI e estado:
- `CatViewModel`: Gerencia estado da UI e interage com UseCases
- `CatUiState`: Representa estados da tela (Loading, Content, Error, Empty)
- Fragments dedicados para cada estado visual
- Layouts XML para componentes de UI
- `CatViewModelFactory`: Injeção manual de dependências

## Tecnologias

- **Linguagem**: Kotlin
- **Arquitetura**: Clean Architecture
- **UI**: Layouts XML + Fragments
- **ViewModel**: Jetpack ViewModel
- **Async**: Coroutines
- **Animações**: Lottie
- **Dependency Injection**: Manual via ViewModelFactory
## Como Executar

### Pré-requisitos

- Android Studio Hedgehog ou superior
- JDK 17 ou superior
- Android SDK com API level 24 ou superior
- Gradle 8.13

### Passos

1. Clone este repositório:
   ```bash
   git clone https://github.com/seu-usuario/CatImageList.git
   cd CatImageList
   ```

2. Abra o projeto no Android Studio

3. Sincronize o Gradle

4. Execute no emulador ou dispositivo conectado via Android Studio ou linha de comando:
   ```bash
   ./gradlew installDebug
   ```

## Estados da UI

A aplicação possui estados distintos para feedback visual:

- **Loading**: Animação enquanto carrega dados
- **Content**: Lista de gatos carregada
- **Empty**: Lista de favoritos vazia
- **Error**: Erro ao carregar dados com mensagem e botão de retry

## Contribuindo

Contribuicoes sao bem-vindas. Para contribuir:

1. Fork este repositório
2. Crie uma branch para sua feature (`git checkout -b feature/SuaFeature`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/SuaFeature`)
5. Abra um Pull Request

## Licenca

Este projeto está sob a licença MIT. Veja o arquivo LICENSE para detalhes.

## Autor

Desenvolvido por Mariana Silva

## Agradecimentos

- Lottie Airbnb pelas animacoes
- Documentacao oficial do Android
- Comunidade Android

