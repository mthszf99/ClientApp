package com.empresa.clientapp.controller;

import com.empresa.clientapp.model.OrdemCompra;
import com.empresa.clientapp.service.OrdemCompraService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/ordens-compra")
public class OrdemCompraController {

    private final OrdemCompraService ordemCompraService = new OrdemCompraService();

    @PostMapping("/gerar-pdf")
    public ResponseEntity<byte[]> gerarPdf(@RequestBody OrdemCompra ordemCompra) {
        try {
            byte[] pdfBytes = ordemCompraService.gerarPdf(ordemCompra);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=ordem_compra.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

