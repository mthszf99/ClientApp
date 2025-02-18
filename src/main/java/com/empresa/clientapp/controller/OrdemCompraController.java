package com.empresa.clientapp.controller;

import com.empresa.clientapp.model.OrdemCompra;
import com.empresa.clientapp.service.OrdemCompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ordens-compra")
public class OrdemCompraController {

    @Autowired
    private OrdemCompraService ordemCompraService;

    @PostMapping
    public ResponseEntity<?> salvarOrdemCompra(@RequestBody OrdemCompra ordemCompra) {
        try {
            byte[] pdfBytes = ordemCompraService.salvarOrdemCompra(ordemCompra);
            return ResponseEntity.ok(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar a ordem de compra: " + e.getMessage());
        }
    }
}


