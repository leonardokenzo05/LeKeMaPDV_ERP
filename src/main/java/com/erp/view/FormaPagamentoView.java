package com.erp.view;

import com.erp.model.FormaPagamento;
import com.erp.service.FormaPagamentoService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FormaPagamentoView extends JPanel {
    private final FormaPagamentoService service;
    private JTable table;
    private DefaultTableModel tableModel;

    public FormaPagamentoView() {
        this.service = new FormaPagamentoService();
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(setupTable(), BorderLayout.CENTER);
        add(setupActionPanel(), BorderLayout.SOUTH);
        
        atualizarTabela();
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JLabel title = new JLabel("Gerenciamento de Formas de Pagamento");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.add(title, BorderLayout.WEST);
        
        return header;
    }

    private JPanel setupActionPanel() {
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelButtons.setOpaque(false);
        
        JButton btnNovo = createStyledButton("Nova Forma", new Color(46, 204, 113));
        JButton btnEditar = createStyledButton("Editar Selecionada", new Color(52, 152, 219));
        JButton btnInativar = createStyledButton("Inativar/Excluir", new Color(231, 76, 60));
        JButton btnAtualizarTabela = createStyledButton("Atualizar Lista", new Color(149, 165, 166));

        btnNovo.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> editarSelecionado());
        btnInativar.addActionListener(e -> inativar());
        btnAtualizarTabela.addActionListener(e -> atualizarTabela());

        panelButtons.add(btnNovo);
        panelButtons.add(btnEditar);
        panelButtons.add(btnInativar);
        panelButtons.add(btnAtualizarTabela);

        return panelButtons;
    }

    private JScrollPane setupTable() {
        tableModel = new DefaultTableModel(new Object[]{"ID", "Descrição", "Permite Desconto", "Ativo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        return scroll;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return btn;
    }

    private void abrirFormulario(FormaPagamento forma) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        FormaPagamentoFormDialog dialog = new FormaPagamentoFormDialog(owner, forma, service);
        dialog.setVisible(true);
        if (dialog.isSalvo()) {
            atualizarTabela();
        }
    }

    private void editarSelecionado() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma forma de pagamento na tabela.");
            return;
        }
        Long id = (Long) table.getValueAt(row, 0);
        service.buscarTodos().stream()
                .filter(f -> f.getId().equals(id))
                .findFirst()
                .ifPresent(this::abrirFormulario);
    }

    private void inativar() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma forma de pagamento na tabela.");
            return;
        }
        Long id = (Long) table.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Deseja inativar esta forma de pagamento?");
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                service.buscarTodos().stream()
                        .filter(f -> f.getId().equals(id))
                        .findFirst()
                        .ifPresent(f -> {
                            f.setAtivo(false);
                            service.atualizar(f);
                        });
                JOptionPane.showMessageDialog(this, "Forma de pagamento inativada!");
                atualizarTabela();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
            }
        }
    }

    private void atualizarTabela() {
        tableModel.setRowCount(0);
        List<FormaPagamento> formas = service.buscarTodos();
        for (FormaPagamento f : formas) {
            tableModel.addRow(new Object[]{
                f.getId(), 
                f.getDescricao(), 
                f.isPermiteDesconto() ? "Sim" : "Não", 
                f.isAtivo() ? "Sim" : "Não"
            });
        }
    }
}
