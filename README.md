# Projeto de Gestão de Vacuum e Backup

Este projeto é uma aplicação Java que permite executar operações de vacuum e backup em um banco de dados PostgreSQL. O programa oferece uma interface gráfica simples, onde o usuário pode escolher as opções desejadas para gerenciamento de vacuum e backup.

## Requisitos

- Java Development Kit (JDK) 8 ou superior
- Eclipse ou IntelliJ IDEA
- Biblioteca do driver JDBC do PostgreSQL

## Configuração do Ambiente

### Adicionando o Driver do PostgreSQL

Caso o programa não consiga se conectar ao banco de dados PostgreSQL automaticamente, você precisará adicionar o driver JDBC do PostgreSQL à biblioteca do seu projeto.

#### No Eclipse

1. Baixe a versão mais recente do driver JDBC do PostgreSQL [aqui](https://jdbc.postgresql.org/download).
2. Abra o Eclipse e seu projeto.
3. Clique com o botão direito no projeto na `Package Explorer` e selecione `Build Path > Configure Build Path`.
4. Vá até a aba `Libraries` e clique em `Add External JARs...`.
5. Selecione o arquivo `.jar` do driver que você baixou e clique em `OK`.
6. Clique em `Apply and Close`.

#### No IntelliJ IDEA

1. Baixe a versão mais recente do driver JDBC do PostgreSQL [aqui](https://jdbc.postgresql.org/download).
2. Abra o IntelliJ IDEA e seu projeto.
3. Vá até `File > Project Structure`.
4. Na aba `Libraries`, clique em `+` e escolha `Java`.
5. Selecione o arquivo `.jar` do driver que você baixou e clique em `OK`.
6. Clique em `Apply` e depois em `OK`.

## Como Usar

1. Compile e execute o projeto.
2. A interface gráfica será exibida.
3. Selecione as opções desejadas para as operações de vacuum e backup.
4. Clique no botão "Iniciar" para executar as operações.

## Licença

Este projeto está licenciado sob a [Licença MIT](LICENSE).
