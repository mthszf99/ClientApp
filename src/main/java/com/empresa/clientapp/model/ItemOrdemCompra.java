package com.empresa.clientapp.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class ItemOrdemCompra {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String descricao;
        private int quantidade;
        private BigDecimal valor;

        @ManyToOne
        @JoinColumn(name = "ordem_compra_id")
        private OrdemCompra ordemCompra;

        @ManyToOne
        @JoinColumn(name = "produto_id", nullable = true)
        private Produto produto;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
}


