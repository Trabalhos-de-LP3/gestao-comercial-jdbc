package service;

import dao.EstoqueDAO;
import dao.ProdutoDAO;
import exception.NegocioException;
import model.Estoque;
import model.Produto;

import java.util.List;

public class ProdutoService {

    private final ProdutoDAO produtoDAO;
    private final EstoqueDAO estoqueDAO;

    public ProdutoService(ProdutoDAO produtoDAO, EstoqueDAO estoqueDAO) {
        this.produtoDAO = produtoDAO;
        this.estoqueDAO = estoqueDAO;
    }

    public void cadastrar(Produto produto, int quantidadeInicial,
                          int quantidadeMinima, String localizacao) {
        validar(produto);
        produtoDAO.inserir(produto);
        Estoque estoque = new Estoque(produto, quantidadeInicial,
                quantidadeMinima, localizacao);
        estoqueDAO.inserir(estoque);
    }

    public void atualizar(Produto produto) {
        validar(produto);
        produtoDAO.atualizar(produto);
    }

    public void inativar(int id) {
        Produto produto = buscarPorId(id);
        produto.inativar();
        produtoDAO.atualizar(produto);
    }

    public void ativar(int id) {
        Produto produto = buscarPorId(id);
        produto.ativar();
        produtoDAO.atualizar(produto);
    }

    public Produto buscarPorId(int id) {
        Produto produto = produtoDAO.buscarPorId(id);
        if (produto == null) {
            throw new NegocioException("Produto com ID " + id + " não encontrado.");
        }
        return produto;
    }

    public List<Produto> listarTodos() {
        return produtoDAO.listarTodos();
    }

    public List<Produto> listarAtivos() {
        return produtoDAO.listarPorStatus(true);
    }

    private void validar(Produto produto) {
        if (produto.getNome() == null || produto.getNome().isBlank()) {
            throw new NegocioException("Nome do produto é obrigatório.");
        }
        if (produto.getPrecoVenda() <= 0) {
            throw new NegocioException("Preço de venda deve ser maior que zero.");
        }
        if (produto.getPrecoCusto() > produto.getPrecoVenda()) {
            throw new NegocioException("Preço de custo não pode ser maior que o preço de venda.");
        }
    }
}
