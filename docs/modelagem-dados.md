# Modelo de Dados — DigitalVX

## 1. Visão geral

O modelo de dados do DigitalVX utiliza o paradigma relacional, com entidades Java mapeadas para tabelas no PostgreSQL por meio do Jakarta Persistence (JPA) e do Hibernate.

O modelo contempla pessoas, clientes, funcionários, produtos, serviços, pedidos, itens de pedido e pagamentos.

## 2. Descrição das entidades

### Pessoa
Classe base abstrata para os dados comuns de pessoas cadastradas no sistema, como nome, e-mail e telefone.

### Cliente
Representa o cliente da gráfica. Herda os atributos de Pessoa e pode estar associado a vários pedidos.

### Funcionario
Representa o funcionário responsável pelo atendimento e pelo registro dos pedidos.

### Produto
Representa um item físico comercializado pela gráfica. Possui nome, código, preço e quantidade em estoque.

### Servico
Representa um serviço oferecido pela gráfica, contendo tipo, preço e descrição. Não possui estoque próprio.

### Pedido
Representa um pedido registrado no sistema. Contém número, data, status, total, cliente e funcionário responsável.

### ItemPedido
Representa um produto ou serviço incluído em um pedido. Armazena a quantidade, o preço unitário praticado na venda e permite calcular o subtotal.

### Pagamento
Representa um pagamento associado a um pedido, com informações como valor, data e forma de pagamento.

## 3. Relacionamentos

- Uma Pessoa pode ser especializada como Cliente ou Funcionario.
- Um Cliente pode possuir vários pedidos.
- Um Funcionario pode ser responsável por vários pedidos.
- Um Pedido pode conter vários itens de pedido.
- Cada ItemPedido pertence a um único Pedido.
- Cada ItemPedido deve estar associado a exatamente um Produto ou um Servico.
- Um Pedido pode possuir vários pagamentos.
- Um Produto pode aparecer em vários itens de pedido.
- Um Servico pode aparecer em vários itens de pedido.

## 4. Regras de integridade

- Cada pedido deve possuir um cliente e um funcionário responsável.
- A quantidade de um item deve ser maior que zero.
- O preço unitário deve ser preservado no registro do item para manter o histórico da venda.
- O subtotal corresponde à multiplicação da quantidade pelo preço unitário.
- O total do pedido corresponde à soma dos subtotais dos seus itens.
- Um item não pode estar associado simultaneamente a um produto e a um serviço, nem ficar sem ambos.

## 5. Observações

A modelagem de ItemPedido substitui a associação direta entre Pedido e Produto ou Servico. Essa estrutura permite registrar quantidades e preços históricos, evitando que alterações posteriores no catálogo modifiquem o valor de vendas anteriores.

A implementação completa dos relacionamentos e das regras de integridade deve ser validada durante os testes do módulo de pedidos.
