# Sistema de Gestão Comercial — Versão JDBC

Gestão de clientes, produtos, estoque e vendas em Java, com persistência em MySQL via
**JDBC** (SQL manual). Faz parte de um trabalho com duas versões (JDBC e Hibernate) que
compartilham o mesmo domínio e as mesmas regras de negócio.

## Stack

- Java 21 (Maven)
- JDBC (`mysql-connector-j`)
- MySQL 8.4 em container Docker

## Como rodar

1. **Banco**: na pasta `gestao-comercial-db/` (na raiz do workspace), rode `docker compose up -d`.
   Cria o banco `gestao_comercial` (usuário `app`/`app`, porta 3306) com tabelas e dados de exemplo.
2. **Projeto**: no Eclipse, **File → Import → Maven → Existing Maven Projects** apontando para esta pasta.
3. Rode a classe `Main` (Run As → Java Application) e use o menu no console.

## Estrutura

```
src/main/java/
├── exception/   NegocioException, EstoqueInsuficienteException
├── model/       Cliente, Produto, Estoque, Venda, ItemVenda, StatusVenda
├── dao/         interfaces (contrato de persistência)
├── dao/jdbc/    implementação JDBC (PreparedStatement, mapeamento manual)
├── service/     regras de negócio e validações
├── config/      ConnectionFactory, TransactionManager (transação com ThreadLocal)
└── Main.java    menu textual
```

## Regras de negócio

- Cliente/produto inativo não participa de vendas.
- Não se vende acima do estoque disponível.
- Confirmar venda dá baixa no estoque; cancelar estorna — em **transação única**.
- Total da venda calculado pelos itens; preço unitário registrado no momento da venda.

## Documentação

- [`docs/ROTEIRO_DEMONSTRACAO.md`](docs/ROTEIRO_DEMONSTRACAO.md) — passo a passo da demo.
- [`docs/EVIDENCIAS.md`](docs/EVIDENCIAS.md) — fluxos verificados em execução real.
- [`docs/RELATORIO_COMPARATIVO.md`](docs/RELATORIO_COMPARATIVO.md) — JDBC × Hibernate.
