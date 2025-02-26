package com.empresa.clientapp.controller;

import com.empresa.clientapp.model.ItemVenda;
import com.empresa.clientapp.model.Venda;
import com.empresa.clientapp.service.VendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/vendas")
public class VendaController {

    @Autowired
    private VendaService vendaService;

    @GetMapping
    public List<Venda> getAllVendas() {
        return vendaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venda> getVendaById(@PathVariable Long id) {
        Optional<Venda> venda = vendaService.findById(id);
        return venda.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Venda> getVendasByCliente(@PathVariable Long clienteId) {
        return vendaService.findByClienteId(clienteId);
    }

    @GetMapping("/periodo")
    public List<Venda> getVendasByPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return vendaService.findByPeriodo(inicio, fim);
    }

    @GetMapping("/produto/{produtoId}")
    public List<Venda> getVendasByProduto(@PathVariable Long produtoId) {
        return vendaService.findByProdutoId(produtoId);
    }

    @PostMapping
    public ResponseEntity<Venda> createVenda(@Valid @RequestBody Venda venda) {
        Venda novaVenda = vendaService.save(venda);
        return ResponseEntity.ok(novaVenda);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Venda> updateVenda(@PathVariable Long id, @Valid @RequestBody Venda venda) {
        if (!vendaService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        venda.setId(id);
        Venda vendaAtualizada = vendaService.save(venda);
        return ResponseEntity.ok(vendaAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenda(@PathVariable Long id) {
        if (!vendaService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        vendaService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/itens")
    public ResponseEntity<Venda> adicionarItem(@PathVariable Long id, @RequestBody ItemVenda item) {
        try {
            Venda venda = vendaService.adicionarItem(id, item);
            return ResponseEntity.ok(venda);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{vendaId}/itens/{itemId}")
    public ResponseEntity<Venda> removerItem(@PathVariable Long vendaId, @PathVariable Long itemId) {
        try {
            Venda venda = vendaService.removerItem(vendaId, itemId);
            return ResponseEntity.ok(venda);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/finalizar")
    public ResponseEntity<Venda> finalizarVenda(@PathVariable Long id) {
        try {
            Venda venda = vendaService.finalizarVenda(id);
            return ResponseEntity.ok(venda);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
