package com.empresa.clientapp.controller;

import com.empresa.clientapp.model.Fornecedor;
import com.empresa.clientapp.service.FornecedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/fornecedores")
public class FornecedorController {

    @Autowired
    private FornecedorService fornecedorService;

    @GetMapping
    public List<Fornecedor> getAllFornecedores() {
        return fornecedorService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Fornecedor> getFornecedorById(@PathVariable Long id) {
        return fornecedorService.findById(id);
    }

    @PostMapping
    public Fornecedor createFornecedor(@Valid @RequestBody Fornecedor fornecedor) {
        return fornecedorService.save(fornecedor);
    }

    @PutMapping("/{id}")
    public Fornecedor updateFornecedor(@PathVariable Long id, @Valid @RequestBody Fornecedor fornecedor) {
        if (fornecedorService.findById(id).isPresent()) {
            fornecedor.setId(id);
            return fornecedorService.save(fornecedor);
        }
        return null; // Ou lançar uma exceção apropriada
    }

    @DeleteMapping("/{id}")
    public void deleteFornecedor(@PathVariable Long id) {
        fornecedorService.deleteById(id);
    }
}

