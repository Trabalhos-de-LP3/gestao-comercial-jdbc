package config;

import exception.NegocioException;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionManager {

    private static final ThreadLocal<Connection> CONEXAO_ATUAL = new ThreadLocal<>();

    private TransactionManager() {
    }

    public static Connection getConnection() {
        Connection conexao = CONEXAO_ATUAL.get();
        if (conexao != null) {
            return conexao;
        }
        return ConnectionFactory.abrir();
    }

    public static boolean transacaoAtiva() {
        return CONEXAO_ATUAL.get() != null;
    }

    public static void liberar(Connection conexao) {
        if (!transacaoAtiva() && conexao != null) {
            fechar(conexao);
        }
    }

    public static void executar(Runnable acao) {
        if (transacaoAtiva()) {
            acao.run();
            return;
        }

        Connection conexao = ConnectionFactory.abrir();
        try {
            conexao.setAutoCommit(false);
            CONEXAO_ATUAL.set(conexao);
            acao.run();
            conexao.commit();
        } catch (RuntimeException | Error e) {
            reverter(conexao);
            throw e;
        } catch (SQLException e) {
            reverter(conexao);
            throw new NegocioException("Falha ao controlar a transação.", e);
        } finally {
            CONEXAO_ATUAL.remove();
            fechar(conexao);
        }
    }

    private static void reverter(Connection conexao) {
        try {
            conexao.rollback();
        } catch (SQLException e) {
            throw new NegocioException("Falha ao reverter a transação.", e);
        }
    }

    private static void fechar(Connection conexao) {
        try {
            conexao.close();
        } catch (SQLException e) {
            throw new NegocioException("Falha ao fechar a conexão.", e);
        }
    }
}
