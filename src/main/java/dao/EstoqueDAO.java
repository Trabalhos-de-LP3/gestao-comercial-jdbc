package dao;

import model.Estoque;
import java.util.List;

public interface EstoqueDAO {

    void inserir(Estoque estoque);

    void atualizar(Estoque estoque);

    Estoque buscarPorProduto(int produtoId);

    List<Estoque> listarTodos();

    List<Estoque> listarAbaixoDoMinimo();
}
