package com.empresa.clientapp.model;

import com.empresa.clientapp.model.ItemOrdemCompra;
import java.util.List;
import java.time.LocalDate;
import java.util.List;

public class OrdemCompra {
    private Long id;
    private Cliente cliente;
    private LocalDate data;
    private List<ItemOrdemCompra> itens;
    private double desconto;
    private String observacao;
    private double total;

    public OrdemCompra() {
    }

    public OrdemCompra(Long id, Cliente cliente, LocalDate data, List<ItemOrdemCompra> itens, double desconto, String observacao) {
        this.id = id;
        this.cliente = cliente;
        this.data = data;
        this.itens = itens;
        this.desconto = desconto;
        this.observacao = observacao;
        this.total = calcularTotal();
    }

    public double calcularTotal() {
        double totalItens = itens.stream().mapToDouble(ItemOrdemCompra::calcularSubtotal).sum();
        return totalItens - desconto;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public List<ItemOrdemCompra> getItens() { return itens; }
    public void setItens(List<ItemOrdemCompra> itens) { this.itens = itens; }

    public double getDesconto() { return desconto; }
    public void setDesconto(double desconto) { this.desconto = desconto; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}


