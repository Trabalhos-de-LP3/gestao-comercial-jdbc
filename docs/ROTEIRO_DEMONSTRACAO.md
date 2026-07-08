# Roteiro de Demonstração

Passo a passo para apresentar o sistema ao professor e treinar a equipe. Vale para as
duas versões (JDBC e Hibernate) — o menu é idêntico.

## Pré-requisitos

1. Subir o banco (uma vez): na pasta `gestao-comercial-db/`, rodar `docker compose up -d`.
2. Importar o projeto no Eclipse: **File → Import → Maven → Existing Maven Projects**.
3. Rodar a classe `Main` (Run As → Java Application).

> Para começar do zero a qualquer momento: `docker compose down -v && docker compose up -d`
> na pasta do banco recria o esquema e os dados de exemplo (3 clientes, 3 produtos, estoque).

## Fluxos a demonstrar

Os dados de exemplo já trazem clientes (IDs 1–3) e produtos (IDs 1–3) com estoque
(Teclado=50, Mouse=30, Monitor=15).

### 1. Cadastro e consulta de cliente
- Menu `1` (Clientes) → `1` (Cadastrar): nome, CPF no formato `000.000.000-00`, e-mail válido, telefone.
- Menu `1` → `2` (Listar todos): o novo cliente aparece.
- Regras: CPF/e-mail inválidos ou CPF repetido são bloqueados com mensagem de erro.

### 2. Cadastro e consulta de produto (com estoque inicial)
- Menu `2` (Produtos) → `1` (Cadastrar): nome, descrição, preço de venda, preço de custo
  (não pode ser maior que o de venda), categoria, quantidade inicial, quantidade mínima, localização.
- Menu `2` → `2` (Listar todos).

### 3. Controle de estoque
- Menu `3` (Estoque) → `1` (Registrar entrada): informe o ID do produto e a quantidade.
- Menu `3` → `2` (Listar todos): confira a quantidade atualizada.
- Menu `3` → `3` (Listar abaixo do mínimo).

### 4. Venda com itens + baixa de estoque
- Menu `4` (Vendas) → `1` (Nova venda): informe o ID do cliente.
- Adicione itens informando ID do produto e quantidade; digite `0` no ID do produto para finalizar.
- Informe um desconto (ou `0`). A venda é **confirmada** e gravada.
- Menu `3` → `2`: o estoque dos produtos vendidos **diminuiu**.

### 5. Cancelamento + estorno de estoque
- Menu `4` (Vendas) → `2` (Cancelar venda): informe o ID da venda.
- Menu `3` → `2`: o estoque dos itens **voltou** ao valor anterior.

### 6. Consultas
- Menu `5` (Consultas) → `1` (Venda por ID), `2` (por cliente), `3` (por produto — lista as
  vendas que contêm o produto informado), `4` (por status: ABERTA, CONFIRMADA, CANCELADA,
  FATURADA), `5` (por período no formato `AAAA-MM-DD`).

### 7. Regras de negócio (mostrar os bloqueios)
- Vender quantidade maior que o estoque → "Estoque insuficiente para o produto ...".
- Cliente inativo não pode comprar; produto inativo não pode ser vendido
  (inative via menu `1`/`2` → opção `4` e tente usar nas vendas).

## Consistência no banco (DBeaver)

Durante a demo, abra as tabelas `venda`, `item_venda` e `estoque` no DBeaver para mostrar
que os dados gravados batem com a tela e que a baixa/estorno de estoque é consistente.

> A pasta `docs/evidencia-execucao.txt` contém a saída completa de uma execução real
> seguindo exatamente estes passos.
