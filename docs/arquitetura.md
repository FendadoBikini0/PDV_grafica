# Arquitetura do Sistema — DigitalVX

## 1. Visão geral

O DigitalVX é uma aplicação web desenvolvida em Java com Spring Boot. Sua arquitetura separa a interface, o processamento das requisições, o acesso aos dados e a persistência no banco de dados.

## 2. Tecnologias e responsabilidades

- **Java:** linguagem utilizada no desenvolvimento.
- **Spring Boot:** estrutura principal da aplicação.
- **Spring MVC:** gerenciamento das requisições HTTP por meio dos controllers.
- **Spring Data JPA:** abstração para consultas e operações de persistência.
- **Hibernate:** implementação JPA responsável pelo mapeamento objeto-relacional.
- **PostgreSQL:** banco de dados relacional.
- **Thymeleaf e HTML:** renderização das páginas web, conforme a configuração do projeto.
- **CSS:** estilização e adaptação visual da interface.
- **Maven:** gerenciamento de dependências e execução dos testes.

## 3. Organização da aplicação

### Controllers

Os controllers recebem as requisições do navegador, executam as operações necessárias e retornam páginas ou respostas.

Exemplos:
- `HomeController`: apresenta o painel inicial e seus indicadores.
- `ClienteController`: gerencia as operações relacionadas aos clientes.
- `ProdutoController`: gerencia as operações relacionadas aos produtos.
- `ServicoController`: gerencia as operações relacionadas aos serviços.

### Repositories

Os repositories permitem consultar, salvar, atualizar e excluir registros no banco de dados por meio do Spring Data JPA.

Exemplos:
- `ClienteRepository`
- `ProdutoRepository`
- `ServicoRepository`

### Entidades

As entidades representam os dados persistidos no banco.

- `Pessoa`: classe base para pessoas cadastradas.
- `Cliente`: representa os clientes.
- `Funcionario`: representa os funcionários.
- `Produto`: representa os produtos comercializados.
- `Servico`: representa os serviços oferecidos.
- `Pedido`: representa uma venda ou pedido.
- `ItemPedido`: representa cada produto ou serviço incluído em um pedido.
- `Pagamento`: representa um pagamento associado a um pedido.

### Interface web

As páginas HTML apresentam formulários, tabelas, indicadores e opções de navegação. O CSS define o padrão visual compartilhado entre as telas.

## 4. Fluxo de uma requisição

1. O usuário acessa uma página pelo navegador.
2. A requisição HTTP é encaminhada ao controller correspondente.
3. O controller utiliza o repository para consultar ou persistir dados.
4. O Spring Data JPA e o Hibernate realizam a comunicação com o PostgreSQL.
5. O resultado é devolvido à interface para apresentação ao usuário.

## 5. Persistência dos dados

O mapeamento objeto-relacional é realizado por meio das entidades JPA e de suas anotações, como `@Entity`, `@Id`, `@ManyToOne` e `@OneToMany`.

As operações de persistência utilizam o PostgreSQL, permitindo manter os registros entre diferentes execuções da aplicação.

## 6. Estado atual

A arquitetura já é utilizada nos módulos de clientes, produtos e serviços. O fluxo completo de pedidos, pagamentos, controle de estoque e autenticação ainda está em desenvolvimento ou precisa de validação.

## 7. Evolução prevista

A arquitetura será ampliada para suportar o registro de pedidos e itens, o controle de estoque, os pagamentos e a autenticação dos usuários, mantendo a separação de responsabilidades entre interface, controllers, repositories e entidades.
