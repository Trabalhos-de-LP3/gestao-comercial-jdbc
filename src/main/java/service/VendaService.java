package service;

import config.TransactionManager;
import dao.VendaDAO;
import exception.NegocioException;
import model.*;

import java.time.LocalDateTime;
import java.util.List;

public class VendaService {

    private final VendaDAO vendaDAO;
    private final EstoqueService estoqueService;

    public VendaService(VendaDAO vendaDAO, EstoqueService estoqueService) {
        this.vendaDAO = vendaDAO;
        this.estoqueService = estoqueService;
    }

    public Venda abrirVenda(Cliente cliente) {
        if (!cliente.podeComprar()) {
            throw new NegocioException("Cliente inativo não pode realizar compras.");
        }
        return new Venda(cliente);
    }

    public void adicionarItem(Venda venda, Produto produto, int quantidade) {
        if (!produto.podeSerVendido()) {
            throw new NegocioException("Produto inativo não pode ser vendido.");
        }
        Estoque estoque = estoqueService.buscarPorProduto(produto.getId());
        if (!estoque.temDisponibilidade(quantidade)) {
            throw new NegocioException("Estoque insuficiente para o produto " + produto.getNome());
        }
        venda.adicionarItem(new ItemVenda(produto, quantidade));
    }

    public void confirmarVenda(Venda venda) {
        if (venda.getItens().isEmpty()) {
            throw new NegocioException("Venda não possui itens.");
        }

        venda.confirmar();

        TransactionManager.executar(() -> {
            vendaDAO.inserir(venda);
            for (ItemVenda item : venda.getItens()) {
                estoqueService.registrarSaida(item.getProduto().getId(), item.getQuantidade());
            }
        });
    }

    public void cancelarVenda(int vendaId) {
        Venda venda = buscarPorId(vendaId);
        venda.cancelar();

        TransactionManager.executar(() -> {
            vendaDAO.atualizarStatus(venda);
            for (ItemVenda item : venda.getItens()) {
                estoqueService.registrarEntrada(item.getProduto().getId(), item.getQuantidade());
            }
        });
    }

    public Venda buscarPorId(int id) {
        Venda venda = vendaDAO.buscarPorId(id);
        if (venda == null) {
            throw new NegocioException("Venda com ID " + id + " não encontrada.");
        }
        return venda;
    }

    public List<Venda> listarPorCliente(int clienteId) {
        return vendaDAO.listarPorCliente(clienteId);
    }

    public List<Venda> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return vendaDAO.listarPorPeriodo(inicio, fim);
    }

    public List<Venda> listarPorStatus(StatusVenda status) {
        return vendaDAO.listarPorStatus(status);
    }
}
