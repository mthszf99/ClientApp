package com.empresa.clientapp.service;

import com.empresa.clientapp.DTO.DashboardDTO;
import com.empresa.clientapp.DTO.ProdutoMaisVendidoDTO;
import com.empresa.clientapp.DTO.VendaPorPeriodoDTO;
import com.empresa.clientapp.model.Venda;
import com.empresa.clientapp.repository.ClientRepository;
import com.empresa.clientapp.repository.ProdutoRepository;
import com.empresa.clientapp.repository.VendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EstatisticasService {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public DashboardDTO getDashboardData() {
        DashboardDTO dashboardDTO = new DashboardDTO();

        // Total de vendas
        dashboardDTO.setTotalVendas(vendaRepository.count());

        // Total de clientes
        dashboardDTO.setTotalClientes(clientRepository.count());

        // Total de produtos
        dashboardDTO.setTotalProdutos(produtoRepository.count());

        // Valor total das vendas
        List<Venda> todasVendas = vendaRepository.findAll();
        BigDecimal valorTotal = todasVendas.stream()
                .map(Venda::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dashboardDTO.setValorTotalVendas(valorTotal);

        // Ticket médio
        if (dashboardDTO.getTotalVendas() > 0) {
            double ticketMedio = valorTotal.doubleValue() / dashboardDTO.getTotalVendas();
            dashboardDTO.setTicketMedio(Math.round(ticketMedio * 100.0) / 100.0);
        } else {
            dashboardDTO.setTicketMedio(0.0);
        }

        // Produtos mais vendidos (top 5)
        dashboardDTO.setProdutosMaisVendidos(getProdutosMaisVendidos(5, null, null));

        // Vendas dos últimos 7 dias
        dashboardDTO.setVendasRecentes(getVendasPorUltimosDias(7));

        return dashboardDTO;
    }

    public List<VendaPorPeriodoDTO> getVendasPorUltimosDias(int dias) {
        // Data atual
        LocalDateTime dataFim = LocalDateTime.now();
        // Data de início (dias atrás)
        LocalDateTime dataInicio = dataFim.minusDays(dias);

        // Buscar todas as vendas no período
        List<Venda> vendas = vendaRepository.findByPeriodo(dataInicio, dataFim);

        // Mapa para agrupar vendas por data
        Map<LocalDate, VendaPorPeriodoDTO> vendasPorData = new HashMap<>();

        // Inicializar o mapa com todas as datas no período
        for (int i = 0; i < dias; i++) {
            LocalDate data = dataFim.minusDays(i).toLocalDate();
            vendasPorData.put(data, new VendaPorPeriodoDTO(data, 0L, BigDecimal.ZERO));
        }

        // Preencher o mapa com os dados das vendas
        for (Venda venda : vendas) {
            LocalDate dataVenda = venda.getDataVenda().toLocalDate();
            VendaPorPeriodoDTO dto = vendasPorData.get(dataVenda);

            if (dto != null) {
                dto.setQuantidade(dto.getQuantidade() + 1);
                dto.setValor(dto.getValor().add(venda.getValorTotal()));
            }
        }

        // Converter o mapa para uma lista ordenada por data
        return vendasPorData.values().stream()
                .sorted((a, b) -> a.getData().compareTo(b.getData()))
                .collect(Collectors.toList());
    }

    public List<VendaPorPeriodoDTO> getVendasPorMes(Integer ano) {
        // Se o ano não for especificado, usar o ano atual
        if (ano == null) {
            ano = LocalDate.now().getYear();
        }

        String jpql = "SELECT FUNCTION('MONTH', v.dataVenda) as mes, " +
                "COUNT(v) as quantidade, " +
                "SUM(v.valorTotal) as valor " +
                "FROM Venda v " +
                "WHERE FUNCTION('YEAR', v.dataVenda) = :ano " +
                "GROUP BY FUNCTION('MONTH', v.dataVenda) " +
                "ORDER BY mes";

        Query query = entityManager.createQuery(jpql);
        query.setParameter("ano", ano);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        List<VendaPorPeriodoDTO> vendasPorMes = new ArrayList<>();

        // Criar uma entrada para cada mês (1-12)
        Map<Integer, VendaPorPeriodoDTO> mesesMap = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            LocalDate data = LocalDate.of(ano, i, 1);
            mesesMap.put(i, new VendaPorPeriodoDTO(data, 0L, BigDecimal.ZERO));
        }

        // Preencher com os dados reais
        for (Object[] result : results) {
            Integer mes = ((Number) result[0]).intValue();
            Long quantidade = ((Number) result[1]).longValue();
            BigDecimal valor = (BigDecimal) result[2];

            LocalDate data = LocalDate.of(ano, mes, 1);
            mesesMap.put(mes, new VendaPorPeriodoDTO(data, quantidade, valor));
        }

        // Converter o mapa para uma lista ordenada por mês
        return mesesMap.values().stream()
                .sorted((a, b) -> a.getData().compareTo(b.getData()))
                .collect(Collectors.toList());
    }

    public List<ProdutoMaisVendidoDTO> getProdutosMaisVendidos(Integer limite, LocalDateTime dataInicio, LocalDateTime dataFim) {
        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT iv.produto.id, iv.produto.nome, SUM(iv.quantidade), SUM(iv.subtotal) ")
                .append("FROM ItemVenda iv ")
                .append("JOIN iv.venda v ");

        if (dataInicio != null && dataFim != null) {
            jpql.append("WHERE v.dataVenda BETWEEN :dataInicio AND :dataFim ");
        }

        jpql.append("GROUP BY iv.produto.id, iv.produto.nome ")
                .append("ORDER BY SUM(iv.quantidade) DESC");

        Query query = entityManager.createQuery(jpql.toString());

        if (dataInicio != null && dataFim != null) {
            query.setParameter("dataInicio", dataInicio);
            query.setParameter("dataFim", dataFim);
        }

        if (limite != null && limite > 0) {
            query.setMaxResults(limite);
        }

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        List<ProdutoMaisVendidoDTO> produtosMaisVendidos = new ArrayList<>();

        for (Object[] result : results) {
            Long produtoId = (Long) result[0];
            String nomeProduto = (String) result[1];
            Long quantidadeVendida = ((Number) result[2]).longValue();
            BigDecimal valorTotal = (BigDecimal) result[3];

            ProdutoMaisVendidoDTO dto = new ProdutoMaisVendidoDTO(produtoId, nomeProduto, quantidadeVendida, valorTotal);
            produtosMaisVendidos.add(dto);
        }

        return produtosMaisVendidos;
    }

    public List<Object[]> getClientesMaisCompras(Integer limite) {
        String jpql = "SELECT v.cliente.id, v.cliente.nome, COUNT(v), SUM(v.valorTotal) " +
                "FROM Venda v " +
                "GROUP BY v.cliente.id, v.cliente.nome " +
                "ORDER BY COUNT(v) DESC";

        Query query = entityManager.createQuery(jpql);

        if (limite != null && limite > 0) {
            query.setMaxResults(limite);
        }

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        return results;
    }

    public Double getTicketMedio(LocalDateTime dataInicio, LocalDateTime dataFim) {
        StringBuilder jpql = new StringBuilder("SELECT AVG(v.valorTotal) FROM Venda v");

        if (dataInicio != null && dataFim != null) {
            jpql.append(" WHERE v.dataVenda BETWEEN :dataInicio AND :dataFim");
        }

        Query query = entityManager.createQuery(jpql.toString());

        if (dataInicio != null && dataFim != null) {
            query.setParameter("dataInicio", dataInicio);
            query.setParameter("dataFim", dataFim);
        }

        Double result = (Double) query.getSingleResult();
        return result != null ? BigDecimal.valueOf(result).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0.0;
    }
}
