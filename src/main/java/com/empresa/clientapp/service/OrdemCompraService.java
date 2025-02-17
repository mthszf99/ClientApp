package com.empresa.clientapp.service;

import com.empresa.clientapp.model.OrdemCompra;
import com.empresa.clientapp.model.ItemOrdemCompra;
import com.empresa.clientapp.model.Produto;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrdemCompraService {

    private final ProdutoService produtoService;

    public OrdemCompraService(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    public void processarOrdemCompra(OrdemCompra ordemCompra) {
        for (ItemOrdemCompra item : ordemCompra.getItens()) {
            if (item.getProduto() != null) {
                Produto produto = produtoService.buscarPorId(item.getProduto().getId());
                if (produto != null) {
                    int novaQuantidade = produto.getQuantidade() - item.getQuantidade();
                    if (novaQuantidade < 0) {
                        throw new IllegalArgumentException("Quantidade insuficiente para o produto: " + produto.getNome());
                    }
                    produto.setQuantidade(novaQuantidade);
                    produtoService.atualizarProduto(produto);
                }
            }
        }
        ordemCompra.setTotal(ordemCompra.calcularTotal());
    }
}



