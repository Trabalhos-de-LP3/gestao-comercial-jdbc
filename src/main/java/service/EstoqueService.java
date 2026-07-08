package service;

import dao.EstoqueDAO;
import exception.NegocioException;
import model.Estoque;

import java.util.List;

public class EstoqueService {

    private final EstoqueDAO dao;

    public EstoqueService(EstoqueDAO dao) {
        this.dao = dao;
    }

    public void registrarEntrada(int produtoId, int quantidade) {
        Estoque estoque = buscarPorProduto(produtoId);
        estoque.registrarEntrada(quantidade);
        dao.atualizar(estoque);
    }

    public void registrarSaida(int produtoId, int quantidade) {
        Estoque estoque = buscarPorProduto(produtoId);
        estoque.registrarSaida(quantidade);
        dao.atualizar(estoque);
    }

    public Estoque buscarPorProduto(int produtoId) {
        Estoque estoque = dao.buscarPorProduto(produtoId);
        if (estoque == null) {
            throw new NegocioException("Estoque não encontrado para o produto informado.");
        }
        return estoque;
    }

    public List<Estoque> listarAbaixoDoMinimo() {
        return dao.listarAbaixoDoMinimo();
    }

    public List<Estoque> listarTodos() {
        return dao.listarTodos();
    }
}
