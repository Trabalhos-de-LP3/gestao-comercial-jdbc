import dao.ClienteDAO;
import dao.EstoqueDAO;
import dao.ProdutoDAO;
import dao.VendaDAO;
import dao.jdbc.ClienteDAOJdbc;
import dao.jdbc.EstoqueDAOJdbc;
import dao.jdbc.ProdutoDAOJdbc;
import dao.jdbc.VendaDAOJdbc;
import model.Cliente;
import model.Estoque;
import model.Produto;
import model.StatusVenda;
import model.Venda;
import service.ClienteService;
import service.EstoqueService;
import service.ProdutoService;
import service.VendaService;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    private static final ClienteService clienteService;
    private static final ProdutoService produtoService;
    private static final EstoqueService estoqueService;
    private static final VendaService vendaService;

    static {
        ClienteDAO clienteDAO = new ClienteDAOJdbc();
        ProdutoDAO produtoDAO = new ProdutoDAOJdbc();
        EstoqueDAO estoqueDAO = new EstoqueDAOJdbc();
        VendaDAO vendaDAO = new VendaDAOJdbc();

        clienteService = new ClienteService(clienteDAO);
        estoqueService = new EstoqueService(estoqueDAO);
        produtoService = new ProdutoService(produtoDAO, estoqueDAO);
        vendaService = new VendaService(vendaDAO, estoqueService);
    }

    public static void main(String[] args) {
        System.out.println("SISTEMA DE GESTÃO COMERCIAL (JDBC)");
        boolean executando = true;
        while (executando) {
            System.out.println();
            System.out.println("=== MENU PRINCIPAL ===");
            System.out.println("1 - Clientes");
            System.out.println("2 - Produtos");
            System.out.println("3 - Estoque");
            System.out.println("4 - Vendas");
            System.out.println("5 - Consultas");
            System.out.println("0 - Sair");
            int opcao = lerInteiro("Opção: ");
            try {
                switch (opcao) {
                    case 1 -> menuClientes();
                    case 2 -> menuProdutos();
                    case 3 -> menuEstoque();
                    case 4 -> menuVendas();
                    case 5 -> menuConsultas();
                    case 0 -> executando = false;
                    default -> System.out.println("Opção inválida.");
                }
            } catch (RuntimeException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
        System.out.println("Encerrando.");
    }

    private static void menuClientes() {
        System.out.println("--- CLIENTES ---");
        System.out.println("1 - Cadastrar");
        System.out.println("2 - Listar todos");
        System.out.println("3 - Listar ativos");
        System.out.println("4 - Inativar");
        System.out.println("5 - Ativar");
        System.out.println("0 - Voltar");
        int opcao = lerInteiro("Opção: ");
        switch (opcao) {
            case 1 -> {
                Cliente cliente = new Cliente(
                        lerTexto("Nome: "),
                        lerTexto("CPF (000.000.000-00): "),
                        lerTexto("E-mail: "),
                        lerTexto("Telefone: "));
                clienteService.cadastrar(cliente);
                System.out.println("Cliente cadastrado com ID " + cliente.getId() + ".");
            }
            case 2 -> imprimirClientes(clienteService.listarTodos());
            case 3 -> imprimirClientes(clienteService.listarAtivos());
            case 4 -> {
                clienteService.inativar(lerInteiro("ID do cliente: "));
                System.out.println("Cliente inativado.");
            }
            case 5 -> {
                clienteService.ativar(lerInteiro("ID do cliente: "));
                System.out.println("Cliente ativado.");
            }
            case 0 -> {
            }
            default -> System.out.println("Opção inválida.");
        }
    }

    private static void menuProdutos() {
        System.out.println("--- PRODUTOS ---");
        System.out.println("1 - Cadastrar");
        System.out.println("2 - Listar todos");
        System.out.println("3 - Listar ativos");
        System.out.println("4 - Inativar");
        System.out.println("5 - Ativar");
        System.out.println("0 - Voltar");
        int opcao = lerInteiro("Opção: ");
        switch (opcao) {
            case 1 -> {
                Produto produto = new Produto(
                        lerTexto("Nome: "),
                        lerTexto("Descrição: "),
                        lerDouble("Preço de venda: "),
                        lerDouble("Preço de custo: "),
                        lerTexto("Categoria: "));
                int quantidadeInicial = lerInteiro("Quantidade inicial em estoque: ");
                int quantidadeMinima = lerInteiro("Quantidade mínima: ");
                String localizacao = lerTexto("Localização: ");
                produtoService.cadastrar(produto, quantidadeInicial, quantidadeMinima, localizacao);
                System.out.println("Produto cadastrado com ID " + produto.getId() + ".");
            }
            case 2 -> imprimirProdutos(produtoService.listarTodos());
            case 3 -> imprimirProdutos(produtoService.listarAtivos());
            case 4 -> {
                produtoService.inativar(lerInteiro("ID do produto: "));
                System.out.println("Produto inativado.");
            }
            case 5 -> {
                produtoService.ativar(lerInteiro("ID do produto: "));
                System.out.println("Produto ativado.");
            }
            case 0 -> {
            }
            default -> System.out.println("Opção inválida.");
        }
    }

    private static void menuEstoque() {
        System.out.println("--- ESTOQUE ---");
        System.out.println("1 - Registrar entrada");
        System.out.println("2 - Listar todos");
        System.out.println("3 - Listar abaixo do mínimo");
        System.out.println("0 - Voltar");
        int opcao = lerInteiro("Opção: ");
        switch (opcao) {
            case 1 -> {
                int produtoId = lerInteiro("ID do produto: ");
                int quantidade = lerInteiro("Quantidade de entrada: ");
                estoqueService.registrarEntrada(produtoId, quantidade);
                System.out.println("Entrada registrada.");
            }
            case 2 -> imprimirEstoques(estoqueService.listarTodos());
            case 3 -> imprimirEstoques(estoqueService.listarAbaixoDoMinimo());
            case 0 -> {
            }
            default -> System.out.println("Opção inválida.");
        }
    }

    private static void menuVendas() {
        System.out.println("--- VENDAS ---");
        System.out.println("1 - Nova venda");
        System.out.println("2 - Cancelar venda");
        System.out.println("0 - Voltar");
        int opcao = lerInteiro("Opção: ");
        switch (opcao) {
            case 1 -> novaVenda();
            case 2 -> {
                vendaService.cancelarVenda(lerInteiro("ID da venda: "));
                System.out.println("Venda cancelada e estoque estornado.");
            }
            case 0 -> {
            }
            default -> System.out.println("Opção inválida.");
        }
    }

    private static void novaVenda() {
        Cliente cliente = clienteService.buscarPorId(lerInteiro("ID do cliente: "));
        Venda venda = vendaService.abrirVenda(cliente);

        boolean adicionando = true;
        while (adicionando) {
            int produtoId = lerInteiro("ID do produto (0 para finalizar): ");
            if (produtoId == 0) {
                adicionando = false;
            } else {
                try {
                    Produto produto = produtoService.buscarPorId(produtoId);
                    int quantidade = lerInteiro("Quantidade: ");
                    vendaService.adicionarItem(venda, produto, quantidade);
                    System.out.println("Item adicionado. Total parcial: R$ " + venda.getTotalBruto());
                } catch (RuntimeException e) {
                    System.out.println("Erro: " + e.getMessage());
                }
            }
        }

        if (venda.getItens().isEmpty()) {
            System.out.println("Venda sem itens, operação cancelada.");
            return;
        }

        double desconto = lerDouble("Desconto (0 se não houver): ");
        if (desconto > 0) {
            venda.aplicarDesconto(desconto);
        }

        vendaService.confirmarVenda(venda);
        System.out.println("Venda confirmada com ID " + venda.getId()
                + " | Total líquido: R$ " + venda.getTotalLiquido());
    }

    private static void menuConsultas() {
        System.out.println("--- CONSULTAS ---");
        System.out.println("1 - Venda por ID");
        System.out.println("2 - Vendas por cliente");
        System.out.println("3 - Vendas por produto");
        System.out.println("4 - Vendas por status");
        System.out.println("5 - Vendas por período");
        System.out.println("0 - Voltar");
        int opcao = lerInteiro("Opção: ");
        switch (opcao) {
            case 1 -> System.out.println(vendaService.buscarPorId(lerInteiro("ID da venda: ")));
            case 2 -> imprimirVendas(vendaService.listarPorCliente(lerInteiro("ID do cliente: ")));
            case 3 -> imprimirVendas(vendaService.listarPorProduto(lerInteiro("ID do produto: ")));
            case 4 -> imprimirVendas(vendaService.listarPorStatus(lerStatus()));
            case 5 -> {
                LocalDate inicio = LocalDate.parse(lerTexto("Data inicial (AAAA-MM-DD): "));
                LocalDate fim = LocalDate.parse(lerTexto("Data final (AAAA-MM-DD): "));
                imprimirVendas(vendaService.listarPorPeriodo(
                        inicio.atStartOfDay(), fim.atTime(23, 59, 59)));
            }
            case 0 -> {
            }
            default -> System.out.println("Opção inválida.");
        }
    }

    private static StatusVenda lerStatus() {
        System.out.println("Status disponíveis: ABERTA, CONFIRMADA, CANCELADA, FATURADA");
        return StatusVenda.valueOf(lerTexto("Status: ").trim().toUpperCase());
    }

    private static void imprimirClientes(List<Cliente> clientes) {
        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente encontrado.");
            return;
        }
        clientes.forEach(System.out::println);
    }

    private static void imprimirProdutos(List<Produto> produtos) {
        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto encontrado.");
            return;
        }
        produtos.forEach(System.out::println);
    }

    private static void imprimirEstoques(List<Estoque> estoques) {
        if (estoques.isEmpty()) {
            System.out.println("Nenhum estoque encontrado.");
            return;
        }
        estoques.forEach(System.out::println);
    }

    private static void imprimirVendas(List<Venda> vendas) {
        if (vendas.isEmpty()) {
            System.out.println("Nenhuma venda encontrada.");
            return;
        }
        vendas.forEach(venda -> {
            System.out.println(venda);
            System.out.println("------------------------");
        });
    }

    private static String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine();
    }

    private static int lerInteiro(String mensagem) {
        System.out.print(mensagem);
        return Integer.parseInt(scanner.nextLine().trim());
    }

    private static double lerDouble(String mensagem) {
        System.out.print(mensagem);
        return Double.parseDouble(scanner.nextLine().trim().replace(",", "."));
    }
}
