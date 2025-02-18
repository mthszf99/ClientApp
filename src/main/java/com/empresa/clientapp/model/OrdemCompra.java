package com.empresa.clientapp.model;

import com.empresa.clientapp.model.ItemOrdemCompra;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
public class OrdemCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    private LocalDate data;
    private BigDecimal desconto;
    private String observacao;
    private BigDecimal total;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ordemCompra")
    private List<ItemOrdemCompra> itens;


    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public List<ItemOrdemCompra> getItens() { return itens; }
    public void setItens(List<ItemOrdemCompra> itens) { this.itens = itens; }

    public BigDecimal getDesconto() { return desconto; }
    public void setDesconto(BigDecimal desconto) { this.desconto = desconto; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}


