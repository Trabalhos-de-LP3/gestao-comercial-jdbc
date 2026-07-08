package dao;

import model.Produto;
import java.util.List;

public interface ProdutoDAO {

    void inserir(Produto produto);

    void atualizar(Produto produto);

    Produto buscarPorId(int id);

    List<Produto> listarTodos();

    List<Produto> listarPorStatus(boolean ativo);

    List<Produto> listarPorCategoria(String categoria);

    void deletar(int id);
}
