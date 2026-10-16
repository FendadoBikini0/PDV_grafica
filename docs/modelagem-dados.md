# Modelo de Dados — DigitalVX

## 1. Visão geral

O modelo de dados do DigitalVX utiliza o paradigma relacional, com entidades Java mapeadas para o PostgreSQL por meio do Jakarta Persistence (JPA) e do Hibernate.

O modelo contempla pessoas, clientes, funcionários, produtos, serviços, pedidos, itens de pedido, usuários e movimentações financeiras.

## 2. Descrição das entidades

### Pessoa

Classe base para informações comuns de pessoas cadastradas, como nome, e-mail e telefone.

### Cliente

Representa o cliente da gráfica. Herda os atributos de `Pessoa` e pode estar associado a vários pedidos.

### Funcionario

Representa o funcionário cadastrado no sistema e associado aos pedidos sob sua responsabilidade.

### Produto

Representa um item físico comercializado pela gráfica. Possui nome, código, preço e quantidade em estoque.

### Servico

Representa um serviço oferecido pela gráfica, contendo tipo, preço e descrição. Não possui estoque próprio.

### Pedido

Representa um pedido registrado no sistema. Contém número, data, status, total, cliente e funcionário responsável.

### ItemPedido

Representa um produto ou serviço incluído em um pedido. Armazena quantidade e preço unitário, permitindo calcular o subtotal do item.

### Pagamento

Entidade presente no projeto para representar informações de pagamento associadas a pedidos. Não deve ser confundida com o módulo financeiro implementado, que registra movimentações manuais de entradas e saídas.

### Usuario

Representa uma conta de acesso ao sistema. Armazena informações de autenticação e o perfil de acesso, que pode ser `ADMINISTRADOR` ou `ATENDENTE`.

### MovimentacaoFinanceira

Representa uma movimentação financeira registrada manualmente. Armazena informações como descrição, categoria, tipo, valor e data.

### Perfil e TipoMovimentacao

`Perfil` define os perfis de acesso disponíveis. `TipoMovimentacao` identifica se uma movimentação financeira é uma `ENTRADA` ou uma `SAIDA`.

## 3. Relacionamentos

- `Cliente` e `Funcionario` são especializações de `Pessoa`.
- Um cliente pode possuir vários pedidos.
- Um funcionário pode ser responsável por vários pedidos.
- Um pedido pode conter vários itens de pedido.
- Cada item de pedido pertence a um pedido.
- Cada item de pedido está associado a um produto ou a um serviço.
- Um produto pode aparecer em vários itens de pedido.
- Um serviço pode aparecer em vários itens de pedido.
- A entidade `Usuario` mantém os dados de acesso e o perfil de segurança.
- As movimentações financeiras são registradas de forma independente dos pedidos nesta versão.

A entidade `Pagamento` existe no código, mas seu uso efetivo no fluxo de vendas deve ser distinguido das movimentações financeiras manuais.

## 4. Regras de integridade e negócio

- O número do pedido deve ser único.
- Um pedido deve possuir itens válidos.
- A quantidade de cada item deve ser maior que zero.
- O preço unitário do item é obtido do cadastro correspondente, não confiado ao valor enviado pelo formulário.
- O subtotal corresponde à quantidade multiplicada pelo preço unitário.
- O total do pedido corresponde à soma dos subtotais.
- Cada item deve estar associado a um produto ou serviço, nunca a ambos simultaneamente nem a nenhum deles.
- A entrega do pedido só pode ocorrer quando seu status for `PRONTO`.
- A baixa de estoque ocorre na entrega e somente após a validação da disponibilidade.
- Itens de serviço não geram baixa de estoque.
- Senhas de usuários são armazenadas com hash BCrypt.
- Movimentações financeiras são classificadas como entradas ou saídas e possuem valor e data.

## 5. Observações

A entidade `ItemPedido` permite registrar quantidades e preços praticados em cada venda, evitando que alterações posteriores no catálogo modifiquem os valores históricos dos itens já registrados.

A modelagem deve ser mantida alinhada às anotações e aos relacionamentos efetivamente declarados nas entidades Java.