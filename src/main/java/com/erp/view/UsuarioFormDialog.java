package com.erp.view;

import com.erp.model.PermissaoUsuario;
import com.erp.model.Usuario;
import com.erp.service.UsuarioService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UsuarioFormDialog extends JDialog {
    private final UsuarioService service;
    private final Usuario usuario;
    private JTextField txtLogin, txtNome;
    private JPasswordField txtSenha;
    private JCheckBox chkAtivo;
    private List<JCheckBox> chkPermissoes;
    private boolean salvo = false;

    public UsuarioFormDialog(Frame owner, Usuario usuario, UsuarioService service) {
        super(owner, usuario == null ? "Novo Usuário" : "Editar Usuário", true);
        this.service = service;
        this.usuario = usuario != null ? usuario : new Usuario();
        this.chkPermissoes = new ArrayList<>();

        setSize(500, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        mainPanel.add(createFieldsPanel());
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(createPermissionsPanel());

        add(new JScrollPane(mainPanel), BorderLayout.CENTER);
        add(createActionPanel(), BorderLayout.SOUTH);

        if (usuario != null) {
            preencherCampos();
        }
    }

    private JPanel createFieldsPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new TitledBorder("Dados Básicos"));

        panel.add(new JLabel("Login:"));
        txtLogin = new JTextField();
        panel.add(txtLogin);

        panel.add(new JLabel("Senha:"));
        txtSenha = new JPasswordField();
        panel.add(txtSenha);

        panel.add(new JLabel("Nome:"));
        txtNome = new JTextField();
        panel.add(txtNome);

        panel.add(new JLabel("Ativo:"));
        chkAtivo = new JCheckBox();
        chkAtivo.setSelected(true);
        chkAtivo.setBackground(Color.WHITE);
        panel.add(chkAtivo);

        return panel;
    }

    private JPanel createPermissionsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new TitledBorder("Permissões de Acesso"));

        String[] permissoesDisponiveis = {
            "CLIENTES", "FORNECEDORES", "PRODUTOS", "FORMAS_PAGAMENTO", "VENDAS", "USUARIOS"
        };

        for (String perm : permissoesDisponiveis) {
            JCheckBox chk = new JCheckBox(perm);
            chk.setBackground(Color.WHITE);
            chkPermissoes.add(chk);
            panel.add(chk);
        }

        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(new Color(245, 245, 245));

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.setBackground(new Color(46, 204, 113));
        btnSalvar.setForeground(Color.WHITE);
        
        JButton btnCancelar = new JButton("Cancelar");

        btnSalvar.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose());

        panel.add(btnSalvar);
        panel.add(btnCancelar);

        return panel;
    }

    private void preencherCampos() {
        txtLogin.setText(usuario.getLogin());
        txtSenha.setText(usuario.getSenha());
        txtNome.setText(usuario.getNome());
        chkAtivo.setSelected(usuario.isAtivo());

        if (usuario.isTecnico()) {
            txtLogin.setEditable(false);
            txtSenha.setEditable(false);
            chkAtivo.setEnabled(false);
            for (JCheckBox chk : chkPermissoes) {
                chk.setSelected(true);
                chk.setEnabled(false);
            }
        } else {
            List<String> userPerms = usuario.getPermissoes().stream()
                    .map(PermissaoUsuario::getNomePermissao)
                    .collect(Collectors.toList());
            
            for (JCheckBox chk : chkPermissoes) {
                if (userPerms.contains(chk.getText())) {
                    chk.setSelected(true);
                }
            }
        }
    }

    private void salvar() {
        try {
            if (txtLogin.getText().isEmpty() || txtNome.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos obrigatórios.");
                return;
            }

            usuario.setLogin(txtLogin.getText());
            usuario.setSenha(new String(txtSenha.getPassword()));
            usuario.setNome(txtNome.getText());
            usuario.setAtivo(chkAtivo.isSelected());

            if (!usuario.isTecnico()) {
                usuario.getPermissoes().clear();
                for (JCheckBox chk : chkPermissoes) {
                    if (chk.isSelected()) {
                        usuario.addPermissao(chk.getText());
                    }
                }
            }

            if (usuario.getId() == null) {
                service.salvar(usuario);
            } else {
                service.atualizar(usuario);
            }

            salvo = true;
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar usuário: " + e.getMessage());
        }
    }

    public boolean isSalvo() {
        return salvo;
    }
}
