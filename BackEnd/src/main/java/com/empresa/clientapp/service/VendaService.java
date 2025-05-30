package com.empresa.clientapp.service;

import com.empresa.clientapp.model.Cliente;
import com.empresa.clientapp.model.ItemVenda;
import com.empresa.clientapp.model.Produto;
import com.empresa.clientapp.model.Venda;
import com.empresa.clientapp.repository.VendaRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class VendaService {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private ClientService clienteService;

    @Autowired
    private ProdutoService produtoService;

    public List<Venda> findAll() {
        return vendaRepository.findAll();
    }

    public Optional<Venda> findById(Long id) {
        return vendaRepository.findById(id);
    }

    public List<Venda> findByClienteId(Long clienteId) {
        return vendaRepository.findByClienteId(clienteId);
    }

    public List<Venda> findByPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return vendaRepository.findByPeriodo(inicio, fim);
    }

    public List<Venda> findByProdutoId(Long produtoId) {
        return vendaRepository.findByProdutoId(produtoId);
    }

    @Transactional
    public Venda save(Venda venda) {
        // Verificar se o cliente existe
        if (venda.getCliente() != null && venda.getCliente().getId() != null) {
            Optional<Cliente> clienteOpt = clienteService.findById(venda.getCliente().getId());
            if (!clienteOpt.isPresent()) {
                throw new RuntimeException("Cliente não encontrado");
            }

            System.out.println("Cliente original: " + venda.getCliente());
            System.out.println("Cliente do banco: " + clienteOpt.get());

            venda.setCliente(clienteOpt.get());
        } else {
            throw new RuntimeException("Cliente é obrigatório para a venda");
        }

        // Garantir que o desconto seja preservado
        if (venda.getDesconto() != null) {
            System.out.println("Desconto informado: " + venda.getDesconto());
        } else {
            venda.setDesconto(BigDecimal.ZERO);
        }

        // Processar cada item da venda
        for (ItemVenda item : venda.getItens()) {
            if (item.getProduto() != null && item.getProduto().getId() != null) {
                // Obter o produto gerenciado do banco de dados
                Optional<Produto> produtoOpt = produtoService.findById(item.getProduto().getId());
                if (!produtoOpt.isPresent()) {
                    throw new RuntimeException("Produto não encontrado: " + item.getProduto().getId());
                }

                Produto produtoGerenciado = produtoOpt.get();

                // Verificar estoque
                if (produtoGerenciado.getQuantidade() < item.getQuantidade()) {
                    throw new RuntimeException("Estoque insuficiente para o produto: " + produtoGerenciado.getNome());
                }

                // Atualizar o produto no item
                item.setProduto(produtoGerenciado);

                // Garantir que o valorUnitario está definido
                if (item.getValorUnitario() == null) {
                    item.setValorUnitario(produtoGerenciado.getValorVenda());
                }

                // Calcular o subtotal
                item.calcularSubtotal();

                // Conectar o item à venda
                item.setVenda(venda);
            } else {
                throw new RuntimeException("Produto inválido no item da venda");
            }
        }

        // Recalcular o total da venda explicitamente depois de processar os itens
        venda.recalcularTotal();

        // Salvar a venda
        Venda vendaSalva = vendaRepository.save(venda);

        // Atualizar o estoque após salvar com sucesso
        for (ItemVenda item : vendaSalva.getItens()) {
            produtoService.reduzirEstoque(item.getProduto().getId(), item.getQuantidade());
        }

        return vendaSalva;
    }

    @Transactional
    public void deleteById(Long id) {
        vendaRepository.deleteById(id);
    }

    @Transactional
    public Venda adicionarItem(Long vendaId, ItemVenda novoItem) {
        // Buscar a venda
        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));

        // Verificar o produto
        if (novoItem.getProduto() == null || novoItem.getProduto().getId() == null) {
            throw new RuntimeException("Produto inválido");
        }

        // Buscar o produto do banco de dados
        Produto produto = produtoService.findById(novoItem.getProduto().getId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        // Verificar estoque
        if (produto.getQuantidade() < novoItem.getQuantidade()) {
            throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
        }

        // Configurar o item
        novoItem.setProduto(produto);
        novoItem.setValorUnitario(produto.getValorVenda());

        // Garantir que a quantidade é válida
        if (novoItem.getQuantidade() == null || novoItem.getQuantidade() <= 0) {
            novoItem.setQuantidade(1);
        }

        // Calcular o subtotal
        novoItem.calcularSubtotal();

        // Adicionar à venda
        venda.adicionarItem(novoItem);

        System.out.println("Item adicionado: " + novoItem);
        System.out.println("Venda após adição: itens=" + venda.getItens().size() + ", total=" + venda.getValorTotal());

        // Salvar a venda
        return vendaRepository.save(venda);
    }

    @Transactional
    public Venda removerItem(Long vendaId, Long itemId) {
        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));

        ItemVenda itemParaRemover = venda.getItens().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item não encontrado na venda"));

        venda.removerItem(itemParaRemover);

        return vendaRepository.save(venda);
    }

    @Transactional
    public Venda finalizarVenda(Long vendaId) {
        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));

        // Verificar se a venda tem itens
        if (venda.getItens().isEmpty()) {
            throw new RuntimeException("Não é possível finalizar uma venda sem itens");
        }

        // Recalcular o total para garantir
        venda.recalcularTotal();

        // Atualizar a data da venda para o momento da finalização
        venda.setDataVenda(LocalDateTime.now());

        // Atualizar o estoque (se ainda não foi feito)
        for (ItemVenda item : venda.getItens()) {
            // Aqui você pode verificar se o estoque já foi reduzido ou não
            produtoService.reduzirEstoque(item.getProduto().getId(), item.getQuantidade());
        }

        return vendaRepository.save(venda);
    }

    @Transactional
    public byte[] gerarPdfVenda(Long vendaId) {
        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // Criando o PdfWriter
            PdfWriter writer = new PdfWriter(baos);

            // Criando o PdfDocument
            PdfDocument pdf = new PdfDocument(writer);

            // Criando o Document
            Document document = new Document(pdf);

            // Adicionando o título e informações
            document.add(new Paragraph("Nota de Venda"));
            document.add(new Paragraph("Código da Venda: " + venda.getId()));
            document.add(new Paragraph("Cliente: " + venda.getCliente().getNome()));

            // Formatando a data
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String dataFormatada = venda.getDataVenda().format(formatter);
            document.add(new Paragraph("Data da Venda: " + dataFormatada));

            // Adicionando espaço
            document.add(new Paragraph(" "));

            // Criando a tabela com 4 colunas
            Table table = new Table(UnitValue.createPercentArray(new float[]{40, 20, 20, 20}));

            // Adicionando o cabeçalho
            table.addHeaderCell(new Cell().add(new Paragraph("Produto")));
            table.addHeaderCell(new Cell().add(new Paragraph("Quantidade")));
            table.addHeaderCell(new Cell().add(new Paragraph("Valor Unitário")));
            table.addHeaderCell(new Cell().add(new Paragraph("Subtotal")));

            // Adicionando os itens
            for (ItemVenda item : venda.getItens()) {
                table.addCell(new Cell().add(new Paragraph(item.getProduto().getNome())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantidade()))));
                table.addCell(new Cell().add(new Paragraph(item.getValorUnitario().toString())));
                table.addCell(new Cell().add(new Paragraph(item.getSubtotal().toString())));
            }

            // Adicionando a tabela ao documento
            document.add(table);

            // Adicionando espaço
            document.add(new Paragraph(" "));

            // Adicionando o total da venda
            document.add(new Paragraph("Valor Total: " + venda.getValorTotal()));

            // Se houver desconto, mostrar
            if (venda.getDesconto().compareTo(BigDecimal.ZERO) > 0) {
                document.add(new Paragraph("Desconto Aplicado: " + venda.getDesconto()));
            }

            // Se houver forma de pagamento, mostrar
            if (venda.getFormaPagamento() != null && !venda.getFormaPagamento().isEmpty()) {
                document.add(new Paragraph("Forma de Pagamento: " + venda.getFormaPagamento()));
            }

            // Se houver observações, mostrar
            if (venda.getObservacoes() != null && !venda.getObservacoes().isEmpty()) {
                document.add(new Paragraph("Observações: " + venda.getObservacoes()));
            }

            // Fechando o documento
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        }
    }
}
