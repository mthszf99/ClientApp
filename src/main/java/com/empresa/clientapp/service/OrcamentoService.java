package com.empresa.clientapp.service;

import com.empresa.clientapp.model.ItemOrcamento;
import com.empresa.clientapp.model.Orcamento;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class OrcamentoService {

    public byte[] gerarPdf(Orcamento orcamento) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Orçamento"));
        document.add(new Paragraph("Cliente: " + orcamento.getNomeCliente()));

        // Formatando a data corretamente
        if (orcamento.getData() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            document.add(new Paragraph("Data: " + orcamento.getData().format(formatter)));
        }

        Table table = new Table(2);
        table.addCell(new Cell().add(new Paragraph("Produto/Serviço")));
        table.addCell(new Cell().add(new Paragraph("Valor")));

        // Verificando se há itens no orçamento antes de iterar
        if (orcamento.getItens() != null && !orcamento.getItens().isEmpty()) {
            for (ItemOrcamento item : orcamento.getItens()) {
                table.addCell(new Cell().add(new Paragraph(item.getDescricao())));
                table.addCell(new Cell().add(new Paragraph("R$ " + item.getValor())));
            }
        } else {
            // Caso não haja itens, adiciona uma linha informando
            Cell cell = new Cell(1, 2).add(new Paragraph("Nenhum item no orçamento."));
            table.addCell(cell);
        }

        document.add(table);
        document.add(new Paragraph("Desconto: R$ " + orcamento.getDesconto()));
        document.add(new Paragraph("Total: R$ " + orcamento.calcularTotal()));

        document.close();
        return outputStream.toByteArray();
    }
}


