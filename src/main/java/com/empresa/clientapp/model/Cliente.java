package com.empresa.clientapp.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;


@Entity
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Nome é obrigatório")  // Tornar o campo nome obrigatório
    private String nome;

    private String endereco;
    private String tipoPessoa; // Físico ou Jurídico
    private String indicacao;
    private String cpf;
    private String cnpj;
    private String ie;

    // Construtores
    public Cliente() {}

    public Cliente(String nome, String endereco, String tipoPessoa, String indicacao, String cpf, String cnpj, String ie) {
        this.nome = nome;
        this.endereco = endereco;
        this.tipoPessoa = tipoPessoa;
        this.indicacao = indicacao;
        this.cpf = cpf;
        this.cnpj = cnpj;
        this.ie = ie;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(String tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public String getIndicacao() {
        return indicacao;
    }

    public void setIndicacao(String indicacao) {
        this.indicacao = indicacao;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getIe() {
        return ie;
    }

    public void setIe(String ie) {
        this.ie = ie;
    }
}

