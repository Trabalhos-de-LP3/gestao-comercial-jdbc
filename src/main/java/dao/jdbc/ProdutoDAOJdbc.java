package dao.jdbc;

import config.TransactionManager;
import dao.ProdutoDAO;
import exception.NegocioException;
import model.Produto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAOJdbc implements ProdutoDAO {

    @Override
    public void inserir(Produto produto) {
        String sql = "INSERT INTO produto (nome, descricao, preco_venda, preco_custo, categoria, ativo) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conexao = TransactionManager.getConnection();
        try (PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, produto.getNome());
            ps.setString(2, produto.getDescricao());
            ps.setDouble(3, produto.getPrecoVenda());
            ps.setDouble(4, produto.getPrecoCusto());
            ps.setString(5, produto.getCategoria());
            ps.setBoolean(6, produto.isAtivo());
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    produto.setId(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new NegocioException("Erro ao inserir produto.", e);
        } finally {
            TransactionManager.liberar(conexao);
        }
    }

    @Override
    public void atualizar(Produto produto) {
        String sql = "UPDATE produto SET nome = ?, descricao = ?, preco_venda = ?, "
                + "preco_custo = ?, categoria = ?, ativo = ? WHERE id = ?";
        Connection conexao = TransactionManager.getConnection();
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, produto.getNome());
            ps.setString(2, produto.getDescricao());
            ps.setDouble(3, produto.getPrecoVenda());
            ps.setDouble(4, produto.getPrecoCusto());
            ps.setString(5, produto.getCategoria());
            ps.setBoolean(6, produto.isAtivo());
            ps.setInt(7, produto.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new NegocioException("Erro ao atualizar produto.", e);
        } finally {
            TransactionManager.liberar(conexao);
        }
    }

    @Override
    public Produto buscarPorId(int id) {
        String sql = "SELECT * FROM produto WHERE id = ?";
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
            throw new NegocioException("Erro ao buscar produto.", e);
        } finally {
            TransactionManager.liberar(conexao);
        }
    }

    @Override
    public List<Produto> listarTodos() {
        return executarConsulta("SELECT * FROM produto ORDER BY nome", null);
    }

    @Override
    public List<Produto> listarPorStatus(boolean ativo) {
        return executarConsulta("SELECT * FROM produto WHERE ativo = ? ORDER BY nome", ativo);
    }

    @Override
    public List<Produto> listarPorCategoria(String categoria) {
        String sql = "SELECT * FROM produto WHERE categoria = ? ORDER BY nome";
        List<Produto> produtos = new ArrayList<>();
        Connection conexao = TransactionManager.getConnection();
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, categoria);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    produtos.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new NegocioException("Erro ao listar produtos por categoria.", e);
        } finally {
            TransactionManager.liberar(conexao);
        }
        return produtos;
    }

    @Override
    public void deletar(int id) {
        String sql = "DELETE FROM produto WHERE id = ?";
        Connection conexao = TransactionManager.getConnection();
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new NegocioException("Erro ao deletar produto.", e);
        } finally {
            TransactionManager.liberar(conexao);
        }
    }

    private List<Produto> executarConsulta(String sql, Boolean ativo) {
        List<Produto> produtos = new ArrayList<>();
        Connection conexao = TransactionManager.getConnection();
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            if (ativo != null) {
                ps.setBoolean(1, ativo);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    produtos.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new NegocioException("Erro ao listar produtos.", e);
        } finally {
            TransactionManager.liberar(conexao);
        }
        return produtos;
    }

    static Produto mapear(ResultSet rs) throws SQLException {
        Produto produto = new Produto();
        produto.setId(rs.getInt("id"));
        produto.setNome(rs.getString("nome"));
        produto.setDescricao(rs.getString("descricao"));
        produto.setPrecoVenda(rs.getDouble("preco_venda"));
        produto.setPrecoCusto(rs.getDouble("preco_custo"));
        produto.setCategoria(rs.getString("categoria"));
        produto.setAtivo(rs.getBoolean("ativo"));
        return produto;
    }
}
