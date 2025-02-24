package com.empresa.clientapp.controller;

import com.empresa.clientapp.model.OrdemCompra;
import com.empresa.clientapp.service.OrdemCompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ordens-compra")
public class OrdemCompraController {

    @Autowired
    private OrdemCompraService ordemCompraService;

    @PostMapping
    public ResponseEntity<OrdemCompra> criarOrdemCompra(@RequestBody OrdemCompra ordemCompra) {
        OrdemCompra novaOrdem = ordemCompraService.salvarOrdemCompra(ordemCompra);
        return ResponseEntity.ok(novaOrdem);
    }

    @GetMapping
    public ResponseEntity<List<OrdemCompra>> listarOrdens() {
        return ResponseEntity.ok(ordemCompraService.listarTodas());
    }
}


