# Relatório Comparativo — JDBC × Hibernate

Sistema de Gestão Comercial implementado em duas versões com o **mesmo domínio e as
mesmas regras de negócio**, mudando apenas a camada de persistência. Este relatório
compara as duas abordagens com base na implementação real do trabalho.

## 1. Visão geral

| Aspecto | JDBC | Hibernate |
|---------|------|-----------|
| Tipo | API de acesso a dados de baixo nível (SQL manual) | Framework ORM (mapeamento objeto-relacional) |
| Quem escreve o SQL | O desenvolvedor | O framework (a partir do mapeamento) |
| Mapeamento linha ↔ objeto | Manual (`ResultSet` → objeto) | Automático (anotações nas entidades) |
| Dependências | `mysql-connector-j` | `hibernate-core` + `mysql-connector-j` (+ transitivas) |

## 2. Esforço de código (camada de persistência)

| Camada | JDBC | Hibernate |
|--------|-----:|----------:|
| DAOs concretos | 645 linhas | 300 linhas |
| Infra (conexão/sessão + transação) | 99 linhas | 86 linhas |
| **Total** | **744 linhas** | **386 linhas** |

A versão Hibernate tem cerca de **metade** do código de persistência, porque não precisa
escrever SQL, montar `PreparedStatement` nem mapear `ResultSet` campo a campo.

## 3. Configuração e conexão

- **JDBC** — `ConnectionFactory` abre conexões via `DriverManager` com a URL/usuário/senha.
  Simples e direto, sem arquivos extras.
- **Hibernate** — `hibernate.cfg.xml` concentra dialeto, credenciais e o registro das
  entidades; `HibernateUtil` cria uma `SessionFactory` única (objeto caro, criado uma vez).
  Mais configuração inicial, mas centralizada.

## 4. Mapeamento objeto-relacional

- **JDBC** — não existe mapeamento automático. Cada DAO traduz manualmente:
  `INSERT/UPDATE` lê os getters do objeto; `SELECT` percorre o `ResultSet` e chama os
  setters. Relacionamentos (venda → itens → produto) são carregados com consultas
  adicionais escritas à mão.
- **Hibernate** — o mapeamento vive nas entidades, por anotações:
  `@Entity`, `@Id @GeneratedValue`, `@Column`, `@ManyToOne`, `@OneToMany`, `@OneToOne`,
  `@Enumerated`. Usamos **acesso por campo** para o framework não disparar os setters com
  validação de regra de negócio ao reidratar os objetos.

## 5. Operações CRUD

| Operação | JDBC | Hibernate |
|----------|------|-----------|
| Inserir | `INSERT` + `RETURN_GENERATED_KEYS` para recuperar o id | `session.persist(obj)` (id preenchido automaticamente) |
| Atualizar | `UPDATE ... WHERE id = ?` | `session.merge(obj)` |
| Buscar por id | `SELECT` + mapeamento manual | `session.get(Classe.class, id)` |
| Listar/filtrar | `SELECT` com `WHERE` + laço no `ResultSet` | HQL (`from Entidade where ...`) |

## 6. Consultas e relacionamentos

- **JDBC** — para montar uma `Venda` completa são feitas consultas encadeadas (venda,
  depois cliente, depois itens, depois o produto de cada item), tudo manual.
- **Hibernate** — uma consulta HQL com `join fetch` traz venda + itens + produto + cliente
  de uma vez, evitando `LazyInitializationException`. O `cascade = ALL` faz a venda
  persistir seus itens automaticamente.

## 7. Transações (consistência venda × estoque)

Nas duas versões a confirmação/cancelamento de venda roda em **uma transação única**
(gravar venda + itens **e** dar baixa/estorno no estoque, tudo ou nada). A orquestração
ficou num `TransactionManager` com `ThreadLocal`, mudando só o mecanismo por baixo:

- **JDBC** — `connection.setAutoCommit(false)` + `commit()` / `rollback()`.
- **Hibernate** — `session.beginTransaction()` + `commit()` / `rollback()`.

Ambas foram testadas com rollback (venda acima do estoque): a operação falha e o estoque
permanece inalterado.

## 8. Vantagens e desvantagens

**JDBC**
- ✅ Controle total sobre o SQL; sem "mágica"; fácil de entender o que vai ao banco.
- ✅ Poucas dependências, leve.
- ❌ Muito código repetitivo (boilerplate); mapeamento manual propenso a erro.
- ❌ Relacionamentos e consultas complexas dão trabalho.

**Hibernate**
- ✅ Muito menos código; produtividade alta.
- ✅ Mapeamento e relacionamentos declarativos; portável entre bancos (dialeto).
- ❌ Curva de aprendizado maior; comportamento menos óbvio (lazy/eager, cache, flush).
- ❌ Mais dependências; SQL gerado nem sempre é o ideal sem ajuste.

## 9. Conclusão

As duas versões resolvem o mesmo problema com resultados idênticos (mesma regra,
mesmo banco). O **JDBC** é melhor para entender o que acontece no nível do SQL e para
cenários simples e controlados. O **Hibernate** entrega muito mais produtividade em
sistemas com muitas entidades e relacionamentos, ao custo de mais abstração e
configuração. Para um sistema de gestão comercial que tende a crescer, o Hibernate é a
escolha mais escalável; o JDBC permanece valioso como base conceitual e para pontos onde
se precisa de SQL afinado à mão.
