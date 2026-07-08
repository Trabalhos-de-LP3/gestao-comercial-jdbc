package service;

import dao.ClienteDAO;
import exception.NegocioException;
import model.Cliente;

import java.util.List;
import java.util.regex.Pattern;

public class ClienteService {

    private static final Pattern REGEX_EMAIL =
            Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");

    private static final Pattern REGEX_CPF =
            Pattern.compile("^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$");

    private final ClienteDAO dao;

    public ClienteService(ClienteDAO dao) {
        this.dao = dao;
    }

    public void cadastrar(Cliente cliente) {
        validar(cliente);
        if (dao.existeCpf(cliente.getCpf())) {
            throw new NegocioException("Já existe um cliente com o CPF informado.");
        }
        dao.inserir(cliente);
    }

    public void atualizar(Cliente cliente) {
        validar(cliente);
        dao.atualizar(cliente);
    }

    public void inativar(int id) {
        Cliente cliente = buscarPorId(id);
        cliente.inativar();
        dao.atualizar(cliente);
    }

    public void ativar(int id) {
        Cliente cliente = buscarPorId(id);
        cliente.ativar();
        dao.atualizar(cliente);
    }

    public Cliente buscarPorId(int id) {
        Cliente cliente = dao.buscarPorId(id);
        if (cliente == null) {
            throw new NegocioException("Cliente com ID " + id + " não encontrado.");
        }
        return cliente;
    }

    public List<Cliente> listarTodos() {
        return dao.listarTodos();
    }

    public List<Cliente> listarAtivos() {
        return dao.listarPorStatus(true);
    }

    private void validar(Cliente cliente) {
        if (cliente.getNome() == null || cliente.getNome().isBlank()) {
            throw new NegocioException("Nome do cliente é obrigatório.");
        }
        if (!REGEX_CPF.matcher(cliente.getCpf()).matches()) {
            throw new NegocioException("CPF inválido.");
        }
        if (!REGEX_EMAIL.matcher(cliente.getEmail()).matches()) {
            throw new NegocioException("E-mail inválido.");
        }
    }
}
