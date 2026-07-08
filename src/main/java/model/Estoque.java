package model;

import exception.EstoqueInsuficienteException;
import exception.NegocioException;

import java.time.LocalDateTime;

public class Estoque {

    private int id;
    private Produto produto;
    private int quantidadeDisponivel;
    private int quantidadeMinima;
    private String localizacao;
    private LocalDateTime ultimaAtualizacao;

    public Estoque() {
    }

    public Estoque(Produto produto, int quantidadeDisponivel,
                   int quantidadeMinima, String localizacao) {
        this.produto = produto;
        this.quantidadeMinima = quantidadeMinima;
        this.localizacao = localizacao;
        this.ultimaAtualizacao = LocalDateTime.now();
        setQuantidadeDisponivel(quantidadeDisponivel);
    }

    public void registrarEntrada(int quantidade) {
        if (quantidade <= 0) {
            throw new NegocioException("Quantidade de entrada deve ser maior que zero.");
        }
        this.quantidadeDisponivel += quantidade;
        this.ultimaAtualizacao = LocalDateTime.now();
    }

    public void registrarSaida(int quantidade) {
        if (quantidade <= 0) {
            throw new NegocioException("Quantidade de saída deve ser maior que zero.");
        }
        if (quantidade > quantidadeDisponivel) {
            throw new EstoqueInsuficienteException(
                    produto.getId(), quantidade, quantidadeDisponivel);
        }
        this.quantidadeDisponivel -= quantidade;
        this.ultimaAtualizacao = LocalDateTime.now();
    }

    public boolean estaBaixoDoMinimo() {
        return quantidadeDisponivel < quantidadeMinima;
    }

    public boolean temDisponibilidade(int quantidade) {
        return quantidadeDisponivel >= quantidade;
    }

    public int getId() {
        return id;
    }

    public Produto getProduto() {
        return produto;
    }

    public int getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public int getQuantidadeMinima() {
        return quantidadeMinima;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public LocalDateTime getUltimaAtualizacao() {
        return ultimaAtualizacao;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public void setQuantidadeDisponivel(int quantidadeDisponivel) {
        if (quantidadeDisponivel < 0) {
            throw new NegocioException("Estoque não pode ser negativo.");
        }
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public void setQuantidadeMinima(int quantidadeMinima) {
        this.quantidadeMinima = quantidadeMinima;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public void setUltimaAtualizacao(LocalDateTime ultimaAtualizacao) {
        this.ultimaAtualizacao = ultimaAtualizacao;
    }

    @Override
    public String toString() {
        return "Estoque [" + produto.getNome() + "] | Disponível: "
                + quantidadeDisponivel + " | Mínimo: " + quantidadeMinima;
    }
}
