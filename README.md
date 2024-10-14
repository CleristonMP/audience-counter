# Audience Counter

## Descrição do Projeto
O **Audience Counter** é um aplicativo desenvolvido para contar a audiência em eventos, oferecendo dois tipos de contagem: **contagem direta** e **contagem por fileiras**. Ele foi projetado para facilitar a contagem e registro da assistência de forma eficiente e precisa, com opções para salvar, visualizar e gerenciar as contagens salvas.

## Funcionalidades do Projeto
- **Contagem Direta**: Adicione, decremente e salve a contagem de pessoas diretamente.
- **Contagem por Fileiras**: Conte pessoas por fileiras, salvando o total de todas as fileiras ao final.
- **Histórico de Contagens**: Exibe as contagens salvas mais recentes.
- **Zerar Contagem**: Reseta a contagem atual.
- **Salvar Contagem**: Salva a contagem com data e hora.
- **Botão "Próxima Fileira"**: Salva a contagem de uma fileira e avança para a próxima.

## Tecnologias Utilizadas
- **Linguagem**: Kotlin
- **Framework**: Jetpack Compose
- **Android Studio**: Versão 2024.1.2 (Koala Feature Drop)
- **DataStore**: Persistência de dados
- **JUnit e Compose Testing**: Para testes automatizados de componentes e layouts

## Como Instalar e Rodar o Projeto
1. Clone o repositório:
   ```bash
   git clone https://github.com/CleristonMP/audience-counter.git
   ```
2. Abra o projeto no Android Studio.
3. Conecte um dispositivo Android ou inicie um emulador.
4. Execute o projeto com o botão "Run".

## Como Usar o Projeto
1. Abra o app.
2. Escolha entre as abas **Contagem Direta** ou **Contagem por Fileiras**.
3. Na contagem direta, incremente ou decremente a contagem e salve.
4. Na contagem por fileiras, insira a quantidade de fileiras, adicione as contagens por fileira e salve o total.
5. Visualize as contagens salvas na tela principal.

## Como Contribuir para o Projeto
1. Faça um fork do repositório.
2. Crie uma nova branch para suas alterações:
   ```bash
   git checkout -b minha-feature
   ```
3. Faça suas alterações e commit:
   ```bash
   git commit -m "Adicionei uma nova feature"
   ```
4. Envie suas alterações:
   ```bash
   git push origin minha-feature
   ```
5. Abra um pull request no GitHub.

## Licença do Projeto
Este projeto é licenciado sob a licença [MIT](LICENSE).

## Autores do Projeto
- **Cleriston Pereira** (GitHub: [CleristonMP](https://github.com/CleristonMP))

## Onde Encontrar Ajuda
- Para obter ajuda ou relatar problemas, acesse a seção de [Issues](https://github.com/CleristonMP/audience-counter/issues) do repositório no GitHub.