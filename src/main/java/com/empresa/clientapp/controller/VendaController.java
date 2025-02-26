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
        List<Venda> vendas = vendaService.findAll();

        // Garantir que os valores totais e itens estão atualizados
        vendas.forEach(Venda::recalcularTotal);

        return vendas;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venda> getVendaById(@PathVariable Long id) {
        Optional<Venda> vendaOpt = vendaService.findById(id);

        if (!vendaOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Venda venda = vendaOpt.get();
        // Garantir que o total está atualizado
        venda.recalcularTotal();

        return ResponseEntity.ok(venda);
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Venda> getVendasByCliente(@PathVariable Long clienteId) {
        List<Venda> vendas = vendaService.findByClienteId(clienteId);
        vendas.forEach(Venda::recalcularTotal);
        return vendas;
    }

    @GetMapping("/periodo")
    public List<Venda> getVendasByPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        List<Venda> vendas = vendaService.findByPeriodo(inicio, fim);
        vendas.forEach(Venda::recalcularTotal);
        return vendas;
    }

    @GetMapping("/produto/{produtoId}")
    public List<Venda> getVendasByProduto(@PathVariable Long produtoId) {
        List<Venda> vendas = vendaService.findByProdutoId(produtoId);
        vendas.forEach(Venda::recalcularTotal);
        return vendas;
    }

    @PostMapping
    public ResponseEntity<Venda> createVenda(@Valid @RequestBody Venda venda) {
        try {
            // Garantir que temos uma data de venda
            if (venda.getDataVenda() == null) {
                venda.setDataVenda(LocalDateTime.now());
            }

            Venda novaVenda = vendaService.save(venda);
            return ResponseEntity.ok(novaVenda);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Venda> updateVenda(@PathVariable Long id, @Valid @RequestBody Venda venda) {
        if (!vendaService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        venda.setId(id);
        try {
            Venda vendaAtualizada = vendaService.save(venda);
            return ResponseEntity.ok(vendaAtualizada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
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
