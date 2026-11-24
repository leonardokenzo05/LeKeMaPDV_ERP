package com.erp.view;

import com.erp.model.*;
import com.erp.service.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class VendasView extends JPanel {
    private final ClienteService clienteService;
    private final ProdutoService produtoService;
    private final PedidoService pedidoService;

    private JComboBox<Cliente> cbClientes;
    private JComboBox<Produto> cbProdutos;
    private JTextField txtQuantidade;
    private JTable tableItens;
    private DefaultTableModel tableModel;
    private JLabel lblTotal;

    private List<PedidoItem> itensTemporarios = new ArrayList<>();
    private BigDecimal valorTotal = BigDecimal.ZERO;

    public VendasView() {
        this.clienteService = new ClienteService();
        this.produtoService = new ProdutoService();
        this.pedidoService = new PedidoService();

        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(setupCenterPanel(), BorderLayout.CENTER);
        add(setupBottomPanel(), BorderLayout.SOUTH);

        carregarCombos();
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new GridLayout(1, 2, 20, 0));
        header.setOpaque(false);

        JPanel panelCliente = new JPanel(new BorderLayout(10, 10));
        panelCliente.setOpaque(false);
        panelCliente.setBorder(BorderFactory.createTitledBorder("1. Selecione o Cliente"));
        cbClientes = new JComboBox<>();
        panelCliente.add(cbClientes, BorderLayout.CENTER);

        JPanel panelProduto = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelProduto.setOpaque(false);
        panelProduto.setBorder(BorderFactory.createTitledBorder("2. Adicione Produtos"));
        cbProdutos = new JComboBox<>();
        txtQuantidade = new JTextField(5);
        JButton btnAdicionar = new JButton("Adicionar");
        btnAdicionar.setBackground(new Color(52, 152, 219));
        btnAdicionar.setForeground(Color.WHITE);
        btnAdicionar.addActionListener(e -> adicionarItem());

        panelProduto.add(new JLabel("Produto:"));
        panelProduto.add(cbProdutos);
        panelProduto.add(new JLabel("Qtd:"));
        panelProduto.add(txtQuantidade);
        panelProduto.add(btnAdicionar);

        header.add(panelCliente);
        header.add(panelProduto);

        return header;
    }

    private JPanel setupCenterPanel() {
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(BorderFactory.createTitledBorder("Itens do Pedido"));

        tableModel = new DefaultTableModel(new Object[]{"Produto", "Qtd", "Preço Unit.", "Subtotal"}, 0);
        tableItens = new JTable(tableModel);
        tableItens.setRowHeight(25);
        
        JScrollPane scroll = new JScrollPane(tableItens);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        center.add(scroll, BorderLayout.CENTER);
        
        return center;
    }

    private JPanel setupBottomPanel() {
        JPanel panelBottom = new JPanel(new BorderLayout());
        panelBottom.setOpaque(false);
        panelBottom.setBorder(new EmptyBorder(10, 0, 0, 0));

        lblTotal = new JLabel("TOTAL: R$ 0,00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTotal.setForeground(new Color(44, 62, 80));

        JPanel panelActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelActions.setOpaque(false);

        JButton btnFinalizar = new JButton("Finalizar Venda");
        btnFinalizar.setPreferredSize(new Dimension(180, 50));
        btnFinalizar.setBackground(new Color(46, 204, 113));
        btnFinalizar.setForeground(Color.WHITE);
        btnFinalizar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnFinalizar.addActionListener(e -> finalizarVenda());

        JButton btnLimpar = new JButton("Limpar");
        btnLimpar.setPreferredSize(new Dimension(120, 50));
        btnLimpar.setBackground(new Color(149, 165, 166));
        btnLimpar.setForeground(Color.WHITE);
        btnLimpar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLimpar.addActionListener(e -> limparVenda());

        panelActions.add(btnLimpar);
        panelActions.add(btnFinalizar);

        panelBottom.add(lblTotal, BorderLayout.WEST);
        panelBottom.add(panelActions, BorderLayout.EAST);

        return panelBottom;
    }

    private void carregarCombos() {
        clienteService.buscarTodos().forEach(cbClientes::addItem);
        produtoService.buscarTodos().forEach(cbProdutos::addItem);
        
        cbClientes.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Cliente) setText(((Cliente) value).getNome());
                return this;
            }
        });

        cbProdutos.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Produto) {
                    Produto p = (Produto) value;
                    setText(p.getNome() + " (Estoque: " + p.getEstoque() + ")");
                }
                return this;
            }
        });
    }

    private void adicionarItem() {
        try {
            Produto produto = (Produto) cbProdutos.getSelectedItem();
            Integer qtd = Integer.parseInt(txtQuantidade.getText());

            if (produto == null || qtd <= 0) {
                throw new RuntimeException("Selecione um produto e uma quantidade válida.");
            }

            if (produto.getEstoque() < getQuantidadeReservada(produto) + qtd) {
                throw new RuntimeException("Estoque insuficiente!");
            }

            PedidoItem item = new PedidoItem(produto, qtd);
            itensTemporarios.add(item);

            BigDecimal subtotal = item.getPrecoUnitario().multiply(new BigDecimal(qtd));
            tableModel.addRow(new Object[]{produto.getNome(), qtd, item.getPrecoUnitario(), subtotal});

            valorTotal = valorTotal.add(subtotal);
            lblTotal.setText(String.format("TOTAL: R$ %.2f", valorTotal));
            txtQuantidade.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantidade inválida.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private int getQuantidadeReservada(Produto produto) {
        return itensTemporarios.stream()
                .filter(item -> item.getProduto().getId().equals(produto.getId()))
                .mapToInt(PedidoItem::getQuantidade)
                .sum();
    }

    private void finalizarVenda() {
        try {
            Cliente cliente = (Cliente) cbClientes.getSelectedItem();
            if (cliente == null || itensTemporarios.isEmpty()) {
                throw new RuntimeException("Selecione um cliente e adicione itens ao pedido.");
            }

            Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
            PagamentoVendaDialog dialog = new PagamentoVendaDialog(owner, valorTotal, itensTemporarios);
            dialog.setVisible(true);

            if (dialog.isFinalizado()) {
                Pedido pedido = new Pedido();
                pedido.setCliente(cliente);
                itensTemporarios.forEach(pedido::adicionarItem);
                
                // Atribuir pagamentos
                dialog.getPagamentos().forEach(pedido::adicionarPagamento);
                
                // O valor total do pedido deve refletir descontos e acréscimos aplicados nos pagamentos
                pedido.setValorTotal(dialog.getTotalFinal());

                pedidoService.realizarPedido(pedido);

                // Atualizar o dashboard se estiver no MainFrame
                if (owner instanceof MainFrame) {
                    ((MainFrame) owner).refreshDashboard();
                }

                mostrarMensagemVendaFinalizada(pedido);
                limparVenda();
                
                cbProdutos.removeAllItems();
                produtoService.buscarTodos().forEach(cbProdutos::addItem);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao finalizar: " + e.getMessage());
        }
    }

    private void mostrarMensagemVendaFinalizada(Pedido pedido) {
        StringBuilder mensagem = new StringBuilder("Venda finalizada com sucesso! ID: " + pedido.getId());
        Set<String> alertas = new LinkedHashSet<>();
        pedido.getItens().forEach(item -> {
            String alerta = produtoService.gerarAlertaEstoque(item.getProduto());
            if (alerta != null && alerta.contains("estoque minimo")) alertas.add(alerta);
        });
        if (!alertas.isEmpty()) {
            mensagem.append("\n\n");
            alertas.forEach(alerta -> mensagem.append(alerta).append("\n"));
        }
        JOptionPane.showMessageDialog(this, mensagem.toString());
    }

    private void limparVenda() {
        itensTemporarios.clear();
        tableModel.setRowCount(0);
        valorTotal = BigDecimal.ZERO;
        lblTotal.setText("TOTAL: R$ 0,00");
        txtQuantidade.setText("");
    }
}
