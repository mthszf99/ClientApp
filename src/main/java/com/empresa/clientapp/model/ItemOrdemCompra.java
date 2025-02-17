package com.empresa.clientapp.model;

public class ItemOrdemCompra {
    private Long id;
    private Produto produto;
    private String descricaoServico; // Apenas se for serviço
    private int quantidade;
    private double valor;

    public ItemOrdemCompra() {
    }

    public ItemOrdemCompra(Long id, Produto produto, String descricaoServico, int quantidade, double valor) {
        this.id = id;
        this.produto = produto;
        this.descricaoServico = descricaoServico;
        this.quantidade = quantidade;
        this.valor = valor;
    }

    public double calcularSubtotal() {
        return quantidade * valor;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public String getDescricaoServico() { return descricaoServico; }
    public void setDescricaoServico(String descricaoServico) { this.descricaoServico = descricaoServico; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
}


