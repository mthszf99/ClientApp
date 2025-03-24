package com.empresa.clientapp.controller;

import com.empresa.clientapp.model.Produto;
import com.empresa.clientapp.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @GetMapping
    public List<Produto> getAllProdutos() {
        return produtoService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Produto> getProdutoById(@PathVariable Long id) {
        return produtoService.findById(id);
    }

    @PostMapping
    public Produto createProduto(@RequestBody Produto produto) {
        return produtoService.save(produto);
    }

    @PutMapping("/{id}")
    public Produto updateProduto(@PathVariable Long id, @RequestBody Produto produto) {
        if (produtoService.findById(id).isPresent()) {
            produto.setId(id);
            return produtoService.save(produto);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteProduto(@PathVariable Long id) {
        produtoService.deleteById(id);
    }
}

