package com.erp.view;

import com.erp.model.Fornecedor;
import com.erp.service.FornecedorService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FornecedorView extends JPanel {
    private final FornecedorService service;
    private JTable table;
    private DefaultTableModel tableModel;

    public FornecedorView() {
        this.service = new FornecedorService();
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
        
        JLabel title = new JLabel("Gerenciamento de Fornecedores");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.add(title, BorderLayout.WEST);
        
        return header;
    }

    private JPanel setupActionPanel() {
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelButtons.setOpaque(false);
        
        JButton btnNovo = createStyledButton("Novo Fornecedor", new Color(46, 204, 113));
        JButton btnEditar = createStyledButton("Editar Selecionado", new Color(52, 152, 219));
        JButton btnDeletar = createStyledButton("Deletar", new Color(231, 76, 60));
        JButton btnAtualizarTabela = createStyledButton("Atualizar Lista", new Color(149, 165, 166));

        btnNovo.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> editarSelecionado());
        btnDeletar.addActionListener(e -> deletar());
        btnAtualizarTabela.addActionListener(e -> atualizarTabela());

        panelButtons.add(btnNovo);
        panelButtons.add(btnEditar);
        panelButtons.add(btnDeletar);
        panelButtons.add(btnAtualizarTabela);

        return panelButtons;
    }

    private JScrollPane setupTable() {
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nome/Razão Social", "CPF/CNPJ", "Telefone", "Ativo"}, 0);
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

    private void abrirFormulario(Fornecedor fornecedor) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        FornecedorFormDialog dialog = new FornecedorFormDialog(owner, fornecedor, service);
        dialog.setVisible(true);
        if (dialog.isSalvo()) {
            atualizarTabela();
        }
    }

    private void editarSelecionado() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um fornecedor na tabela.");
            return;
        }
        Long id = (Long) table.getValueAt(row, 0);
        service.buscarPorId(id).ifPresent(this::abrirFormulario);
    }

    private void deletar() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um fornecedor na tabela.");
            return;
        }
        Long id = (Long) table.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Deseja realmente deletar?");
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                service.deletar(id);
                JOptionPane.showMessageDialog(this, "Fornecedor deletado!");
                atualizarTabela();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
            }
        }
    }

    private void atualizarTabela() {
        tableModel.setRowCount(0);
        List<Fornecedor> fornecedores = service.buscarTodos();
        for (Fornecedor f : fornecedores) {
            tableModel.addRow(new Object[]{
                f.getId(), 
                f.getNomeRazaoSocial(), 
                f.getCpfCnpj(), 
                f.getTelefone(), 
                f.isAtivo() ? "Sim" : "Não"
            });
        }
    }
}
