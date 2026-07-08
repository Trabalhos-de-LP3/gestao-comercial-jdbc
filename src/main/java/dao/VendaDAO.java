package dao;

import model.StatusVenda;
import model.Venda;

import java.time.LocalDateTime;
import java.util.List;

public interface VendaDAO {

    void inserir(Venda venda);

    void atualizarStatus(Venda venda);

    Venda buscarPorId(int id);

    List<Venda> listarPorCliente(int clienteId);

    List<Venda> listarPorProduto(int produtoId);

    List<Venda> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim);

    List<Venda> listarPorStatus(StatusVenda status);
}
