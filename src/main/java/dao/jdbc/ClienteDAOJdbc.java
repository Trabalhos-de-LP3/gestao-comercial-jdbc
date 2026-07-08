package dao.jdbc;

import config.TransactionManager;
import dao.ClienteDAO;
import exception.NegocioException;
import model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAOJdbc implements ClienteDAO {

    @Override
    public void inserir(Cliente cliente) {
        String sql = "INSERT INTO cliente (nome, cpf, email, telefone, ativo, data_cadastro) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conexao = TransactionManager.getConnection();
        try (PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getCpf());
            ps.setString(3, cliente.getEmail());
            ps.setString(4, cliente.getTelefone());
            ps.setBoolean(5, cliente.isAtivo());
            ps.setTimestamp(6, Timestamp.valueOf(cliente.getDataCadastro()));
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    cliente.setId(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new NegocioException("Erro ao inserir cliente.", e);
        } finally {
            TransactionManager.liberar(conexao);
        }
    }

    @Override
    public void atualizar(Cliente cliente) {
        String sql = "UPDATE cliente SET nome = ?, cpf = ?, email = ?, telefone = ?, ativo = ? "
                + "WHERE id = ?";
        Connection conexao = TransactionManager.getConnection();
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getCpf());
            ps.setString(3, cliente.getEmail());
            ps.setString(4, cliente.getTelefone());
            ps.setBoolean(5, cliente.isAtivo());
            ps.setInt(6, cliente.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new NegocioException("Erro ao atualizar cliente.", e);
        } finally {
            TransactionManager.liberar(conexao);
        }
    }

    @Override
    public Cliente buscarPorId(int id) {
        String sql = "SELECT * FROM cliente WHERE id = ?";
        Connection conexao = TransactionManager.getConnection();
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new NegocioException("Erro ao buscar cliente.", e);
        } finally {
            TransactionManager.liberar(conexao);
        }
    }

    @Override
    public List<Cliente> listarTodos() {
        String sql = "SELECT * FROM cliente ORDER BY nome";
        return executarConsulta(sql, null);
    }

    @Override
    public List<Cliente> listarPorStatus(boolean ativo) {
        String sql = "SELECT * FROM cliente WHERE ativo = ? ORDER BY nome";
        return executarConsulta(sql, ativo);
    }

    @Override
    public boolean existeCpf(String cpf) {
        String sql = "SELECT 1 FROM cliente WHERE cpf = ?";
        Connection conexao = TransactionManager.getConnection();
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, cpf);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new NegocioException("Erro ao verificar CPF.", e);
        } finally {
            TransactionManager.liberar(conexao);
        }
    }

    @Override
    public void deletar(int id) {
        String sql = "DELETE FROM cliente WHERE id = ?";
        Connection conexao = TransactionManager.getConnection();
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new NegocioException("Erro ao deletar cliente.", e);
        } finally {
            TransactionManager.liberar(conexao);
        }
    }

    private List<Cliente> executarConsulta(String sql, Boolean ativo) {
        List<Cliente> clientes = new ArrayList<>();
        Connection conexao = TransactionManager.getConnection();
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            if (ativo != null) {
                ps.setBoolean(1, ativo);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    clientes.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new NegocioException("Erro ao listar clientes.", e);
        } finally {
            TransactionManager.liberar(conexao);
        }
        return clientes;
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId(rs.getInt("id"));
        cliente.setNome(rs.getString("nome"));
        cliente.setCpf(rs.getString("cpf"));
        cliente.setEmail(rs.getString("email"));
        cliente.setTelefone(rs.getString("telefone"));
        cliente.setAtivo(rs.getBoolean("ativo"));
        cliente.setDataCadastro(rs.getTimestamp("data_cadastro").toLocalDateTime());
        return cliente;
    }
}
