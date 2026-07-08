# Evidências de Execução — Versão JDBC

Execução real da classe `Main` contra o MySQL (container Docker), seguindo o
[Roteiro de Demonstração](ROTEIRO_DEMONSTRACAO.md). Saída completa em
[`evidencia-execucao.txt`](evidencia-execucao.txt).

## Resumo dos fluxos verificados

| Fluxo | Resultado |
|-------|-----------|
| Cadastro de cliente | `Cliente cadastrado com ID 4.` |
| Consulta de clientes | lista os 3 do seed + o novo (João Teste) |
| Cadastro de produto + estoque | `Produto cadastrado com ID 4.` (Cadeira Gamer) |
| Entrada de estoque (produto 1) | Teclado `50 → 60` |
| Venda com itens (3× Teclado + 2× Mouse) | Total bruto R$ 1110,00 |
| Desconto de R$ 50 | Total líquido **R$ 1060,00** |
| Confirmação da venda | `Venda confirmada com ID 1` |
| Baixa de estoque ao confirmar | Teclado `60 → 57`, Mouse `30 → 28` |
| Consulta de venda por ID e por cliente | Venda #1 com itens e totais |
| Cancelamento da venda | `Venda cancelada e estoque estornado.` |
| Estorno de estoque ao cancelar | Teclado `57 → 60`, Mouse `28 → 30` |
| Consulta por status (CANCELADA) | Venda #1 com status CANCELADA |
| Regra: estoque insuficiente | `Erro: Estoque insuficiente para o produto Teclado Mecanico` |

## Controle transacional

A confirmação e o cancelamento de venda rodam dentro de uma transação JDBC única
(`TransactionManager.executar` → `setAutoCommit(false)` + `commit`/`rollback`).
Teste de rollback (vender além do estoque): a operação falha e o estoque permanece
inalterado — gravação da venda e baixa de estoque são atômicas (tudo ou nada).
