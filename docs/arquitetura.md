# Arquitetura do Sistema — DigitalVX

## 1. Visão geral

O DigitalVX é uma aplicação web desenvolvida em Java com Spring Boot para apoiar a gestão de uma gráfica. O sistema reúne cadastros, gerenciamento de pedidos, controle de estoque, autenticação de usuários e registro financeiro.

A arquitetura separa a interface web, o processamento das requisições, as regras de negócio, o acesso aos dados e a persistência no banco de dados.

## 2. Tecnologias e responsabilidades

- **Java 21:** linguagem de desenvolvimento.
- **Spring Boot:** estrutura principal da aplicação.
- **Spring MVC:** gerenciamento das requisições HTTP.
- **Spring Data JPA e Hibernate:** persistência e mapeamento objeto-relacional.
- **PostgreSQL:** banco de dados da aplicação.
- **Spring Security:** autenticação e controle de acesso.
- **Thymeleaf, HTML e CSS:** construção e apresentação das páginas web.
- **Maven:** gerenciamento de dependências, compilação e testes.
- **JUnit e Spring Boot Test:** testes automatizados.
- **H2:** banco de dados utilizado nos testes.

## 3. Organização da aplicação

### Controllers

Recebem as requisições do navegador, interagem com os serviços ou repositórios e retornam as páginas correspondentes.

Principais classes:
- `HomeController`: painel inicial e indicadores.
- `ClienteController`: gerenciamento de clientes.
- `FuncionarioController`: gerenciamento de funcionários.
- `ProdutoController`: gerenciamento de produtos.
- `ServicoController`: gerenciamento de serviços.
- `PedidoController`: criação e acompanhamento de pedidos.
- `EstoqueController`: consulta das quantidades disponíveis.
- `FinanceiroController`: registro e consulta de movimentações financeiras.
- `UsuarioController`: gerenciamento de usuários.
- `LoginController`: página de autenticação.

### Services

Concentram regras de negócio e operações que precisam de validações específicas.

Principais classes:
- `PedidoService`: validação de pedidos, transições de status e baixa de estoque na entrega.
- `MovimentacaoFinanceiraService`: registro e consulta de entradas e saídas financeiras.
- `UsuarioService`: operações relacionadas aos usuários.
- `UsuarioDetailsService`: carregamento dos usuários para autenticação.
- `AdminBootstrapService`: inicialização do primeiro administrador conforme a configuração do ambiente.

### Repositories

Realizam consultas e operações de persistência utilizando Spring Data JPA.

Exemplos:
- `ClienteRepository`
- `FuncionarioRepository`
- `ProdutoRepository`
- `ServicoRepository`
- `PedidoRepository`
- `ItemPedidoRepository`
- `UsuarioRepository`
- `MovimentacaoFinanceiraRepository`

### Entidades e DTOs

As entidades representam os dados persistidos, enquanto os DTOs transportam dados recebidos pelos formulários sem expor diretamente toda a estrutura das entidades.

Entre as entidades estão `Pessoa`, `Cliente`, `Funcionario`, `Produto`, `Servico`, `Pedido`, `ItemPedido`, `Pagamento`, `Usuario` e `MovimentacaoFinanceira`.

O projeto também utiliza `PedidoForm` e `UsuarioForm` para receber dados de formulários.

### Interface web

As páginas são implementadas com Thymeleaf, HTML e CSS. Os templates apresentam formulários, tabelas, indicadores e opções de navegação.

## 4. Fluxo de uma requisição

1. O usuário acessa uma página pelo navegador.
2. O Spring Security verifica a autenticação e as permissões aplicáveis.
3. A requisição chega ao controller correspondente.
4. O controller executa a operação necessária, utilizando serviços e repositórios.
5. O Spring Data JPA e o Hibernate realizam as operações no PostgreSQL.
6. O resultado é apresentado ao usuário por meio das páginas Thymeleaf.

## 5. Persistência e regras de negócio

As entidades são mapeadas para o banco relacional por meio do Jakarta Persistence (JPA).

No fluxo de pedidos, os produtos e serviços são registrados como itens. Os pedidos percorrem os estados `ABERTO`, `EM PRODUÇÃO`, `PRONTO` e `ENTREGUE`. A baixa do estoque ocorre na entrega, após a validação da disponibilidade dos produtos. Serviços não afetam o estoque.

O módulo financeiro permite registrar manualmente entradas e saídas, consultar o histórico e gerar relatórios por período.

## 6. Segurança

A aplicação utiliza Spring Security para autenticação por formulário e controle de acesso por perfil. As senhas dos usuários são armazenadas com hash BCrypt. O acesso à área de usuários é restrito ao perfil `ADMINISTRADOR`.

As credenciais do banco de dados e os dados utilizados para a criação inicial do administrador são configurados por variáveis de ambiente.

## 7. Estado atual

A arquitetura atende aos módulos de cadastros, pedidos, estoque, autenticação e controle financeiro implementados nesta versão acadêmica. O projeto possui testes automatizados para partes dos controllers, repositories e services.

## 8. Evolução prevista

A arquitetura poderá ser ampliada em versões futuras com novas funcionalidades, melhorias de segurança, relatórios e integrações, mantendo a separação de responsabilidades existente.
