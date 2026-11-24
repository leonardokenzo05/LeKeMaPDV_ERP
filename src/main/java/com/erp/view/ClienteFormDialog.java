package com.erp.view;

import com.erp.model.Cliente;
import com.erp.service.ClienteService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ClienteFormDialog extends JDialog {
    private final ClienteService service;
    private final Cliente cliente;
    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private JTextField txtNome, txtCpf, txtRg, txtCnpj, txtIe, txtNomeFantasia, txtNascimento;
    private JTextField txtEndereco, txtNumero, txtBairro, txtCidade, txtEstado, txtComplemento, txtCep, txtPontoRef, txtPais;
    private JTextField txtEmail, txtTelefone, txtCelular, txtFax;
    private JCheckBox chkAtivo, chkFisica, chkJuridica, chkConvenio, chkEstrangeiro, chkConsumidorFinal;
    private JTextArea txtObservacoes;

    private boolean salvo = false;

    public ClienteFormDialog(Frame owner, Cliente cliente, ClienteService service) {
        super(owner, "Cadastro de Cliente", true);
        this.cliente = (cliente != null) ? cliente : new Cliente();
        this.service = service;
        
        setLayout(new BorderLayout());
        setSize(600, 700);
        setLocationRelativeTo(owner);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Dados Básicos", createBasicosPanel());
        tabs.addTab("Endereço", createEnderecoPanel());
        tabs.addTab("Contato & Outros", createOutrosPanel());

        add(tabs, BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        if (cliente != null && cliente.getId() != null) {
            preencherCampos();
        }
    }

    private JPanel createBasicosPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        txtNome = new JTextField();
        txtNascimento = new JTextField("dd/mm/aaaa");
        chkAtivo = new JCheckBox("Ativo", true);
        chkFisica = new JCheckBox("Pessoa Física", true);
        chkJuridica = new JCheckBox("Pessoa Jurídica");
        txtCpf = new JTextField();
        txtRg = new JTextField();
        txtCnpj = new JTextField();
        txtIe = new JTextField();
        txtNomeFantasia = new JTextField();

        JPanel pTipo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pTipo.add(chkFisica); pTipo.add(chkJuridica);

        int row = 0;
        addFormRow(p, row++, "Nome: *", txtNome);
        addFormRow(p, row++, "Nascimento:", txtNascimento);
        addFormRow(p, row++, "Status:", chkAtivo);
        addFormRow(p, row++, "Tipo:", pTipo);
        addFormRow(p, row++, "CPF:", txtCpf);
        addFormRow(p, row++, "RG:", txtRg);
        addFormRow(p, row++, "CNPJ:", txtCnpj);
        addFormRow(p, row++, "Insc. Estadual:", txtIe);
        addFormRow(p, row, "Nome Fantasia:", txtNomeFantasia);

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
        txtComplemento = new JTextField();
        txtCep = new JTextField();
        txtPontoRef = new JTextField();
        txtPais = new JTextField("Brasil");
        chkEstrangeiro = new JCheckBox("Estrangeiro");

        int row = 0;
        addFormRow(p, row++, "Logradouro:", txtEndereco);
        addFormRow(p, row++, "Número:", txtNumero);
        addFormRow(p, row++, "Bairro:", txtBairro);
        addFormRow(p, row++, "Cidade:", txtCidade);
        addFormRow(p, row++, "Estado:", txtEstado);
        addFormRow(p, row++, "CEP:", txtCep);
        addFormRow(p, row++, "Complemento:", txtComplemento);
        addFormRow(p, row++, "Ponto Ref:", txtPontoRef);
        addFormRow(p, row++, "País:", txtPais);
        addFormRow(p, row, "Flags:", chkEstrangeiro);

        return p;
    }

    private void addFormRow(JPanel panel, int row, String label, JComponent field) {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(3, 0, 3, 8);
        panel.add(new JLabel(label), labelConstraints);

        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = row;
        fieldConstraints.weightx = 1.0;
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.anchor = GridBagConstraints.WEST;
        fieldConstraints.insets = new Insets(3, 0, 3, 0);
        panel.add(field, fieldConstraints);
    }

    private JPanel createOutrosPanel() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(7, 2, 5, 5));
        txtEmail = new JTextField();
        txtTelefone = new JTextField();
        txtCelular = new JTextField();
        txtFax = new JTextField();
        chkConvenio = new JCheckBox("Permite Convênio Vinculado");
        chkConsumidorFinal = new JCheckBox("Consumidor Final");

        form.add(new JLabel("Email:")); form.add(txtEmail);
        form.add(new JLabel("Telefone:")); form.add(txtTelefone);
        form.add(new JLabel("Celular:")); form.add(txtCelular);
        form.add(new JLabel("Fax:")); form.add(txtFax);
        form.add(new JLabel("Convênio:")); form.add(chkConvenio);
        form.add(new JLabel("Consumidor:")); form.add(chkConsumidorFinal);

        p.add(form, BorderLayout.NORTH);
        p.add(new JLabel("Observações:"), BorderLayout.CENTER);
        txtObservacoes = new JTextArea(5, 20);
        p.add(new JScrollPane(txtObservacoes), BorderLayout.SOUTH);

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
        txtNome.setText(cliente.getNome());
        if (cliente.getNascimento() != null) txtNascimento.setText(cliente.getNascimento().format(df));
        chkAtivo.setSelected(cliente.isAtivo());
        chkFisica.setSelected(cliente.isFisica());
        chkJuridica.setSelected(cliente.isJuridica());
        txtCpf.setText(cliente.getCpf());
        txtRg.setText(cliente.getRg());
        txtCnpj.setText(cliente.getCnpj());
        txtIe.setText(cliente.getInscricaoestadual());
        txtNomeFantasia.setText(cliente.getNomefantasia());

        txtEndereco.setText(cliente.getEndereco());
        txtNumero.setText(cliente.getNumero());
        txtBairro.setText(cliente.getBairro());
        txtCidade.setText(cliente.getCidade());
        txtEstado.setText(cliente.getEstado());
        txtCep.setText(cliente.getCep());
        txtComplemento.setText(cliente.getComplemento());
        txtPontoRef.setText(cliente.getPontoreferencia());
        txtPais.setText(cliente.getPais());
        chkEstrangeiro.setSelected(cliente.isEstrangeiro());

        txtEmail.setText(cliente.getEmail());
        txtTelefone.setText(cliente.getTelefone());
        txtCelular.setText(cliente.getCelular());
        txtFax.setText(cliente.getFax());
        chkConvenio.setSelected(cliente.isPermiteconveniovinculado());
        chkConsumidorFinal.setSelected(cliente.isConsumidorfinal());
        txtObservacoes.setText(cliente.getObservacoes());
    }

    private void salvar() {
        try {
            cliente.setNome(txtNome.getText());
            if (!txtNascimento.getText().equals("dd/mm/aaaa") && !txtNascimento.getText().isEmpty()) {
                cliente.setNascimento(LocalDate.parse(txtNascimento.getText(), df));
            }
            cliente.setAtivo(chkAtivo.isSelected());
            cliente.setFisica(chkFisica.isSelected());
            cliente.setJuridica(chkJuridica.isSelected());
            cliente.setCpf(txtCpf.getText());
            cliente.setRg(txtRg.getText());
            cliente.setCnpj(txtCnpj.getText());
            cliente.setInscricaoestadual(txtIe.getText());
            cliente.setNomefantasia(txtNomeFantasia.getText());

            cliente.setEndereco(txtEndereco.getText());
            cliente.setNumero(txtNumero.getText());
            cliente.setBairro(txtBairro.getText());
            cliente.setCidade(txtCidade.getText());
            cliente.setEstado(txtEstado.getText());
            cliente.setCep(txtCep.getText());
            cliente.setComplemento(txtComplemento.getText());
            cliente.setPontoreferencia(txtPontoRef.getText());
            cliente.setPais(txtPais.getText());
            cliente.setEstrangeiro(chkEstrangeiro.isSelected());

            cliente.setEmail(txtEmail.getText());
            cliente.setTelefone(txtTelefone.getText());
            cliente.setCelular(txtCelular.getText());
            cliente.setFax(txtFax.getText());
            cliente.setPermiteconveniovinculado(chkConvenio.isSelected());
            cliente.setConsumidorfinal(chkConsumidorFinal.isSelected());
            cliente.setObservacoes(txtObservacoes.getText());

            if (cliente.getId() == null) {
                service.salvar(cliente);
            } else {
                service.atualizar(cliente);
            }
            
            salvo = true;
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage());
        }
    }

    public boolean isSalvo() { return salvo; }
}
