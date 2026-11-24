# LeKeMaPDV - ERP

Sistema ERP desktop com ponto de venda (PDV), desenvolvido em Java 11 como Trabalho de Conclusão de Curso.

O sistema roda localmente e permite gerenciar clientes, produtos, fornecedores, formas de pagamento, usuários e vendas, com controle de acesso por permissões.

## Funcionalidades

- **Dashboard** com indicadores de clientes, produtos e vendas
- **Clientes**: cadastro de pessoa física e jurídica
- **Produtos**: cadastro com controle de estoque e estoque mínimo
- **Fornecedores**
- **Formas de pagamento**
- **Vendas**: registro de pedidos com múltiplas formas de pagamento e baixa de estoque
- **Usuários e permissões**: login e acesso aos módulos de acordo com as permissões de cada usuário

## Tecnologias

- Java 11
- Swing + [FlatLaf](https://www.formdev.com/flatlaf/) (interface)
- Hibernate 5.6 (ORM)
- PostgreSQL
- Maven

## Arquitetura

O projeto é organizado em camadas:

```
src/main/java/com/erp/
├── config/       # Conexão com o banco
├── model/        # Entidades (mapeadas com Hibernate)
├── repository/   # Acesso a dados
├── service/      # Regras de negócio
├── util/         # Classes utilitárias
└── view/         # Telas (Swing)
```

## Como executar

### Pré-requisitos

- JDK 11 ou superior
- Maven
- PostgreSQL

### 1. Banco de dados

Crie um banco no PostgreSQL e execute o script de criação das tabelas:

```
src/main/resources/init.sql
```

### 2. Configuração da conexão

Copie o arquivo de exemplo e preencha com os dados do seu banco:

```bash
cp src/main/resources/db.properties.example src/main/resources/db.properties
```

```properties
db.url=jdbc:postgresql://localhost:5432/postgres
db.user=seu_usuario
db.password=sua_senha
```

### 3. Compilar e rodar

```bash
mvn clean package
java -jar target/v6_5lekemaPDV.jar
```

Também é possível executar pela IDE, a partir da classe `com.erp.view.MainFrame`.

## Autor

Leonardo Kenzo
