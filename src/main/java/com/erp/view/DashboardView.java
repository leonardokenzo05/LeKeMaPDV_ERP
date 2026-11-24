package com.erp.view;

import com.erp.service.ClienteService;
import com.erp.service.ProdutoService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardView extends JPanel {
    private final ClienteService clienteService = new ClienteService();
    private final ProdutoService produtoService = new ProdutoService();
    private final com.erp.service.PedidoService pedidoService = new com.erp.service.PedidoService();

    private JLabel lblClientes;
    private JLabel lblProdutos;
    private JLabel lblTotalVendas;
    private JLabel lblQtdVendas;

    public DashboardView() {
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(30, 30, 30, 30));
        setBackground(new Color(245, 245, 245));

        JLabel title = new JLabel("Dashboard de Indicadores");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        cardsPanel.setOpaque(false);

        lblClientes = new JLabel("0");
        lblProdutos = new JLabel("0");
        lblTotalVendas = new JLabel("R$ 0,00");
        lblQtdVendas = new JLabel("0");

        cardsPanel.add(createStatCard("Clientes Cadastrados", lblClientes, new Color(52, 152, 219)));
        cardsPanel.add(createStatCard("Produtos em Estoque", lblProdutos, new Color(46, 204, 113)));
        cardsPanel.add(createStatCard("Vendas do Dia (Valor)", lblTotalVendas, new Color(155, 89, 182)));
        cardsPanel.add(createStatCard("Quantidade de Vendas (Dia)", lblQtdVendas, new Color(230, 126, 34)));

        add(cardsPanel, BorderLayout.CENTER);

        refresh();

        // Timer para atualizar periodicamente (ex: a cada 30 segundos)
        // Isso garante que a troca de dia funcione se o app ficar aberto
        Timer timer = new Timer(30000, e -> refresh());
        timer.start();
    }

    public void refresh() {
        lblClientes.setText(String.valueOf(clienteService.buscarTodos().size()));
        lblProdutos.setText(String.valueOf(produtoService.buscarTodos().size()));
        
        java.math.BigDecimal totalDia = pedidoService.somarVendasDoDia();
        lblTotalVendas.setText(String.format("R$ %.2f", totalDia));
        
        long qtdDia = pedidoService.contarVendasDoDia();
        lblQtdVendas.setText(String.valueOf(qtdDia));
    }

    private JPanel createStatCard(String title, JLabel lblValue, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTitle.setForeground(Color.GRAY);

        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblValue.setForeground(accentColor);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);

        return card;
    }
}
