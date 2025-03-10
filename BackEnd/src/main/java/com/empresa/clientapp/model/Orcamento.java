package com.empresa.clientapp.model;

import java.time.LocalDate;
import java.util.List;

public class Orcamento {
    private String nomeCliente;
    private LocalDate data;
    private List<ItemOrcamento> itens;
    private double desconto;

    // Construtor
    public Orcamento(String nomeCliente, LocalDate data, List<ItemOrcamento> itens, double desconto) {
        this.nomeCliente = nomeCliente;
        this.data = data;
        this.itens = itens;
        this.desconto = desconto;
    }

    // Método para calcular o total
    public double calcularTotal() {
        double total = itens.stream().mapToDouble(ItemOrcamento::getValor).sum();
        return total - desconto;
    }

    // Getters e Setters
    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public List<ItemOrcamento> getItens() {
        return itens;
    }

    public void setItens(List<ItemOrcamento> itens) {
        this.itens = itens;
    }

    public double getDesconto() {
        return desconto;
    }

    public void setDesconto(double desconto) {
        this.desconto = desconto;
    }
}
