package com.empresa.clientapp.service;

import com.empresa.clientapp.model.Cliente;
import com.empresa.clientapp.model.ItemVenda;
import com.empresa.clientapp.model.Produto;
import com.empresa.clientapp.model.Venda;
import com.empresa.clientapp.repository.VendaRepository;
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
        }

        // Verificar e atualizar estoque para cada item
        for (ItemVenda item : venda.getItens()) {
            Produto produto = item.getProduto();
            if (produto != null && produto.getId() != null) {
                produtoService.reduzirEstoque(produto.getId(), item.getQuantidade());
            }
        }

        return vendaRepository.save(venda);
    }

    @Transactional
    public void deleteById(Long id) {
        vendaRepository.deleteById(id);
    }

    @Transactional
    public Venda adicionarItem(Long vendaId, ItemVenda novoItem) {
        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));

        // Verificar se o produto existe e tem estoque
        Produto produto = produtoService.findById(novoItem.getProduto().getId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        if (produto.getQuantidade() < novoItem.getQuantidade()) {
            throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
        }

        // Configurar o item e adicionar à venda
        novoItem.setProduto(produto);
        novoItem.calcularSubtotal();
        venda.adicionarItem(novoItem);

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

        // Atualizar estoque
        for (ItemVenda item : venda.getItens()) {
            produtoService.reduzirEstoque(item.getProduto().getId(), item.getQuantidade());
        }

        return vendaRepository.save(venda);
    }
}
