package com.erp.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedido")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "data_pedido", nullable = false)
    private LocalDateTime data;

    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoItem> itens = new ArrayList<>();

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoPagamento> pagamentos = new ArrayList<>();

    public Pedido() {
        this.data = LocalDateTime.now();
        this.valorTotal = BigDecimal.ZERO;
    }

    public void adicionarItem(PedidoItem item) {
        itens.add(item);
        item.setPedido(this);
        atualizarValorTotal();
    }

    public void removerItem(PedidoItem item) {
        itens.remove(item);
        item.setPedido(null);
        atualizarValorTotal();
    }

    public void adicionarPagamento(PedidoPagamento pagamento) {
        pagamentos.add(pagamento);
        pagamento.setPedido(this);
    }

    public void removerPagamento(PedidoPagamento pagamento) {
        pagamentos.remove(pagamento);
        pagamento.setPedido(null);
    }

    private void atualizarValorTotal() {
        this.valorTotal = itens.stream()
                .map(item -> item.getPrecoUnitario().multiply(new BigDecimal(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }

    public List<PedidoItem> getItens() { return itens; }
    public void setItens(List<PedidoItem> itens) { this.itens = itens; }

    public List<PedidoPagamento> getPagamentos() { return pagamentos; }
    public void setPagamentos(List<PedidoPagamento> pagamentos) { this.pagamentos = pagamentos; }
}
