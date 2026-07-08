package model;

import exception.NegocioException;

public class Produto {

    private int id;
    private String nome;
    private String descricao;
    private double precoVenda;
    private double precoCusto;
    private String categoria;
    private boolean ativo;

    public Produto() {
        this.ativo = true;
    }

    public Produto(String nome, String descricao,
                   double precoVenda, double precoCusto, String categoria) {
        this();
        this.nome = nome;
        this.descricao = descricao;
        this.categoria = categoria;
        setPrecoVenda(precoVenda);
        setPrecoCusto(precoCusto);
    }

    public boolean podeSerVendido() {
        return ativo;
    }

    public double calcularMargemLucro() {
        if (precoCusto == 0) {
            return 0;
        }
        return ((precoVenda - precoCusto) / precoCusto) * 100;
    }

    public void inativar() {
        this.ativo = false;
    }

    public void ativar() {
        this.ativo = true;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getPrecoVenda() {
        return precoVenda;
    }

    public double getPrecoCusto() {
        return precoCusto;
    }

    public String getCategoria() {
        return categoria;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setPrecoVenda(double precoVenda) {
        if (precoVenda <= 0) {
            throw new NegocioException("Preço de venda deve ser maior que zero.");
        }
        this.precoVenda = precoVenda;
    }

    public void setPrecoCusto(double precoCusto) {
        if (precoCusto < 0) {
            throw new NegocioException("Preço de custo não pode ser negativo.");
        }
        if (precoCusto > precoVenda) {
            throw new NegocioException("Preço de custo não pode ser maior que o preço de venda.");
        }
        this.precoCusto = precoCusto;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + nome + " | R$ " + precoVenda + " | "
                + categoria + " | " + (ativo ? "ATIVO" : "INATIVO");
    }
}
