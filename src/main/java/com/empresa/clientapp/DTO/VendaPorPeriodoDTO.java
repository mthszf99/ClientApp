package com.empresa.clientapp.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public class VendaPorPeriodoDTO {
    private LocalDate data;
    private Long quantidade;
    private BigDecimal valor;

    public VendaPorPeriodoDTO() {
    }

    public VendaPorPeriodoDTO(LocalDate data, Long quantidade, BigDecimal valor) {
        this.data = data;
        this.quantidade = quantidade;
        this.valor = valor;
    }

    // Getters e Setters
    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
