package dao.jdbc;

import config.TransactionManager;
import dao.VendaDAO;
import exception.NegocioException;
import model.Cliente;
import model.ItemVenda;
import model.Produto;
import model.StatusVenda;
import model.Venda;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VendaDAOJdbc implements VendaDAO {

    private final ClienteDAOJdbc clienteDAO = new ClienteDAOJdbc();
    private final ProdutoDAOJdbc produtoDAO = new ProdutoDAOJdbc();

    @Override
    public void inserir(Venda venda) {
        String sqlVenda = "INSERT INTO venda (cliente_id, data_venda, status, total_bruto, "
                + "desconto, total_liquido) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conexao = TransactionManager.getConnection();
        try (PreparedStatement ps = conexao.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, venda.getCliente().getId());
            ps.setTimestamp(2, Timestamp.valueOf(venda.getDataVenda()));
            ps.setString(3, venda.getStatus().name());
            ps.setDouble(4, venda.getTotalBruto());
            ps.setDouble(5, venda.getDesconto());
            ps.setDouble(6, venda.getTotalLiquido());
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    venda.setId(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new NegocioException("Erro ao inserir venda.", e);
        } finally {
            TransactionManager.liberar(conexao);
        }
        inserirItens(venda);
    }

    private void inserirItens(Venda venda) {
        String sql = "INSERT INTO item_venda (venda_id, produto_id, quantidade, preco_unitario, subtotal) "
                + "VALUES (?, ?, ?, ?, ?)";
        Connection conexao = TransactionManager.getConnection();
        try (PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (ItemVenda item : venda.getItens()) {
                ps.setInt(1, venda.getId());
                ps.setInt(2, item.getProduto().getId());
                ps.setInt(3, item.getQuantidade());
                ps.setDouble(4, item.getPrecoUnitario());
                ps.setDouble(5, item.getSubtotal());
                ps.executeUpdate();
                try (ResultSet chaves = ps.getGeneratedKeys()) {
                    if (chaves.next()) {
                        item.setId(chaves.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new NegocioException("Erro ao inserir itens da venda.", e);
        } finally {
            TransactionManager.liberar(conexao);
        }
    }

    @Override
    public void atualizarStatus(Venda venda) {
        String sql = "UPDATE venda SET status = ?, total_bruto = ?, desconto = ?, total_liquido = ? "
                + "WHERE id = ?";
        Connection conexao = TransactionManager.getConnection();
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, venda.getStatus().name());
            ps.setDouble(2, venda.getTotalBruto());
            ps.setDouble(3, venda.getDesconto());
            ps.setDouble(4, venda.getTotalLiquido());
            ps.setInt(5, venda.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new NegocioException("Erro ao atualizar venda.", e);
        } finally {
            TransactionManager.liberar(conexao);
        }
    }

    @Override
    public Venda buscarPorId(int id) {
        String sql = "SELECT * FROM venda WHERE id = ?";
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
            throw new NegocioException("Erro ao buscar venda.", e);
        } finally {
            TransactionManager.liberar(conexao);
        }
    }

    @Override
    public List<Venda> listarPorCliente(int clienteId) {
        String sql = "SELECT * FROM venda WHERE cliente_id = ? ORDER BY data_venda DESC";
        Connection conexao = TransactionManager.getConnection();
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, clienteId);
            return mapearLista(ps);
        } catch (SQLException e) {
            throw new NegocioException("Erro ao listar vendas por cliente.", e);
        } finally {
            TransactionManager.liberar(conexao);
        }
    }

    @Override
    public List<Venda> listarPorProduto(int produtoId) {
        String sql = "SELECT DISTINCT v.* FROM venda v "
                + "JOIN item_venda i ON i.venda_id = v.id "
                + "WHERE i.produto_id = ? ORDER BY v.data_venda DESC";
        Connection conexao = TransactionManager.getConnection();
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, produtoId);
            return mapearLista(ps);
        } catch (SQLException e) {
            throw new NegocioException("Erro ao listar vendas por produto.", e);
        } finally {
            TransactionManager.liberar(conexao);
        }
    }

    @Override
    public List<Venda> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        String sql = "SELECT * FROM venda WHERE data_venda BETWEEN ? AND ? ORDER BY data_venda";
        Connection conexao = TransactionManager.getConnection();
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(inicio));
            ps.setTimestamp(2, Timestamp.valueOf(fim));
            return mapearLista(ps);
        } catch (SQLException e) {
            throw new NegocioException("Erro ao listar vendas por período.", e);
        } finally {
            TransactionManager.liberar(conexao);
        }
    }

    @Override
    public List<Venda> listarPorStatus(StatusVenda status) {
        String sql = "SELECT * FROM venda WHERE status = ? ORDER BY data_venda DESC";
        Connection conexao = TransactionManager.getConnection();
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, status.name());
            return mapearLista(ps);
        } catch (SQLException e) {
            throw new NegocioException("Erro ao listar vendas por status.", e);
        } finally {
            TransactionManager.liberar(conexao);
        }
    }

    private List<Venda> mapearLista(PreparedStatement ps) throws SQLException {
        List<Venda> vendas = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                vendas.add(mapear(rs));
            }
        }
        return vendas;
    }

    private Venda mapear(ResultSet rs) throws SQLException {
        Cliente cliente = clienteDAO.buscarPorId(rs.getInt("cliente_id"));
        Venda venda = new Venda();
        venda.setId(rs.getInt("id"));
        venda.setCliente(cliente);
        venda.setDataVenda(rs.getTimestamp("data_venda").toLocalDateTime());
        venda.setStatus(StatusVenda.valueOf(rs.getString("status")));
        venda.setTotalBruto(rs.getDouble("total_bruto"));
        venda.setDesconto(rs.getDouble("desconto"));
        venda.setTotalLiquido(rs.getDouble("total_liquido"));
        venda.setItens(buscarItens(venda.getId()));
        return venda;
    }

    private List<ItemVenda> buscarItens(int vendaId) {
        String sql = "SELECT * FROM item_venda WHERE venda_id = ?";
        List<ItemVenda> itens = new ArrayList<>();
        Connection conexao = TransactionManager.getConnection();
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, vendaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Produto produto = produtoDAO.buscarPorId(rs.getInt("produto_id"));
                    ItemVenda item = new ItemVenda();
                    item.setId(rs.getInt("id"));
                    item.setProduto(produto);
                    item.setPrecoUnitario(rs.getDouble("preco_unitario"));
                    item.setQuantidade(rs.getInt("quantidade"));
                    item.setSubtotal(rs.getDouble("subtotal"));
                    itens.add(item);
                }
            }
        } catch (SQLException e) {
            throw new NegocioException("Erro ao buscar itens da venda.", e);
        } finally {
            TransactionManager.liberar(conexao);
        }
        return itens;
    }
}
