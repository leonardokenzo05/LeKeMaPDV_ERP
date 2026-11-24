package com.erp.view;

import com.erp.model.FormaPagamento;
import com.erp.model.PedidoItem;
import com.erp.model.PedidoPagamento;
import com.erp.service.FormaPagamentoService;
import com.erp.util.InputFieldDecorator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PagamentoVendaDialog extends JDialog {
    private final BigDecimal totalVendaOriginal;
    private final List<PedidoItem> itens;
    private final FormaPagamentoService formaPagamentoService;

    private JComboBox<FormaPagamento> cbFormas;
    private JTextField txtValorPago;
    private JTextField txtDesconto;
    private JTextField txtAcrescimo;
    private JTable tablePagamentos;
    private DefaultTableModel tableModel;
    private JLabel lblTotalRestante;
    private JLabel lblTotalPago;
    private JLabel lblTotalVendaComAjustes;

    private final List<PedidoPagamento> pagamentos = new ArrayList<>();
    private boolean finalizado = false;

    public PagamentoVendaDialog(Frame owner, BigDecimal totalVenda, List<PedidoItem> itens) {
        super(owner, "Finalizar Venda - Pagamento", true);
        this.totalVendaOriginal = totalVenda;
        this.itens = itens;
        this.formaPagamentoService = new FormaPagamentoService();

        setLayout(new BorderLayout(10, 10));
        setSize(700, 600);
        setLocationRelativeTo(owner);

        add(createInputPanel(), BorderLayout.NORTH);
        add(setupTable(), BorderLayout.CENTER);
        add(createSummaryPanel(), BorderLayout.SOUTH);

        atualizarTotais();
        carregarFormas();
    }

    private JPanel createInputPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cbFormas = new JComboBox<>();
        txtValorPago = new JTextField(10);
        txtDesconto = new JTextField("0,00", 10);
        txtAcrescimo = new JTextField("0,00", 10);

        InputFieldDecorator.configureDecimalField(txtValorPago);
        InputFieldDecorator.configureDecimalField(txtDesconto);
        InputFieldDecorator.configureDecimalField(txtAcrescimo);

        JButton btnAdicionar = new JButton("Adicionar Pagamento");
        btnAdicionar.setBackground(new Color(46, 204, 113));
        btnAdicionar.setForeground(Color.WHITE);
        btnAdicionar.addActionListener(e -> adicionarPagamento());

        gbc.gridx = 0;
        gbc.gridy = 0;
        p.add(new JLabel("Forma de Pagamento:"), gbc);
        gbc.gridx = 1;
        p.add(cbFormas, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        p.add(new JLabel("Valor a Pagar:"), gbc);
        gbc.gridx = 1;
        p.add(InputFieldDecorator.withPrefix(txtValorPago, "R$"), gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        p.add(new JLabel("Desconto:"), gbc);
        gbc.gridx = 3;
        p.add(InputFieldDecorator.withPrefix(txtDesconto, "R$"), gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        p.add(new JLabel("Acrescimo:"), gbc);
        gbc.gridx = 3;
        p.add(InputFieldDecorator.withPrefix(txtAcrescimo, "R$"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        p.add(btnAdicionar, gbc);

        return p;
    }

    private JScrollPane setupTable() {
        tableModel = new DefaultTableModel(new Object[]{"Forma", "Valor", "Desconto", "Acrescimo", "Subtotal"}, 0);
        tablePagamentos = new JTable(tableModel);
        InputFieldDecorator.applyCurrencyRenderer(tablePagamentos, 1, 2, 3, 4);
        return new JScrollPane(tablePagamentos);
    }

    private JPanel createSummaryPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel labels = new JPanel(new GridLayout(3, 1));
        lblTotalVendaComAjustes = new JLabel("Total da Venda (com ajustes): " + InputFieldDecorator.formatCurrency(BigDecimal.ZERO));
        lblTotalPago = new JLabel("Total Pago: " + InputFieldDecorator.formatCurrency(BigDecimal.ZERO));
        lblTotalRestante = new JLabel("Restante: " + InputFieldDecorator.formatCurrency(BigDecimal.ZERO));
        lblTotalRestante.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalRestante.setForeground(Color.RED);

        labels.add(lblTotalVendaComAjustes);
        labels.add(lblTotalPago);
        labels.add(lblTotalRestante);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnFinalizar = new JButton("Concluir Venda");
        btnFinalizar.setPreferredSize(new Dimension(150, 40));
        btnFinalizar.setBackground(new Color(52, 152, 219));
        btnFinalizar.setForeground(Color.WHITE);
        btnFinalizar.addActionListener(e -> finalizar());

        JButton btnLimpar = new JButton("Limpar Pagamentos");
        btnLimpar.addActionListener(e -> {
            pagamentos.clear();
            tableModel.setRowCount(0);
            atualizarTotais();
        });

        actions.add(btnLimpar);
        actions.add(btnFinalizar);

        p.add(labels, BorderLayout.WEST);
        p.add(actions, BorderLayout.EAST);
        return p;
    }

    private void carregarFormas() {
        formaPagamentoService.buscarAtivas().forEach(cbFormas::addItem);
    }

    private void adicionarPagamento() {
        try {
            FormaPagamento forma = (FormaPagamento) cbFormas.getSelectedItem();
            BigDecimal valor = InputFieldDecorator.parseBigDecimal(txtValorPago.getText());
            BigDecimal desconto = InputFieldDecorator.parseBigDecimal(txtDesconto.getText());
            BigDecimal acrescimo = InputFieldDecorator.parseBigDecimal(txtAcrescimo.getText());

            if (forma == null) {
                throw new RuntimeException("Selecione uma forma de pagamento.");
            }
            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Valor deve ser maior que zero.");
            }

            if (desconto.compareTo(BigDecimal.ZERO) > 0) {
                if (!forma.isPermiteDesconto()) {
                    throw new RuntimeException("Esta forma de pagamento nao permite desconto.");
                }

                validarDescontoProdutos(desconto);
            }

            if (desconto.compareTo(valor) > 0) {
                throw new RuntimeException("Desconto nao pode ser maior que o valor do pagamento.");
            }

            PedidoPagamento pag = new PedidoPagamento(null, forma, valor, desconto, acrescimo);
            pagamentos.add(pag);

            BigDecimal subtotal = valor.subtract(desconto).add(acrescimo);
            tableModel.addRow(new Object[]{forma.getDescricao(), valor, desconto, acrescimo, subtotal});

            atualizarTotais();
            txtValorPago.setText("");
            txtDesconto.setText("0,00");
            txtAcrescimo.setText("0,00");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valores numericos invalidos.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void validarDescontoProdutos(BigDecimal descontoPretendido) {
        BigDecimal totalDescontoJaAplicado = pagamentos.stream()
                .map(PedidoPagamento::getDesconto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal novoTotalDesconto = totalDescontoJaAplicado.add(descontoPretendido);

        BigDecimal maximoDescontoPermitido = BigDecimal.ZERO;
        for (PedidoItem item : itens) {
            if (item.getProduto().isPermitirDesconto()) {
                maximoDescontoPermitido = maximoDescontoPermitido.add(item.getPrecoUnitario().multiply(new BigDecimal(item.getQuantidade())));
            }
        }

        if (novoTotalDesconto.compareTo(maximoDescontoPermitido) > 0) {
            for (PedidoItem item : itens) {
                if (!item.getProduto().isPermitirDesconto()) {
                    throw new RuntimeException("O produto " + item.getProduto().getNome() + " nao permite desconto");
                }
            }
            throw new RuntimeException("Desconto total excede o valor permitido pelos produtos.");
        }

        if (novoTotalDesconto.compareTo(totalVendaOriginal) > 0) {
            throw new RuntimeException("Desconto nao pode ser maior que o total da venda.");
        }
    }

    private void atualizarTotais() {
        BigDecimal totalPagoComAjustes = pagamentos.stream()
                .map(p -> p.getValorPago().subtract(p.getDesconto()).add(p.getAcrescimo()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDescontoTotal = pagamentos.stream()
                .map(PedidoPagamento::getDesconto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAcrescimoTotal = pagamentos.stream()
                .map(PedidoPagamento::getAcrescimo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalVendaFinal = totalVendaOriginal.subtract(totalDescontoTotal).add(totalAcrescimoTotal);

        lblTotalVendaComAjustes.setText("Total da Venda (com ajustes): " + InputFieldDecorator.formatCurrency(totalVendaFinal));
        lblTotalPago.setText("Total Pago: " + InputFieldDecorator.formatCurrency(totalPagoComAjustes));

        BigDecimal restante = totalVendaFinal.subtract(totalPagoComAjustes);
        lblTotalRestante.setText("Restante: " + InputFieldDecorator.formatCurrency(restante));

        if (restante.compareTo(BigDecimal.ZERO) == 0) {
            lblTotalRestante.setForeground(new Color(46, 204, 113));
        } else {
            lblTotalRestante.setForeground(Color.RED);
        }
    }

    private void finalizar() {
        BigDecimal totalPagoComAjustes = pagamentos.stream()
                .map(p -> p.getValorPago().subtract(p.getDesconto()).add(p.getAcrescimo()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDescontoTotal = pagamentos.stream()
                .map(PedidoPagamento::getDesconto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAcrescimoTotal = pagamentos.stream()
                .map(PedidoPagamento::getAcrescimo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalVendaFinal = totalVendaOriginal.subtract(totalDescontoTotal).add(totalAcrescimoTotal);

        if (pagamentos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nao existir forma de pagamento.");
            return;
        }

        if (totalPagoComAjustes.compareTo(totalVendaFinal) < 0) {
            JOptionPane.showMessageDialog(this, "Valor pago for menor que o total.");
            return;
        }

        if (totalPagoComAjustes.compareTo(totalVendaFinal) > 0) {
            JOptionPane.showMessageDialog(this, "Valor pago ultrapassar o total.");
            return;
        }

        finalizado = true;
        dispose();
    }

    public boolean isFinalizado() {
        return finalizado;
    }

    public List<PedidoPagamento> getPagamentos() {
        return pagamentos;
    }

    public BigDecimal getTotalFinal() {
        BigDecimal totalDescontoTotal = pagamentos.stream()
                .map(PedidoPagamento::getDesconto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAcrescimoTotal = pagamentos.stream()
                .map(PedidoPagamento::getAcrescimo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalVendaOriginal.subtract(totalDescontoTotal).add(totalAcrescimoTotal);
    }
}
