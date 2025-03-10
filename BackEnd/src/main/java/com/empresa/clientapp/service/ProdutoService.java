package com.empresa.clientapp.service;

import com.empresa.clientapp.model.Produto;
import com.empresa.clientapp.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public List<Produto> findAll() {
        return produtoRepository.findAll();
    }

    public Optional<Produto> findById(Long id) {
        return produtoRepository.findById(id);
    }

    public Produto save(Produto produto) {
        return produtoRepository.save(produto);
    }

    public void deleteById(Long id) {
        produtoRepository.deleteById(id);
    }

    public void reduzirEstoque(Long id, int quantidadeVendida) {
        Optional<Produto> produtoOpt = produtoRepository.findById(id);
        if (produtoOpt.isPresent()) {
            Produto produto = produtoOpt.get();
            if (produto.getQuantidade() >= quantidadeVendida) {
                produto.setQuantidade(produto.getQuantidade() - quantidadeVendida);
                produtoRepository.save(produto);
            } else {
                throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
            }
        } else {
            throw new RuntimeException("Produto não encontrado.");
        }
    }
}
