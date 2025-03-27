package com.empresa.clientapp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "itens_venda")
public class ItemVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "venda_id")
    @JsonBackReference
    private Venda venda;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private BigDecimal valorUnitario;

    @Column(nullable = false)
    private BigDecimal subtotal;

    // Construtor padrão
    public ItemVenda() {
        this.quantidade = 0;
        this.valorUnitario = BigDecimal.ZERO;
        this.subtotal = BigDecimal.ZERO;
    }

    // Construtor com parâmetros
    public ItemVenda(Produto produto, Integer quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
        if (produto != null && produto.getValorVenda() != null) {
            this.valorUnitario = produto.getValorVenda();
        } else {
            this.valorUnitario = BigDecimal.ZERO;
        }
        calcularSubtotal();
    }

    // Método para calcular o subtotal com verificações adicionais
    public void calcularSubtotal() {
        if (this.quantidade == null || this.quantidade <= 0) {
            this.quantidade = 1; // Valor padrão
        }

        if (this.valorUnitario == null) {
            if (this.produto != null && this.produto.getValorVenda() != null) {
                this.valorUnitario = this.produto.getValorVenda();
            } else {
                this.valorUnitario = BigDecimal.ZERO;
            }
        }

        this.subtotal = this.valorUnitario.multiply(BigDecimal.valueOf(this.quantidade));

        // Log para debug
        System.out.println("Calculando subtotal: " + this.valorUnitario + " * " + this.quantidade + " = " + this.subtotal);
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
        if (produto != null && produto.getValorVenda() != null) {
            this.valorUnitario = produto.getValorVenda();
            calcularSubtotal();
        }
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
        calcularSubtotal();
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
        calcularSubtotal();
    }

    public BigDecimal getSubtotal() {
        // Calcular se for nulo
        if (this.subtotal == null) {
            calcularSubtotal();
        }
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public String toString() {
        return "ItemVenda{" +
                "id=" + id +
                ", produto=" + (produto != null ? produto.getNome() : "null") +
                ", quantidade=" + quantidade +
                ", valorUnitario=" + valorUnitario +
                ", subtotal=" + subtotal +
                '}';
    }
}
