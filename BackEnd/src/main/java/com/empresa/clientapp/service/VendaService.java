package com.empresa.clientapp.service;

import com.empresa.clientapp.model.Cliente;
import com.empresa.clientapp.model.ItemVenda;
import com.empresa.clientapp.model.Produto;
import com.empresa.clientapp.model.Venda;
import com.empresa.clientapp.repository.VendaRepository;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

        // Recalcular o total da venda
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
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            document.add(new Paragraph("Nota de Venda"));
            document.add(new Paragraph("Código da Venda: " + venda.getId()));
            document.add(new Paragraph("Cliente: " + venda.getCliente().getNome()));
            document.add(new Paragraph("Data da Venda: " + venda.getDataVenda()));

            PdfPTable table = new PdfPTable(4);
            table.addCell("Produto");
            table.addCell("Quantidade");
            table.addCell("Valor Unitário");
            table.addCell("Subtotal");

            for (ItemVenda item : venda.getItens()) {
                table.addCell(item.getProduto().getNome());
                table.addCell(String.valueOf(item.getQuantidade()));
                table.addCell(item.getValorUnitario().toString());
                table.addCell(item.getSubtotal().toString());
            }

            document.add(table);
            document.add(new Paragraph("Valor Total: " + venda.getValorTotal()));

            document.close();
            writer.close();

            return baos.toByteArray();
        } catch (DocumentException e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        }
    }

}
