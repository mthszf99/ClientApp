package com.empresa.clientapp.controller;

import com.empresa.clientapp.DTO.DashboardDTO;
import com.empresa.clientapp.DTO.ProdutoMaisVendidoDTO;
import com.empresa.clientapp.DTO.VendaPorPeriodoDTO;
import com.empresa.clientapp.service.EstatisticasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/estatisticas")
@CrossOrigin("*")
public class EstatisticasController {

    @Autowired
    private EstatisticasService estatisticasService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> getDashboardData() {
        DashboardDTO dashboardData = estatisticasService.getDashboardData();
        return ResponseEntity.ok(dashboardData);
    }

    @GetMapping("/vendas-por-periodo")
    public ResponseEntity<List<VendaPorPeriodoDTO>> getVendasPorPeriodo(
            @RequestParam(defaultValue = "7") Integer dias) {
        List<VendaPorPeriodoDTO> vendasPorPeriodo = estatisticasService.getVendasPorUltimosDias(dias);
        return ResponseEntity.ok(vendasPorPeriodo);
    }

    @GetMapping("/vendas-por-mes")
    public ResponseEntity<List<VendaPorPeriodoDTO>> getVendasPorMes(
            @RequestParam(required = false) Integer ano) {
        List<VendaPorPeriodoDTO> vendasPorMes = estatisticasService.getVendasPorMes(ano);
        return ResponseEntity.ok(vendasPorMes);
    }

    @GetMapping("/produtos-mais-vendidos")
    public ResponseEntity<List<ProdutoMaisVendidoDTO>> getProdutosMaisVendidos(
            @RequestParam(defaultValue = "10") Integer limite,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        LocalDateTime inicio = dataInicio != null ? dataInicio.atStartOfDay() : null;
        LocalDateTime fim = dataFim != null ? dataFim.atTime(LocalTime.MAX) : null;

        List<ProdutoMaisVendidoDTO> produtosMaisVendidos = estatisticasService.getProdutosMaisVendidos(limite, inicio, fim);
        return ResponseEntity.ok(produtosMaisVendidos);
    }

    @GetMapping("/clientes-mais-compras")
    public ResponseEntity<List<Object[]>> getClientesMaisCompras(
            @RequestParam(defaultValue = "10") Integer limite) {
        List<Object[]> clientesMaisCompras = estatisticasService.getClientesMaisCompras(limite);
        return ResponseEntity.ok(clientesMaisCompras);
    }

    @GetMapping("/ticket-medio")
    public ResponseEntity<Double> getTicketMedio(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        LocalDateTime inicio = dataInicio != null ? dataInicio.atStartOfDay() : null;
        LocalDateTime fim = dataFim != null ? dataFim.atTime(LocalTime.MAX) : null;

        Double ticketMedio = estatisticasService.getTicketMedio(inicio, fim);
        return ResponseEntity.ok(ticketMedio);
    }
}
