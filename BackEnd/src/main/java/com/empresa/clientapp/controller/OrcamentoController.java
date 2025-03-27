package com.empresa.clientapp.controller;

import com.empresa.clientapp.model.Orcamento;
import com.empresa.clientapp.service.OrcamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/orcamentos")
public class OrcamentoController {

    @Autowired
    private OrcamentoService orcamentoService;

    @PostMapping("/gerar-pdf")
    public ResponseEntity<byte[]> gerarOrcamento(@RequestBody Orcamento orcamento) {
        try {
            byte[] pdfBytes = orcamentoService.gerarPdf(orcamento);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "orcamento.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

