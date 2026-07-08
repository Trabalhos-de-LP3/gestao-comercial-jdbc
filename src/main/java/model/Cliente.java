package model;

import java.time.LocalDateTime;

public class Cliente {

    private int id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private boolean ativo;
    private LocalDateTime dataCadastro;

    public Cliente() {
        this.ativo = true;
        this.dataCadastro = LocalDateTime.now();
    }

    public Cliente(String nome, String cpf, String email, String telefone) {
        this();
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
    }

    public boolean podeComprar() {
        return ativo;
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

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + nome + " | CPF: " + cpf + " | "
                + (ativo ? "ATIVO" : "INATIVO");
    }
}
