package com.empresa.clientapp.service;

import com.empresa.clientapp.model.Cliente;
import com.empresa.clientapp.model.ItemOrdemCompra;
import com.empresa.clientapp.model.OrdemCompra;
import com.empresa.clientapp.model.Produto;
import com.empresa.clientapp.repository.ClientRepository;
import com.empresa.clientapp.repository.OrdemCompraRepository;
import com.empresa.clientapp.repository.ProdutoRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class OrdemCompraService {

    @Autowired
    private OrdemCompraRepository ordemCompraRepository;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ClientRepository clientRepository;

    public byte[] salvarOrdemCompra(OrdemCompra ordemCompra) throws Exception {
        BigDecimal total = BigDecimal.ZERO;

        for (ItemOrdemCompra item : ordemCompra.getItens()) {
            if (item.getProduto() != null) {
                Produto produto = produtoService.findById(item.getProduto().getId())
                        .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

                item.setValor(produto.getValorVenda());

                System.out.println("Valor atribuído ao item: " + item.getValor());

                produtoService.reduzirEstoque(produto.getId(), item.getQuantidade());
            }

            BigDecimal quantidadeBigDecimal = BigDecimal.valueOf(item.getQuantidade());

            if (item.getValor() != null) {
                total = total.add(item.getValor().multiply(quantidadeBigDecimal));
            } else {
                throw new RuntimeException("Valor do item é nulo");
            }
        }

        ordemCompra.setTotal(total.subtract(ordemCompra.getDesconto()));
        ordemCompraRepository.save(ordemCompra);

        return gerarPdf(ordemCompra);
    }


    public byte[] gerarPdf(OrdemCompra ordemCompra) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Ordem de Compra"));
        document.add(new Paragraph("Cliente: " + ordemCompra.getCliente().getNome()));
        document.add(new Paragraph("Data: " + ordemCompra.getData()));

        Table table = new Table(3);
        table.addCell(new Cell().add(new Paragraph("Produto/Serviço")));
        table.addCell(new Cell().add(new Paragraph("Quantidade")));
        table.addCell(new Cell().add(new Paragraph("Valor")));

        for (ItemOrdemCompra item : ordemCompra.getItens()) {
            table.addCell(new Cell().add(new Paragraph(item.getDescricao())));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantidade()))));
            table.addCell(new Cell().add(new Paragraph("R$ " + item.getValor())));
        }

        document.add(table);
        document.add(new Paragraph("Desconto: R$ " + ordemCompra.getDesconto()));
        document.add(new Paragraph("Total: R$ " + ordemCompra.getTotal()));

        document.close();
        return outputStream.toByteArray();
    }
}




