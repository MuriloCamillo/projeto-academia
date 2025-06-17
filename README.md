# GymTime API

API RESTful desenvolvida em Java com Spring Boot para gerenciamento de alunos, treinos e exercícios para academia.

## 🧾 Funcionalidades

- Cadastro, atualização e remoção de alunos
- Cadastro de treinos e exercícios
- Associação de treinos aos alunos
- Validação de CPF e e-mail
- Tratamento de exceções
- Documentação Swagger/OpenAPI

## 🚀 Tecnologias Utilizadas

- Java 17+
- Spring Boot
- Spring Data JPA
- Hibernate
- Lombok
- H2 Database (padrão)
- Maven
- Swagger (OpenAPI)

## 📁 Estrutura de Diretórios

```
projeto-academia-test/
├── src/
│   ├── main/
│   │   ├── java/br/com/gymtime/
│   │   │   ├── controller/           # Controllers da API
│   │   │   ├── dto/                  # DTOs de entrada e saída
│   │   │   ├── exception/            # Tratamento de exceções
│   │   │   ├── model/                # Entidades JPA
│   │   │   ├── repository/           # Interfaces de persistência
│   │   │   ├── service/              # Interfaces de serviço
│   │   │   ├── service/impl/         # Implementações dos serviços
│   │   │   └── config/               # Configurações (ex: Swagger)
│   │   └── resources/
│   │       ├── application.properties
│   └── test/                         
├── pom.xml                           # Arquivo de configuração do Maven
```

## ▶️ Como Rodar o Projeto

### Pré-requisitos

- Java 17+
- Maven 3.6+

### Passos

1. Clone o repositório:
```bash
git clone https://github.com/leticiaaleme/projeto-academia.git
cd projeto-academia
```

2. Compile e rode a aplicação:
```bash
./mvnw spring-boot:run
```

3. Acesse a API:
- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- H2 Console: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

> Usuário padrão do H2: `sa`  
> URL: `jdbc:h2:mem:gymtimedb`  
> Password: `password`


## 📌 Observações

- O projeto utiliza Lombok. O plugin deve estar habilitado.
- O banco de dados utilizado é em memória (H2), mas futuramente poderá ser adaptado para usar PostgreSQL, MySQL ou outro SGBD.
- A validação de CPF e e-mail está implementada para evitar duplicatas.

## 📄 Autores

- [Letícia Leme](https://github.com/leticiaaleme)
- [Murilo Camillo](https://github.com/MuriloCamillo)

## ⚖️ **Licença**

© 2025 **GymTime** - Todos os direitos reservados.
