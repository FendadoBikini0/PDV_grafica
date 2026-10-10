# Histórico de desenvolvimento — DigitalVX

## Etapa 1 — Estrutura inicial
- Configuração do projeto Java com Spring Boot e Maven.
- Configuração da persistência com PostgreSQL, Spring Data JPA e Hibernate.
- Criação da página inicial do sistema.

## Etapa 2 — Cadastros
- Implementação do cadastro, listagem e edição de clientes.
- Implementação do cadastro, listagem e edição de produtos.
- Implementação do cadastro, listagem e edição de serviços.
- Criação das respectivas páginas HTML e integração com o banco de dados.
- Implementação de validações para os dados cadastrados.

## Etapa 3 — Modelagem de pedidos
- Criação da entidade ItemPedido para representar os itens de uma venda.
- Alteração dos relacionamentos entre Pedido, ItemPedido, Produto e Servico.
- Implementação do cálculo do subtotal dos itens e do total do pedido.

## Situação atual
O painel inicial e os módulos de cadastro de clientes, produtos e serviços foram implementados e verificados no navegador. Os testes Maven executados até o momento foram concluídos com sucesso.

O módulo de pedidos e vendas, o controle de estoque, os pagamentos e a autenticação ainda precisam ser implementados ou validados.
