package com.empresa.clientapp.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vendas")
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(nullable = false)
    private LocalDateTime dataVenda;

    @Column(nullable = false)
    private BigDecimal valorTotal = BigDecimal.ZERO; // Inicializar com ZERO

    private BigDecimal desconto = BigDecimal.ZERO; // Inicializar com ZERO

    private String formaPagamento;

    private String observacoes;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<ItemVenda> itens = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (this.dataVenda == null) {
            this.dataVenda = LocalDateTime.now();
        }
        // Garantir que o valorTotal seja calculado antes de salvar
        recalcularTotal();
    }

    // Método para adicionar item à venda
    public void adicionarItem(ItemVenda item) {
        if (item == null) return;

        // Verificar se o item já tem o subtotal calculado
        if (item.getSubtotal() == null) {
            item.calcularSubtotal();
        }

        itens.add(item);
        item.setVenda(this);
        recalcularTotal();
    }

    // Método para remover item da venda
    public void removerItem(ItemVenda item) {
        if (itens.remove(item)) {
            item.setVenda(null);
            recalcularTotal();
        }
    }

    // Método para calcular o total da venda com verificações adicionais
    public void recalcularTotal() {
        BigDecimal total = BigDecimal.ZERO;

        // Log para debug
        System.out.println("Recalculando total da venda #" + id + " com " + itens.size() + " itens");

        for (ItemVenda item : itens) {
            // Garantir que o subtotal está calculado
            if (item.getSubtotal() == null) {
                item.calcularSubtotal();
            }

            // Log para debug
            System.out.println("Item: " + item.getProduto().getNome() + " - Subtotal: " + item.getSubtotal());

            if (item.getSubtotal() != null) {
                total = total.add(item.getSubtotal());
            }
        }

        // Aplicar desconto se existir
        if (desconto != null && desconto.compareTo(BigDecimal.ZERO) > 0) {
            if (desconto.compareTo(total) <= 0) {
                total = total.subtract(desconto);
            } else {
                // Se o desconto for maior que o total, apenas zerar o total
                desconto = total;
                total = BigDecimal.ZERO;
            }
        }

        // Log para debug
        System.out.println("Valor total calculado: " + total);

        this.valorTotal = total;
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

    public LocalDateTime getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(LocalDateTime dataVenda) {
        this.dataVenda = dataVenda;
    }

    public BigDecimal getValorTotal() {
        // Garantir que não seja nulo
        if (valorTotal == null) {
            recalcularTotal();
        }
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal != null ? valorTotal : BigDecimal.ZERO;
    }

    public BigDecimal getDesconto() {
        return desconto != null ? desconto : BigDecimal.ZERO;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto != null ? desconto : BigDecimal.ZERO;
        recalcularTotal();
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public List<ItemVenda> getItens() {
        return itens;
    }

    public void setItens(List<ItemVenda> itens) {
        this.itens = itens != null ? itens : new ArrayList<>();
        // Atualizar a referência da venda em cada item
        for (ItemVenda item : this.itens) {
            item.setVenda(this);
        }
        recalcularTotal();
    }
}
