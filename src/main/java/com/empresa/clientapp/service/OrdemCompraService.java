package com.empresa.clientapp.service;

import com.empresa.clientapp.model.OrdemCompra;
import com.empresa.clientapp.model.ItemOrdemCompra;
import com.empresa.clientapp.model.Produto;
import com.empresa.clientapp.repository.OrdemCompraRepository;
import com.empresa.clientapp.repository.ProdutoRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;

@Service
public class OrdemCompraService {

    @Autowired
    private OrdemCompraRepository ordemCompraRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Transactional
    public OrdemCompra salvarOrdemCompra(OrdemCompra ordemCompra) {
        BigDecimal total = BigDecimal.ZERO;

        for (ItemOrdemCompra item : ordemCompra.getItens()) {
            Produto produto = produtoRepository.findById(item.getProduto().getId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            if (produto.getQuantidade() < item.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
            }

            produto.setQuantidade(produto.getQuantidade() - item.getQuantidade());
            produtoRepository.save(produto);

            item.setPrecoUnitario(produto.getValorVenda());
            item.calcularSubtotal();
            total = total.add(item.getSubtotal());
        }

        ordemCompra.setTotal(total.subtract(ordemCompra.getDesconto()));
        ordemCompra = ordemCompraRepository.save(ordemCompra);

        gerarPdfOrdemCompra(ordemCompra);

        return ordemCompra;
    }

    public List<OrdemCompra> listarTodas() {
        return ordemCompraRepository.findAll();
    }

    public byte[] gerarPdfOrdemCompra(OrdemCompra ordemCompra) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Ordem de Compra - ID: " + ordemCompra.getId()));
            document.add(new Paragraph("Cliente: " + ordemCompra.getCliente().getNome()));
            document.add(new Paragraph("Observação: " + ordemCompra.getObservacao()));
            document.add(new Paragraph(" "));

            Table table = new Table(4);
            table.addCell("Produto");
            table.addCell("Quantidade");
            table.addCell("Preço Unitário");
            table.addCell("Subtotal");

            for (ItemOrdemCompra item : ordemCompra.getItens()) {
                table.addCell(item.getProduto().getNome());
                table.addCell(String.valueOf(item.getQuantidade()));
                table.addCell("R$ " + item.getPrecoUnitario());
                table.addCell("R$ " + item.getSubtotal());
            }

            document.add(table);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Desconto: R$ " + ordemCompra.getDesconto()));
            document.add(new Paragraph("Total: R$ " + ordemCompra.getTotal()));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        }
    }
}







