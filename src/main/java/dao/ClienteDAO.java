package dao;

import model.Cliente;
import java.util.List;

public interface ClienteDAO {

    void inserir(Cliente cliente);

    void atualizar(Cliente cliente);

    Cliente buscarPorId(int id);

    List<Cliente> listarTodos();

    List<Cliente> listarPorStatus(boolean ativo);

    boolean existeCpf(String cpf);

    void deletar(int id);
}
