package com.erp.service;

import com.erp.model.Pedido;
import com.erp.model.PedidoItem;
import com.erp.model.Produto;
import com.erp.repository.PedidoRepository;
import com.erp.repository.ProdutoRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService() {
        this.pedidoRepository = new PedidoRepository();
        this.produtoRepository = new ProdutoRepository();
    }

    public Pedido realizarPedido(Pedido pedido) {
        if (pedido.getItens().isEmpty()) {
            throw new RuntimeException("Não é possível realizar um pedido sem itens");
        }

        Map<Long, Produto> produtos = new LinkedHashMap<>();
        Map<Long, Integer> quantidades = new LinkedHashMap<>();

        for (PedidoItem item : pedido.getItens()) {
            Produto produto = item.getProduto();
            produtos.put(produto.getId(), produto);
            quantidades.merge(produto.getId(), item.getQuantidade(), Integer::sum);
        }

        for (Map.Entry<Long, Integer> entry : quantidades.entrySet()) {
            Produto produto = produtos.get(entry.getKey());
            Integer quantidadeTotal = entry.getValue();
            if (produto.getEstoque() < quantidadeTotal) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
            }
        }

        for (Map.Entry<Long, Integer> entry : quantidades.entrySet()) {
            Produto produto = produtos.get(entry.getKey());
            produto.setEstoque(produto.getEstoque() - entry.getValue());
            produtoRepository.atualizar(produto);
        }

        return pedidoRepository.salvar(pedido);
    }

    public List<Pedido> buscarTodos() {
        return pedidoRepository.buscarTodos();
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.buscarPorId(id);
    }

    public long contarVendasDoDia() {
        return pedidoRepository.contarVendasDoDia();
    }

    public java.math.BigDecimal somarVendasDoDia() {
        return pedidoRepository.somarVendasDoDia();
    }
}
