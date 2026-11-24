package com.erp.view;

import com.erp.model.Usuario;
import com.erp.service.UsuarioService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Optional;

public class LoginDialog extends JDialog {
    private final UsuarioService usuarioService;
    private JTextField txtLogin;
    private JPasswordField txtSenha;
    private boolean autenticado = false;
    private Usuario usuarioLogado;

    public LoginDialog(Frame owner) {
        super(owner, "Login - Sistema ERP", true);
        this.usuarioService = new UsuarioService();
        
        setLayout(new BorderLayout());
        setSize(400, 300);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        add(createMainPanel());
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel lblTitle = new JLabel("Bem-vindo ao Sistema");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        panel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel lblLogin = new JLabel("Login:");
        lblLogin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(lblLogin, gbc);

        txtLogin = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(txtLogin, gbc);

        JLabel lblSenha = new JLabel("Senha:");
        lblSenha.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(lblSenha, gbc);

        txtSenha = new JPasswordField(15);
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(txtSenha, gbc);

        JButton btnLogin = new JButton("Entrar");
        btnLogin.setBackground(new Color(52, 152, 219));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setFocusPainted(false);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 0, 0);
        panel.add(btnLogin, gbc);

        btnLogin.addActionListener(e -> realizarLogin());
        txtSenha.addActionListener(e -> realizarLogin());

        return panel;
    }

    private void realizarLogin() {
        String login = txtLogin.getText();
        String senha = new String(txtSenha.getPassword());

        if (login.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe login e senha.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Optional<Usuario> usuarioOpt = usuarioService.autenticar(login, senha);
        if (usuarioOpt.isPresent()) {
            this.usuarioLogado = usuarioOpt.get();
            this.autenticado = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Login ou senha inválidos.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isAutenticado() {
        return autenticado;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }
}
