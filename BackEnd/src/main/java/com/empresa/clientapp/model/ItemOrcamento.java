package com.empresa.clientapp.model;

public class ItemOrcamento {
    private String descricao;
    private double valor;


    public ItemOrcamento(String descricao, double valor) {
        this.descricao = descricao;
        this.valor = valor;
    }

    // Getters e Setters
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}

