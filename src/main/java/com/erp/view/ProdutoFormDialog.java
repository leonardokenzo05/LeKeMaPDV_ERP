package com.erp.view;

import com.erp.model.Fornecedor;
import com.erp.model.Produto;
import com.erp.service.FornecedorService;
import com.erp.service.ProdutoService;
import com.erp.util.InputFieldDecorator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ProdutoFormDialog extends JDialog {
    private final ProdutoService service;
    private final FornecedorService fornecedorService;
    private final Produto produto;
    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private JTextField txtNome;
    private JTextField txtDescricaoResumida;
    private JTextField txtCodigoBarras;
    private JTextField txtCodigoInterno;
    private JTextField txtPrecoVenda;
    private JTextField txtPrecoCompra;
    private JTextField txtMargemLucro;
    private JTextField txtDesconto;
    private JTextField txtEstoque;
    private JTextField txtEstoqueMinimo;
    private JTextField txtEstoqueMaximo;
    private JTextField txtValidade;
    private JTextField txtNcm;
    private JTextField txtCfop;
    private JComboBox<String> cbUnidadeMedida;
    private JComboBox<Fornecedor> cbFornecedor;
    private JCheckBox chkAtivo;
    private JCheckBox chkPermitirDesconto;

    private boolean salvo = false;
    private boolean calculando = false;

    public ProdutoFormDialog(Frame owner, Produto produto, ProdutoService service) {
        super(owner, "Cadastro de Produto", true);
        this.produto = (produto != null) ? produto : new Produto();
        this.service = service;
        this.fornecedorService = new FornecedorService();

        setLayout(new BorderLayout());
        setSize(500, 650);
        setLocationRelativeTo(owner);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Dados Basicos", createBasicosPanel());
        tabs.addTab("Estoque & Precos", createEstoquePrecoPanel());
        tabs.addTab("Fiscal & Outros", createFiscalPanel());

        add(tabs, BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        if (produto != null && produto.getId() != null) {
            preencherCampos();
        }

        adicionarListenersCalculo();
    }

    private JPanel createBasicosPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        txtNome = new JTextField();
        txtDescricaoResumida = new JTextField();
        txtCodigoBarras = new JTextField();
        txtCodigoInterno = new JTextField();
        cbFornecedor = new JComboBox<>();
        carregarFornecedores();
        chkAtivo = new JCheckBox("Ativo", true);

        int row = 0;
        addFormRow(p, row++, "Nome (Descricao): *", txtNome);
        addFormRow(p, row++, "Desc. Resumida: *", txtDescricaoResumida);
        addFormRow(p, row++, "Cod. Barras (EAN):", txtCodigoBarras);
        addFormRow(p, row++, "Cod. Interno:", txtCodigoInterno);
        addFormRow(p, row++, "Fornecedor:", cbFornecedor);
        addFormRow(p, row, "Status:", chkAtivo);

        return p;
    }

    private void carregarFornecedores() {
        cbFornecedor.addItem(null);
        List<Fornecedor> fornecedores = fornecedorService.buscarTodos();
        for (Fornecedor f : fornecedores) {
            if (f.isAtivo()) {
                cbFornecedor.addItem(f);
            }
        }
    }

    private JPanel createEstoquePrecoPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        txtPrecoCompra = new JTextField();
        txtMargemLucro = new JTextField();
        txtPrecoVenda = new JTextField();
        chkPermitirDesconto = new JCheckBox("Permitir Desconto");
        txtDesconto = new JTextField();
        txtEstoque = new JTextField("0");
        txtEstoqueMinimo = new JTextField();
        txtEstoqueMaximo = new JTextField();

        InputFieldDecorator.configureDecimalField(txtPrecoCompra);
        InputFieldDecorator.configureDecimalField(txtMargemLucro);
        InputFieldDecorator.configureDecimalField(txtPrecoVenda);
        InputFieldDecorator.configureDecimalField(txtDesconto);

        int row = 0;
        addFormRow(p, row++, "Preco Compra: *", InputFieldDecorator.withPrefix(txtPrecoCompra, "R$"));
        addFormRow(p, row++, "Margem Lucro:", InputFieldDecorator.withSuffix(txtMargemLucro, "%"));
        addFormRow(p, row++, "Preco Venda: *", InputFieldDecorator.withPrefix(txtPrecoVenda, "R$"));
        addFormRow(p, row++, "Desconto:", chkPermitirDesconto);
        addFormRow(p, row++, "Valor Desconto:", InputFieldDecorator.withSuffix(txtDesconto, "%"));
        addFormRow(p, row++, "Estoque Atual: *", txtEstoque);
        addFormRow(p, row++, "Estoque Minimo:", txtEstoqueMinimo);
        addFormRow(p, row, "Estoque Maximo:", txtEstoqueMaximo);

        return p;
    }

    private JPanel createFiscalPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        cbUnidadeMedida = new JComboBox<>(new String[]{"UN", "KG"});
        txtNcm = new JTextField();
        txtCfop = new JTextField();
        txtValidade = new JTextField();

        int row = 0;
        addFormRow(p, row++, "Unidade Medida: *", cbUnidadeMedida);
        addFormRow(p, row++, "NCM (8 digitos):", txtNcm);
        addFormRow(p, row++, "CFOP (4 digitos):", txtCfop);
        addFormRow(p, row, "Validade (dias):", txtValidade);

        return p;
    }

    private void addFormRow(JPanel panel, int row, String label, JComponent field) {
        GridBagConstraints l = new GridBagConstraints();
        l.gridx = 0;
        l.gridy = row;
        l.anchor = GridBagConstraints.WEST;
        l.insets = new Insets(3, 0, 3, 8);
        panel.add(new JLabel(label), l);

        GridBagConstraints f = new GridBagConstraints();
        f.gridx = 1;
        f.gridy = row;
        f.weightx = 1.0;
        f.fill = GridBagConstraints.HORIZONTAL;
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

    private void preencherCampos() {
        txtNome.setText(produto.getNome());
        txtDescricaoResumida.setText(produto.getDescricaoResumida());
        txtCodigoBarras.setText(produto.getCodigoBarras());
        txtCodigoInterno.setText(produto.getCodigoInterno());
        chkAtivo.setSelected(produto.isAtivo());

        if (produto.getFornecedor() != null) {
            for (int i = 0; i < cbFornecedor.getItemCount(); i++) {
                Fornecedor f = cbFornecedor.getItemAt(i);
                if (f != null && f.getId().equals(produto.getFornecedor().getId())) {
                    cbFornecedor.setSelectedIndex(i);
                    break;
                }
            }
        }

        txtPrecoCompra.setText(InputFieldDecorator.formatDecimal(produto.getPrecoCompra()));
        txtMargemLucro.setText(InputFieldDecorator.formatPercent(produto.getMargemLucro()));
        txtPrecoVenda.setText(InputFieldDecorator.formatDecimal(produto.getPreco()));
        chkPermitirDesconto.setSelected(produto.isPermitirDesconto());
        txtDesconto.setText(InputFieldDecorator.formatPercent(produto.getDesconto()));
        txtEstoque.setText(String.valueOf(produto.getEstoque()));
        txtEstoqueMinimo.setText(produto.getEstoqueMinimo() != null ? produto.getEstoqueMinimo().toString() : "");
        txtEstoqueMaximo.setText(produto.getEstoqueMaximo() != null ? produto.getEstoqueMaximo().toString() : "");

        cbUnidadeMedida.setSelectedItem(produto.getUnidadeMedida());
        txtNcm.setText(produto.getNcm());
        txtCfop.setText(produto.getCfop());
        txtValidade.setText(produto.getValidade() != null ? produto.getValidade().toString() : "");
    }

    private void adicionarListenersCalculo() {
        txtPrecoCompra.addActionListener(e -> calcularPeloPrecoVenda());
        txtMargemLucro.addActionListener(e -> calcularPelaMargem());
        txtPrecoVenda.addActionListener(e -> calcularPeloPrecoVenda());

        java.awt.event.FocusAdapter adapter = new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (e.getSource() == txtPrecoVenda || e.getSource() == txtPrecoCompra) {
                    calcularPeloPrecoVenda();
                } else if (e.getSource() == txtMargemLucro) {
                    calcularPelaMargem();
                }
            }
        };

        txtPrecoCompra.addFocusListener(adapter);
        txtMargemLucro.addFocusListener(adapter);
        txtPrecoVenda.addFocusListener(adapter);
    }

    private void calcularPeloPrecoVenda() {
        if (calculando) {
            return;
        }

        calculando = true;
        try {
            double compra = InputFieldDecorator.parseDouble(txtPrecoCompra.getText());
            double venda = InputFieldDecorator.parseDouble(txtPrecoVenda.getText());
            if (compra > 0) {
                double margem = ((venda - compra) / compra) * 100;
                txtMargemLucro.setText(InputFieldDecorator.formatDecimal(margem));
            }
        } catch (Exception ignored) {
        }
        calculando = false;
    }

    private void calcularPelaMargem() {
        if (calculando) {
            return;
        }

        calculando = true;
        try {
            double compra = InputFieldDecorator.parseDouble(txtPrecoCompra.getText());
            double margem = InputFieldDecorator.parseDouble(txtMargemLucro.getText());
            double venda = compra * (1 + (margem / 100));
            txtPrecoVenda.setText(InputFieldDecorator.formatDecimal(venda));
        } catch (Exception ignored) {
        }
        calculando = false;
    }

    private void salvar() {
        try {
            produto.setNome(txtNome.getText());
            produto.setDescricaoResumida(txtDescricaoResumida.getText());
            produto.setCodigoBarras(txtCodigoBarras.getText());
            produto.setCodigoInterno(txtCodigoInterno.getText());
            produto.setAtivo(chkAtivo.isSelected());
            produto.setFornecedor((Fornecedor) cbFornecedor.getSelectedItem());

            produto.setPrecoCompra(InputFieldDecorator.parseDouble(txtPrecoCompra.getText()));
            produto.setMargemLucro(InputFieldDecorator.parseDouble(txtMargemLucro.getText()));
            produto.setPreco(InputFieldDecorator.parseBigDecimal(txtPrecoVenda.getText()));
            produto.setPermitirDesconto(chkPermitirDesconto.isSelected());
            produto.setDesconto(txtDesconto.getText().isEmpty() ? 0 : InputFieldDecorator.parseDouble(txtDesconto.getText()));
            produto.setEstoque(Integer.parseInt(txtEstoque.getText()));
            produto.setEstoqueMinimo(txtEstoqueMinimo.getText().isEmpty() ? null : Integer.parseInt(txtEstoqueMinimo.getText()));
            produto.setEstoqueMaximo(txtEstoqueMaximo.getText().isEmpty() ? null : Integer.parseInt(txtEstoqueMaximo.getText()));

            produto.setUnidadeMedida((String) cbUnidadeMedida.getSelectedItem());
            produto.setNcm(txtNcm.getText());
            produto.setCfop(txtCfop.getText());
            produto.setValidade(txtValidade.getText().isEmpty() ? null : Long.parseLong(txtValidade.getText()));

            if (produto.getId() == null) {
                service.salvar(produto);
            } else {
                service.atualizar(produto);
            }

            salvo = true;
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage());
        }
    }

    public boolean isSalvo() {
        return salvo;
    }
}
