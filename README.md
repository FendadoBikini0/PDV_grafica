# DigitalVX — Sistema de Ponto de Venda (PDV)

Sistema web acadêmico desenvolvido para apoiar a rotina de uma gráfica, reunindo cadastros, acompanhamento de pedidos, controle de estoque e registro financeiro em uma única aplicação.

## Funcionalidades

- **Painel inicial:** visão geral e indicadores da operação.
- **Clientes:** cadastro e consulta de clientes.
- **Funcionários:** cadastro e consulta de funcionários.
- **Produtos:** cadastro e edição de produtos físicos, com código, preço e quantidade em estoque.
- **Estoque:** acompanhamento das quantidades disponíveis e identificação de produtos com estoque baixo ou zerado.
- **Serviços:** cadastro e consulta de serviços oferecidos.
- **Pedidos e vendas:** criação e acompanhamento de pedidos que podem conter produtos e/ou serviços.
- **Fluxo de pedidos:** `ABERTO` → `EM PRODUÇÃO` → `PRONTO` → `ENTREGUE`.
- **Baixa de estoque:** os produtos do pedido têm o estoque atualizado quando o pedido é marcado como entregue; serviços não afetam o estoque. O sistema valida a disponibilidade antes da baixa.
- **Usuários e autenticação:** login, senha armazenada com hash BCrypt e controle de acesso por perfil. A área de usuários é restrita ao perfil `ADMINISTRADOR`.
- **Financeiro:** registro manual de entradas e saídas, indicadores gerais, histórico recente e relatório filtrado por período com totais de entradas, saídas e saldo.

## Tecnologias

- Java 21
- Spring Boot 4.1.1
- Spring MVC / Spring Web
- Spring Data JPA e Hibernate
- Spring Security
- Thymeleaf
- PostgreSQL
- Maven Wrapper
- JUnit / Spring Boot Test
- H2 para testes
- HTML e CSS

## Estrutura do projeto

O código da aplicação está na pasta `demo/`.

```text
demo/
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── config/       # Configuração de segurança e inicialização do administrador
│   │   │   ├── controller/   # Controladores web
│   │   │   ├── dto/          # Objetos usados para receber dados de formulários
│   │   │   ├── entity/       # Entidades do domínio
│   │   │   ├── repository/   # Acesso a dados
│   │   │   └── service/      # Regras de negócio
│   │   └── resources/
│   │       ├── static/css/   # Estilos
│   │       ├── templates/    # Páginas Thymeleaf
│   │       ├── application.properties
│   │       └── application-example.properties
│   └── test/                 # Testes automatizados
├── mvnw
├── mvnw.cmd
└── pom.xml
```

## Requisitos para executar

- JDK 21 instalado.
- PostgreSQL instalado e em execução.
- Banco de dados PostgreSQL criado para a aplicação.
- Variáveis de ambiente configuradas conforme a seção abaixo.

## Configuração do banco de dados

A aplicação lê as seguintes variáveis de ambiente:

| Variável | Finalidade | Valor padrão |
|---|---|---|
| `DB_URL` | URL JDBC do PostgreSQL | `jdbc:postgresql://localhost:5433/digitalvx` |
| `DB_USERNAME` | Usuário do banco | `postgres` |
| `DB_PASSWORD` | Senha do banco | Sem valor padrão; deve ser configurada |

Crie o banco `digitalvx` no PostgreSQL ou ajuste `DB_URL` para apontar para o banco escolhido. Configure `DB_PASSWORD` com a senha do usuário do banco. Se usar outra porta, usuário ou nome de banco, ajuste as variáveis correspondentes.

> Não coloque senhas reais no Git nem compartilhe arquivos de configuração contendo credenciais.

## Primeiro acesso: administrador

Na inicialização, o projeto pode criar o primeiro usuário administrador quando as quatro variáveis abaixo estiverem definidas e ainda não houver usuários cadastrados:

- `ADMIN_LOGIN`
- `ADMIN_PASSWORD`
- `ADMIN_NOME`
- `ADMIN_EMAIL`

A senha é armazenada com BCrypt. O inicializador não cria outro administrador quando já existe pelo menos um usuário. Configure essas variáveis no ambiente em que a aplicação será executada antes da primeira inicialização.

## Executar no Windows

Abra o terminal na pasta `DigitalVX/demo` e execute:

```powershell
.\mvnw.cmd spring-boot:run
```

Depois que a aplicação iniciar, acesse:

```text
http://localhost:8080
```

O acesso às páginas da aplicação exige autenticação. Entre com o usuário administrador configurado pelas variáveis de ambiente.

## Executar testes

Na pasta `DigitalVX/demo`, execute:

```powershell
.\mvnw.cmd test
```

Os testes automatizados cobrem partes dos controladores, repositórios, serviços e inicialização da aplicação. O projeto inclui H2 no escopo de testes.

## Regras de negócio relevantes

- Os pedidos percorrem os estados `ABERTO`, `EM PRODUÇÃO`, `PRONTO` e `ENTREGUE`.
- A entrega só pode ocorrer a partir do estado `PRONTO`.
- O estoque é baixado na entrega do pedido, e não na sua criação.
- Itens de serviço não geram baixa de estoque.
- A disponibilidade dos produtos é validada antes de efetuar a baixa.
- O relatório financeiro por período apresenta os movimentos e os totais referentes ao intervalo informado; os indicadores gerais representam o histórico completo.
- As movimentações financeiras são registradas manualmente nesta versão.

## Escopo e limitações da versão

Esta é a versão acadêmica final do DigitalVX. O foco é demonstrar o fluxo básico de gestão de uma gráfica: cadastros, pedidos, estoque, autenticação e controle financeiro manual. Melhorias e novas funcionalidades ficam reservadas para versões futuras.

## Observações de segurança e implantação

- Use senhas fortes para o banco e para o administrador.
- Não versione credenciais nem as publique em repositórios.
- A configuração atual de JPA usa `spring.jpa.hibernate.ddl-auto=update`, adequada ao contexto de desenvolvimento acadêmico; antes de uma implantação real, revise a estratégia de migração do banco e as demais configurações de produção.
- O sistema foi desenvolvido como projeto acadêmico e não deve ser considerado pronto para produção sem uma revisão adicional de segurança, operação, backup e implantação.
