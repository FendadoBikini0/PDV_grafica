# Histórico de desenvolvimento — DigitalVX

## Etapa 1 — Estrutura inicial

- Configuração do projeto Java com Spring Boot e Maven.
- Configuração da persistência com PostgreSQL, Spring Data JPA e Hibernate.
- Criação da página inicial e da estrutura básica da interface.

## Etapa 2 — Cadastros

- Implementação dos cadastros e consultas de clientes.
- Implementação dos cadastros e consultas de funcionários.
- Implementação dos cadastros e edição de produtos.
- Implementação dos cadastros e consultas de serviços.
- Criação das páginas HTML e integração com o banco de dados.
- Inclusão de validações para os dados cadastrados.

## Etapa 3 — Pedidos e vendas

- Criação das entidades `Pedido` e `ItemPedido`.
- Definição dos relacionamentos entre pedidos, clientes, funcionários, produtos e serviços.
- Implementação da criação e consulta de pedidos.
- Cálculo dos subtotais dos itens e do total dos pedidos.
- Implementação do fluxo de status: `ABERTO`, `EM PRODUÇÃO`, `PRONTO` e `ENTREGUE`.
- Validação dos itens, quantidades, preços e números dos pedidos.

## Etapa 4 — Controle de estoque

- Implementação da consulta de estoque.
- Exibição de produtos com estoque baixo ou zerado.
- Implementação da baixa de estoque na entrega do pedido.
- Validação da disponibilidade antes da baixa.
- Definição de que itens de serviço não alteram o estoque.

## Etapa 5 — Autenticação e usuários

- Integração do Spring Security.
- Implementação da autenticação por formulário.
- Armazenamento de senhas com BCrypt.
- Implementação dos perfis `ADMINISTRADOR` e `ATENDENTE`.
- Restrição de acesso à área de usuários.
- Implementação da inicialização condicional do primeiro administrador por variáveis de ambiente.

## Etapa 6 — Controle financeiro

- Criação da entidade `MovimentacaoFinanceira`.
- Implementação do registro manual de entradas e saídas.
- Exibição do histórico de movimentações.
- Implementação dos indicadores gerais de entradas, saídas e saldo.
- Implementação de relatórios filtrados por período.

## Etapa 7 — Testes e validações

- Criação de testes automatizados para controllers, repositories e services.
- Testes das regras de negócio dos pedidos e do controle de estoque.
- Testes das operações e consultas financeiras.
- Utilização do H2 no ambiente de testes.
- Verificação manual das principais funcionalidades no navegador.

## Situação final

O DigitalVX reúne as funcionalidades acadêmicas previstas para esta versão: cadastros, pedidos, estoque, autenticação e controle financeiro manual. Novas funcionalidades e melhorias ficam reservadas para versões futuras.