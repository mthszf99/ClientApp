package com.empresa.clientapp.DTO;

import java.math.BigDecimal;

public class ProdutoMaisVendidoDTO {
    private Long produtoId;
    private String nomeProduto;
    private Long quantidadeVendida;
    private BigDecimal valorTotal;

    public ProdutoMaisVendidoDTO() {
    }

    public ProdutoMaisVendidoDTO(Long produtoId, String nomeProduto, Long quantidadeVendida, BigDecimal valorTotal) {
        this.produtoId = produtoId;
        this.nomeProduto = nomeProduto;
        this.quantidadeVendida = quantidadeVendida;
        this.valorTotal = valorTotal;
    }

    // Getters e Setters
    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public Long getQuantidadeVendida() {
        return quantidadeVendida;
    }

    public void setQuantidadeVendida(Long quantidadeVendida) {
        this.quantidadeVendida = quantidadeVendida;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }
}
