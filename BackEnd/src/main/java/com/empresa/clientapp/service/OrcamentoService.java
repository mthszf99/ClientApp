package com.empresa.clientapp.service;

import com.empresa.clientapp.model.ItemOrcamento;
import com.empresa.clientapp.model.Orcamento;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class OrcamentoService {

    public byte[] gerarPdf(Orcamento orcamento) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Definir margens
        document.setMargins(50, 50, 50, 50);

        // Título
        Paragraph titulo = new Paragraph("ORÇAMENTO")
                .setBold()
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(titulo);

        // Espaçamento
        document.add(new Paragraph("\n"));

        // Dados do cliente
        Table infoTable = new Table(2);
        infoTable.setWidth(UnitValue.createPercentValue(100));

        // Primeira coluna - Dados do cliente
        Cell clienteCell = new Cell();
        clienteCell.add(new Paragraph("Cliente: " + orcamento.getNomeCliente()).setBold());

        // Formatando a data
        if (orcamento.getData() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            clienteCell.add(new Paragraph("Data: " + orcamento.getData().format(formatter)));
        }

        infoTable.addCell(clienteCell.setBorder(Border.NO_BORDER));

        // Segunda coluna - Número do orçamento (exemplo)
        Cell numeroCell = new Cell();
        numeroCell.add(new Paragraph("Orçamento Nº: 001/2025").setBold());
        numeroCell.setTextAlignment(TextAlignment.RIGHT);
        infoTable.addCell(numeroCell.setBorder(Border.NO_BORDER));

        document.add(infoTable);
        document.add(new Paragraph("\n"));

        // Tabela de itens
        Table table = new Table(new float[]{3, 1});
        table.setWidth(UnitValue.createPercentValue(100));

        // Cabeçalho da tabela
        table.addHeaderCell(new Cell().add(new Paragraph("Produto/Serviço"))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("Valor"))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.RIGHT)
                .setBold());

        // Formato para valores monetários
        NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        // Itens do orçamento
        if (orcamento.getItens() != null && !orcamento.getItens().isEmpty()) {
            for (ItemOrcamento item : orcamento.getItens()) {
                table.addCell(new Cell().add(new Paragraph(item.getDescricao())));
                table.addCell(new Cell().add(new Paragraph(formatoMoeda.format(item.getValor())))
                        .setTextAlignment(TextAlignment.RIGHT));
            }
        } else {
            Cell cell = new Cell(1, 2).add(new Paragraph("Nenhum item no orçamento."));
            table.addCell(cell);
        }

        document.add(table);

        // Totais
        Table totaisTable = new Table(2);
        totaisTable.setWidth(UnitValue.createPercentValue(100));

        // Células vazias para alinhar à direita
        totaisTable.addCell(new Cell().setBorder(Border.NO_BORDER));

        // Subtotal, desconto e total
        Cell totais = new Cell().setBorder(Border.NO_BORDER);

        double subtotal = orcamento.getItens() != null ?
                orcamento.getItens().stream().mapToDouble(ItemOrcamento::getValor).sum() : 0;

        totais.add(new Paragraph("Subtotal: " + formatoMoeda.format(subtotal))
                .setTextAlignment(TextAlignment.RIGHT));
        totais.add(new Paragraph("Desconto: " + formatoMoeda.format(orcamento.getDesconto()))
                .setTextAlignment(TextAlignment.RIGHT));
        totais.add(new Paragraph("Total: " + formatoMoeda.format(orcamento.calcularTotal()))
                .setBold()
                .setTextAlignment(TextAlignment.RIGHT));

        totaisTable.addCell(totais);
        document.add(totaisTable);

        // Rodapé
        document.add(new Paragraph("\n\n"));
        document.add(new Paragraph("Observações:").setBold());
        document.add(new Paragraph("1. Este orçamento é válido por 15 dias."));
        document.add(new Paragraph("2. Formas de pagamento: Dinheiro, PIX, Cartão de Crédito (até 3x sem juros)."));
        document.add(new Paragraph("3. Prazo de entrega: a combinar."));

        // Assinatura
        document.add(new Paragraph("\n\n\n"));
        document.add(new Paragraph("_______________________________")
                .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Assinatura do Responsável")
                .setTextAlignment(TextAlignment.CENTER));

        document.close();
        return outputStream.toByteArray();
    }
}