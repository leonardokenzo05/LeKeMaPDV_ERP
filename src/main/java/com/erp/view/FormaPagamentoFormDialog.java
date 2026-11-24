package com.erp.view;

import com.erp.model.FormaPagamento;
import com.erp.service.FormaPagamentoService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FormaPagamentoFormDialog extends JDialog {
    private final FormaPagamentoService service;
    private final FormaPagamento formaPagamento;

    private JTextField txtDescricao;
    private JCheckBox chkPermiteDesconto;
    private JCheckBox chkAtivo;

    private boolean salvo = false;

    public FormaPagamentoFormDialog(Frame owner, FormaPagamento forma, FormaPagamentoService service) {
        super(owner, "Cadastro de Forma de Pagamento", true);
        this.formaPagamento = (forma != null) ? forma : new FormaPagamento();
        this.service = service;
        
        setLayout(new BorderLayout());
        setSize(400, 300);
        setLocationRelativeTo(owner);

        add(createFormPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        if (forma != null && forma.getId() != null) {
            preencherCampos();
        }
    }

    private JPanel createFormPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new EmptyBorder(20, 20, 20, 20));

        txtDescricao = new JTextField();
        chkPermiteDesconto = new JCheckBox("Permite Desconto", true);
        chkAtivo = new JCheckBox("Ativo", true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        p.add(new JLabel("Descrição: *"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        p.add(txtDescricao, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0;
        p.add(chkPermiteDesconto, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        p.add(chkAtivo, gbc);

        return p;
    }

    private JPanel createButtonPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");

        btnSalvar.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose());

        p.add(btnSalvar);
        p.add(btnCancelar);
        return p;
    }

    private void preencherCampos() {
        txtDescricao.setText(formaPagamento.getDescricao());
        chkPermiteDesconto.setSelected(formaPagamento.isPermiteDesconto());
        chkAtivo.setSelected(formaPagamento.isAtivo());
    }

    private void salvar() {
        try {
            formaPagamento.setDescricao(txtDescricao.getText().trim());
            formaPagamento.setPermiteDesconto(chkPermiteDesconto.isSelected());
            formaPagamento.setAtivo(chkAtivo.isSelected());

            if (formaPagamento.getId() == null) {
                service.salvar(formaPagamento);
            } else {
                service.atualizar(formaPagamento);
            }
            
            salvo = true;
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage());
        }
    }

    public boolean isSalvo() { return salvo; }
}
