package com.empresa.clientapp.DTO;

import java.math.BigDecimal;
import java.util.List;

public class DashboardDTO {
    private Long totalVendas;
    private BigDecimal valorTotalVendas;
    private Long totalClientes;
    private Long totalProdutos;
    private Double ticketMedio;
    private List<ProdutoMaisVendidoDTO> produtosMaisVendidos;
    private List<VendaPorPeriodoDTO> vendasRecentes;

    // Getters e Setters
    public Long getTotalVendas() {
        return totalVendas;
    }

    public void setTotalVendas(Long totalVendas) {
        this.totalVendas = totalVendas;
    }

    public BigDecimal getValorTotalVendas() {
        return valorTotalVendas;
    }

    public void setValorTotalVendas(BigDecimal valorTotalVendas) {
        this.valorTotalVendas = valorTotalVendas;
    }

    public Long getTotalClientes() {
        return totalClientes;
    }

    public void setTotalClientes(Long totalClientes) {
        this.totalClientes = totalClientes;
    }

    public Long getTotalProdutos() {
        return totalProdutos;
    }

    public void setTotalProdutos(Long totalProdutos) {
        this.totalProdutos = totalProdutos;
    }

    public Double getTicketMedio() {
        return ticketMedio;
    }

    public void setTicketMedio(Double ticketMedio) {
        this.ticketMedio = ticketMedio;
    }

    public List<ProdutoMaisVendidoDTO> getProdutosMaisVendidos() {
        return produtosMaisVendidos;
    }

    public void setProdutosMaisVendidos(List<ProdutoMaisVendidoDTO> produtosMaisVendidos) {
        this.produtosMaisVendidos = produtosMaisVendidos;
    }

    public List<VendaPorPeriodoDTO> getVendasRecentes() {
        return vendasRecentes;
    }

    public void setVendasRecentes(List<VendaPorPeriodoDTO> vendasRecentes) {
        this.vendasRecentes = vendasRecentes;
    }
}

