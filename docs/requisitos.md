# Requisitos do Sistema — DigitalVX

## 1. Objetivo

O DigitalVX é um sistema web acadêmico para apoiar a gestão de uma gráfica, reunindo cadastros, pedidos, controle de estoque, autenticação e registros financeiros.

## 2. Requisitos funcionais

### RF01 — Painel inicial

O sistema deve apresentar uma página inicial com indicadores gerais da operação.

### RF02 — Clientes

O sistema deve permitir cadastrar, consultar e manter os dados dos clientes.

### RF03 — Funcionários

O sistema deve permitir cadastrar e consultar funcionários.

### RF04 — Produtos

O sistema deve permitir cadastrar, consultar e editar produtos físicos, incluindo nome, código, preço e quantidade em estoque.

### RF05 — Serviços

O sistema deve permitir cadastrar e consultar serviços oferecidos pela gráfica, incluindo tipo, preço e descrição.

### RF06 — Pedidos

O sistema deve permitir registrar pedidos vinculados a clientes e funcionários, contendo produtos e/ou serviços.

### RF07 — Itens e valores dos pedidos

O sistema deve calcular os subtotais dos itens e o total do pedido a partir das quantidades e dos preços cadastrados.

### RF08 — Acompanhamento de pedidos

O sistema deve permitir acompanhar os pedidos pelos status `ABERTO`, `EM PRODUÇÃO`, `PRONTO` e `ENTREGUE`.

### RF09 — Controle de estoque

O sistema deve apresentar as quantidades disponíveis e identificar produtos com estoque baixo ou zerado.

### RF10 — Baixa de estoque

O sistema deve atualizar o estoque dos produtos quando o pedido for entregue, após verificar a disponibilidade. Itens de serviço não devem alterar o estoque.

### RF11 — Autenticação

O sistema deve exigir autenticação para acessar as funcionalidades protegidas.

### RF12 — Controle de acesso

O sistema deve distinguir os perfis `ADMINISTRADOR` e `ATENDENTE` e restringir o acesso à área de gerenciamento de usuários ao administrador.

### RF13 — Gerenciamento de usuários

O sistema deve permitir gerenciar usuários conforme as permissões estabelecidas e utilizar senhas armazenadas com hash BCrypt.

### RF14 — Movimentações financeiras

O sistema deve permitir registrar manualmente entradas e saídas financeiras, com descrição, categoria, valor e data.

### RF15 — Histórico financeiro

O sistema deve apresentar o histórico das movimentações financeiras registradas.

### RF16 — Indicadores financeiros

O sistema deve apresentar os totais gerais de entradas, saídas e saldo.

### RF17 — Relatório financeiro por período

O sistema deve permitir consultar movimentações entre datas informadas, apresentando os valores correspondentes ao período.

## 3. Requisitos não funcionais

### RNF01 — Tecnologia

O sistema deve ser desenvolvido em Java com Spring Boot.

### RNF02 — Persistência

Os dados da aplicação devem ser armazenados em banco de dados PostgreSQL.

### RNF03 — Interface

A aplicação deve disponibilizar uma interface web construída com Thymeleaf, HTML e CSS.

### RNF04 — Segurança das senhas

As senhas dos usuários não devem ser armazenadas em texto puro. Deve ser utilizado hash BCrypt.

### RNF05 — Controle de acesso

As funcionalidades protegidas devem respeitar as regras de autenticação e autorização configuradas no Spring Security.

### RNF06 — Configuração

As credenciais do banco de dados e os dados iniciais do administrador devem ser configuráveis por variáveis de ambiente.

### RNF07 — Testabilidade

O projeto deve possuir testes automatizados para verificar partes das regras de negócio, dos controllers e dos repositories.

### RNF08 — Manutenibilidade

O código deve manter a separação de responsabilidades entre controllers, services, repositories, entidades e templates.

## 4. Regras de negócio

- O número de cada pedido deve ser único.
- Um pedido deve possuir itens válidos e quantidades positivas.
- O preço utilizado no pedido deve ser obtido do cadastro do produto ou serviço.
- A entrega só pode ocorrer quando o pedido estiver no status `PRONTO`.
- A baixa do estoque ocorre na entrega do pedido, após a validação da disponibilidade.
- Serviços não afetam o estoque.
- O relatório financeiro por período deve apresentar os movimentos e totais do intervalo selecionado.
- Os indicadores financeiros gerais representam o histórico completo de movimentações.
- As movimentações financeiras são registradas manualmente nesta versão.

## 5. Limites do escopo

Esta versão tem finalidade acadêmica e contempla os fluxos básicos de cadastros, pedidos, estoque, autenticação e controle financeiro manual. Funcionalidades adicionais e integrações financeiras automáticas não fazem parte do escopo implementado.