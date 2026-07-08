package dao.jdbc;

import config.TransactionManager;
import dao.EstoqueDAO;
import exception.NegocioException;
import model.Estoque;
import model.Produto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class EstoqueDAOJdbc implements EstoqueDAO {

    private final ProdutoDAOJdbc produtoDAO = new ProdutoDAOJdbc();

    @Override
    public void inserir(Estoque estoque) {
        String sql = "INSERT INTO estoque (produto_id, quantidade_disponivel, quantidade_minima, "
                + "localizacao, ultima_atualizacao) VALUES (?, ?, ?, ?, ?)";
        Connection conexao = TransactionManager.getConnection();
        try (PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, estoque.getProduto().getId());
            ps.setInt(2, estoque.getQuantidadeDisponivel());
            ps.setInt(3, estoque.getQuantidadeMinima());
            ps.setString(4, estoque.getLocalizacao());
            ps.setTimestamp(5, Timestamp.valueOf(estoque.getUltimaAtualizacao()));
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    estoque.setId(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new NegocioException("Erro ao inserir estoque.", e);
        } finally {
            TransactionManager.liberar(conexao);
        }
    }

    @Override
    public void atualizar(Estoque estoque) {
        String sql = "UPDATE estoque SET quantidade_disponivel = ?, quantidade_minima = ?, "
                + "localizacao = ?, ultima_atualizacao = ? WHERE id = ?";
        Connection conexao = TransactionManager.getConnection();
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, estoque.getQuantidadeDisponivel());
            ps.setInt(2, estoque.getQuantidadeMinima());
            ps.setString(3, estoque.getLocalizacao());
            ps.setTimestamp(4, Timestamp.valueOf(estoque.getUltimaAtualizacao()));
            ps.setInt(5, estoque.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new NegocioException("Erro ao atualizar estoque.", e);
        } finally {
            TransactionManager.liberar(conexao);
        }
    }

    @Override
    public Estoque buscarPorProduto(int produtoId) {
        String sql = "SELECT * FROM estoque WHERE produto_id = ?";
        Connection conexao = TransactionManager.getConnection();
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, produtoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new NegocioException("Erro ao buscar estoque.", e);
        } finally {
            TransactionManager.liberar(conexao);
        }
    }

    @Override
    public List<Estoque> listarTodos() {
        return executarConsulta("SELECT * FROM estoque ORDER BY produto_id");
    }

    @Override
    public List<Estoque> listarAbaixoDoMinimo() {
        return executarConsulta("SELECT * FROM estoque WHERE quantidade_disponivel < quantidade_minima "
                + "ORDER BY produto_id");
    }

    private List<Estoque> executarConsulta(String sql) {
        List<Estoque> estoques = new ArrayList<>();
        Connection conexao = TransactionManager.getConnection();
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    estoques.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new NegocioException("Erro ao listar estoque.", e);
        } finally {
            TransactionManager.liberar(conexao);
        }
        return estoques;
    }

    private Estoque mapear(ResultSet rs) throws SQLException {
        Produto produto = produtoDAO.buscarPorId(rs.getInt("produto_id"));
        Estoque estoque = new Estoque();
        estoque.setId(rs.getInt("id"));
        estoque.setProduto(produto);
        estoque.setQuantidadeMinima(rs.getInt("quantidade_minima"));
        estoque.setLocalizacao(rs.getString("localizacao"));
        estoque.setUltimaAtualizacao(rs.getTimestamp("ultima_atualizacao").toLocalDateTime());
        estoque.setQuantidadeDisponivel(rs.getInt("quantidade_disponivel"));
        return estoque;
    }
}
