package com.empresa.clientapp.model;

import com.empresa.clientapp.model.Cliente;
import com.empresa.clientapp.model.ItemOrdemCompra;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "ordens_compra")
public class OrdemCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @OneToMany(mappedBy = "ordemCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemOrdemCompra> itens;

    private String observacao;

    private BigDecimal desconto;

    private BigDecimal total;

    public OrdemCompra() {
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<ItemOrdemCompra> getItens() {
        return itens;
    }

    public void setItens(List<ItemOrdemCompra> itens) {
        this.itens = itens;
        calcularTotal();
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
        calcularTotal();
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    private void calcularTotal() {
        BigDecimal soma = itens.stream()
                .map(ItemOrdemCompra::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (desconto != null) {
            soma = soma.subtract(desconto);
        }

        this.total = soma.max(BigDecimal.ZERO); // Evita valores negativos
    }
}


