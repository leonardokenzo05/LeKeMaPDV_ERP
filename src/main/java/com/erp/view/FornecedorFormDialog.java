package com.erp.view;

import com.erp.model.Fornecedor;
import com.erp.service.FornecedorService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FornecedorFormDialog extends JDialog {
    private final FornecedorService service;
    private final Fornecedor fornecedor;

    private JTextField txtNomeRazaoSocial, txtNomeFantasia, txtCpf, txtCnpj, txtRg, txtIe;
    private JTextField txtEndereco, txtNumero, txtBairro, txtCidade, txtEstado, txtCep, txtComplemento;
    private JTextField txtEmail, txtTelefone, txtNomeContato;
    private JCheckBox chkAtivo, chkFisica, chkJuridica, chkEstrangeiro;
    private JTextArea txtObservacoes;

    private boolean salvo = false;

    public FornecedorFormDialog(Frame owner, Fornecedor fornecedor, FornecedorService service) {
        super(owner, "Cadastro de Fornecedor", true);
        this.fornecedor = (fornecedor != null) ? fornecedor : new Fornecedor();
        this.service = service;
        
        setLayout(new BorderLayout());
        setSize(600, 750);
        setLocationRelativeTo(owner);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Identificação", createIdentificacaoPanel());
        tabs.addTab("Endereço", createEnderecoPanel());
        tabs.addTab("Contato & Outros", createContatoOutrosPanel());

        add(tabs, BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        if (fornecedor != null && fornecedor.getId() != null) {
            preencherCampos();
        } else {
            // Estado inicial para novo fornecedor
            atualizarCamposTipoPessoa();
        }
        
        configurarRegrasTipoPessoa();
    }

    private JPanel createIdentificacaoPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        txtNomeRazaoSocial = new JTextField();
        txtNomeFantasia = new JTextField();
        txtCpf = new JTextField();
        txtCnpj = new JTextField();
        txtRg = new JTextField();
        txtIe = new JTextField();
        chkAtivo = new JCheckBox("Ativo", true);
        chkFisica = new JCheckBox("Pessoa Física", true);
        chkJuridica = new JCheckBox("Pessoa Jurídica");
        chkEstrangeiro = new JCheckBox("Estrangeiro");

        JPanel pTipo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pTipo.add(chkFisica); pTipo.add(chkJuridica);

        int row = 0;
        addFormRow(p, row++, "Tipo:", pTipo);
        addFormRow(p, row++, "CPF:", txtCpf);
        addFormRow(p, row++, "CNPJ:", txtCnpj);
        addFormRow(p, row++, "Nome/Razão Social: *", txtNomeRazaoSocial);
        addFormRow(p, row++, "Apelido/Nome Fantasia:", txtNomeFantasia);
        addFormRow(p, row++, "RG:", txtRg);
        addFormRow(p, row++, "IE:", txtIe);
        addFormRow(p, row++, "Estrangeiro:", chkEstrangeiro);
        addFormRow(p, row, "Status:", chkAtivo);

        return p;
    }

    private JPanel createEnderecoPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        txtEndereco = new JTextField();
        txtNumero = new JTextField();
        txtBairro = new JTextField();
        txtCidade = new JTextField();
        txtEstado = new JTextField();
        txtCep = new JTextField();
        txtComplemento = new JTextField();

        int row = 0;
        addFormRow(p, row++, "CEP:", txtCep);
        addFormRow(p, row++, "Rua:", txtEndereco);
        addFormRow(p, row++, "Número:", txtNumero);
        addFormRow(p, row++, "Bairro:", txtBairro);
        addFormRow(p, row++, "Cidade:", txtCidade);
        addFormRow(p, row++, "Estado:", txtEstado);
        addFormRow(p, row, "Complemento:", txtComplemento);

        return p;
    }

    private JPanel createContatoOutrosPanel() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        txtTelefone = new JTextField();
        txtEmail = new JTextField();
        txtNomeContato = new JTextField();

        int row = 0;
        addFormRow(form, row++, "Telefone:", txtTelefone);
        addFormRow(form, row++, "Email:", txtEmail);
        addFormRow(form, row, "Nome do vendedor/contato:", txtNomeContato);

        p.add(form, BorderLayout.NORTH);
        
        JPanel obsPanel = new JPanel(new BorderLayout());
        obsPanel.add(new JLabel("Observações:"), BorderLayout.NORTH);
        txtObservacoes = new JTextArea(10, 20);
        obsPanel.add(new JScrollPane(txtObservacoes), BorderLayout.CENTER);
        
        p.add(obsPanel, BorderLayout.CENTER);

        return p;
    }

    private void addFormRow(JPanel panel, int row, String label, JComponent field) {
        GridBagConstraints l = new GridBagConstraints();
        l.gridx = 0; l.gridy = row; l.anchor = GridBagConstraints.WEST;
        l.insets = new Insets(3, 0, 3, 8);
        panel.add(new JLabel(label), l);

        GridBagConstraints f = new GridBagConstraints();
        f.gridx = 1; f.gridy = row; f.weightx = 1.0; f.fill = GridBagConstraints.HORIZONTAL;
        f.insets = new Insets(3, 0, 3, 0);
        panel.add(field, f);
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

    private void configurarRegrasTipoPessoa() {
        chkFisica.addActionListener(e -> {
            if (chkFisica.isSelected()) {
                chkJuridica.setSelected(false);
            } else if (!chkJuridica.isSelected()) {
                chkFisica.setSelected(true);
            }
            atualizarCamposTipoPessoa();
        });

        chkJuridica.addActionListener(e -> {
            if (chkJuridica.isSelected()) {
                chkFisica.setSelected(false);
            } else if (!chkFisica.isSelected()) {
                chkJuridica.setSelected(true);
            }
            atualizarCamposTipoPessoa();
        });
    }

    private void atualizarCamposTipoPessoa() {
        boolean isFisica = chkFisica.isSelected();
        txtCpf.setEnabled(isFisica);
        txtRg.setEnabled(isFisica);
        txtCnpj.setEnabled(!isFisica);
        txtIe.setEnabled(!isFisica);
        
        if (!isFisica) {
            txtCpf.setText("");
            txtRg.setText("");
        } else {
            txtCnpj.setText("");
            txtIe.setText("");
        }
    }

    private void preencherCampos() {
        chkFisica.setSelected(fornecedor.isFisica());
        chkJuridica.setSelected(fornecedor.isJuridica());
        
        if (fornecedor.isFisica()) {
            txtCpf.setText(fornecedor.getCpfCnpj());
            txtRg.setText(fornecedor.getRgIe());
        } else {
            txtCnpj.setText(fornecedor.getCpfCnpj());
            txtIe.setText(fornecedor.getRgIe());
        }
        
        txtNomeRazaoSocial.setText(fornecedor.getNomeRazaoSocial());
        txtNomeFantasia.setText(fornecedor.getNomeFantasia());
        chkEstrangeiro.setSelected(fornecedor.isEstrangeiro());
        chkAtivo.setSelected(fornecedor.isAtivo());

        txtCep.setText(fornecedor.getCep());
        txtEndereco.setText(fornecedor.getEndereco());
        txtNumero.setText(fornecedor.getNumero());
        txtBairro.setText(fornecedor.getBairro());
        txtCidade.setText(fornecedor.getCidade());
        txtEstado.setText(fornecedor.getEstado());
        txtComplemento.setText(fornecedor.getComplemento());

        txtTelefone.setText(fornecedor.getTelefone());
        txtEmail.setText(fornecedor.getEmail());
        txtNomeContato.setText(fornecedor.getNomeContato());
        txtObservacoes.setText(fornecedor.getObservacoes());
        
        atualizarCamposTipoPessoa();
    }

    private void salvar() {
        try {
            fornecedor.setFisica(chkFisica.isSelected());
            fornecedor.setJuridica(chkJuridica.isSelected());
            
            if (chkFisica.isSelected()) {
                fornecedor.setCpfCnpj(txtCpf.getText());
                fornecedor.setRgIe(txtRg.getText());
            } else {
                fornecedor.setCpfCnpj(txtCnpj.getText());
                fornecedor.setRgIe(txtIe.getText());
            }
            
            fornecedor.setNomeRazaoSocial(txtNomeRazaoSocial.getText());
            fornecedor.setNomeFantasia(txtNomeFantasia.getText());
            fornecedor.setEstrangeiro(chkEstrangeiro.isSelected());
            fornecedor.setAtivo(chkAtivo.isSelected());

            fornecedor.setCep(txtCep.getText());
            fornecedor.setEndereco(txtEndereco.getText());
            fornecedor.setNumero(txtNumero.getText());
            fornecedor.setBairro(txtBairro.getText());
            fornecedor.setCidade(txtCidade.getText());
            fornecedor.setEstado(txtEstado.getText());
            fornecedor.setComplemento(txtComplemento.getText());

            fornecedor.setTelefone(txtTelefone.getText());
            fornecedor.setEmail(txtEmail.getText());
            fornecedor.setNomeContato(txtNomeContato.getText());
            fornecedor.setObservacoes(txtObservacoes.getText());

            if (fornecedor.getId() == null) {
                service.salvar(fornecedor);
            } else {
                service.atualizar(fornecedor);
            }
            
            salvo = true;
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage());
        }
    }

    public boolean isSalvo() { return salvo; }
}
