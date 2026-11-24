package com.erp.view;

import com.erp.model.PermissaoUsuario;
import com.erp.model.Usuario;
import com.erp.service.UsuarioService;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private DashboardView dashboardView;
    private Usuario usuarioLogado;
    private UsuarioService usuarioService;
    
    // Cores baseadas no screenshot
    private final Color COLOR_HEADER = new Color(0, 51, 102); // Azul Escuro
    private final Color COLOR_SIDEBAR = new Color(45, 45, 45); // Cinza Escuro
    private final Color COLOR_SIDEBAR_HOVER = new Color(60, 60, 60);

    public MainFrame(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        this.usuarioService = new UsuarioService();
        setupLookAndFeel();
        setTitle("LekemaPDV - ERP");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout());

        // Header
        add(createHeader(), BorderLayout.NORTH);

        // Sidebar
        add(createSidebar(), BorderLayout.WEST);

        // Main Content Area (CardLayout para trocar entre views)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);
        
        // Adicionando as views iniciais
        dashboardView = new DashboardView();
        contentPanel.add(dashboardView, "Dashboard");
        
        add(contentPanel, BorderLayout.CENTER);

        setupStatusBar();
    }

    private boolean temPermissao(String permissao) {
        if (usuarioLogado == null || usuarioLogado.isTecnico()) {
            return true;
        }
        return usuarioLogado.getPermissoes().stream()
                .anyMatch(p -> p.getNomePermissao().equals(permissao));
    }

    public void refreshDashboard() {
        if (dashboardView != null) {
            dashboardView.refresh();
        }
    }

    private void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            // Customizações globais
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
        } catch (Exception ex) {
            System.err.println("Falha ao inicializar FlatLaf");
        }
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_HEADER);
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(new EmptyBorder(0, 20, 0, 20));

        JLabel title = new JLabel("LeKeMaPDV - ERP");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.add(title, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        rightPanel.setOpaque(false);

        if (usuarioLogado != null) {
            JLabel lblUser = new JLabel("Usuário: " + usuarioLogado.getNome());
            lblUser.setForeground(Color.WHITE);
            lblUser.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            rightPanel.add(lblUser);
        }

        JButton btnSessao = new JButton("Sessão ▼");
        btnSessao.setBackground(Color.WHITE);
        btnSessao.setForeground(COLOR_HEADER);
        btnSessao.setFocusPainted(false);
        
        JPopupMenu menuSessao = new JPopupMenu();
        JMenuItem itemLogoff = new JMenuItem("Fazer Logoff");
        JMenuItem itemSair = new JMenuItem("Sair do Sistema");
        
        itemLogoff.addActionListener(e -> logoff());
        itemSair.addActionListener(e -> confirmarSaida());
        
        menuSessao.add(itemLogoff);
        menuSessao.add(itemSair);
        
        btnSessao.addActionListener(e -> {
            menuSessao.show(btnSessao, 0, btnSessao.getHeight());
        });
        rightPanel.add(btnSessao);

        header.add(rightPanel, BorderLayout.EAST);
        return header;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(COLOR_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(new EmptyBorder(20, 0, 0, 0));

        sidebar.add(createSidebarButton("Dashboard", "Dashboard"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        
        if (temPermissao("CLIENTES")) {
            sidebar.add(createSidebarButton("Clientes", "Clientes"));
            sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        
        if (temPermissao("PRODUTOS")) {
            sidebar.add(createSidebarButton("Produtos", "Produtos"));
            sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        
        // Seção expansível: Cadastros Gerais
        List<String> subItems = new ArrayList<>();
        if (temPermissao("FORMAS_PAGAMENTO")) subItems.add("Formas de pagamento");
        if (temPermissao("FORNECEDORES")) subItems.add("Fornecedores");
        if (temPermissao("USUARIOS")) subItems.add("Usuários");
        
        if (!subItems.isEmpty()) {
            sidebar.add(createExpandableSection("Cadastros Gerais", subItems.toArray(new String[0])));
            sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        
        if (temPermissao("VENDAS")) {
            sidebar.add(createSidebarButton("Vendas", "Vendas"));
        }

        sidebar.add(Box.createVerticalGlue());

        return sidebar;
    }

    private JPanel createExpandableSection(String title, String... subItems) {
        JPanel sectionPanel = new JPanel() {
            @Override
            public Dimension getMaximumSize() {
                Dimension pref = getPreferredSize();
                return new Dimension(220, pref.height);
            }
        };
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setOpaque(false);

        JButton btnToggle = createSidebarButton(title + "  ▼", null);
        JPanel subMenuPanel = new JPanel();
        subMenuPanel.setLayout(new BoxLayout(subMenuPanel, BoxLayout.Y_AXIS));
        subMenuPanel.setOpaque(false);
        subMenuPanel.setVisible(false);

        for (String item : subItems) {
            JButton subBtn = createSidebarButton("   " + item, item);
            subBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            subMenuPanel.add(subBtn);
        }

        btnToggle.addActionListener(e -> {
            boolean visible = subMenuPanel.isVisible();
            subMenuPanel.setVisible(!visible);
            btnToggle.setText(title + (visible ? "  ▼" : "  ▲"));
            sectionPanel.revalidate();
            sectionPanel.repaint();
        });

        sectionPanel.add(btnToggle);
        sectionPanel.add(subMenuPanel);
        return sectionPanel;
    }

    private JButton createSidebarButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(220, 45));
        btn.setPreferredSize(new Dimension(220, 45));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(0, 20, 0, 0));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setContentAreaFilled(true);
                btn.setBackground(COLOR_SIDEBAR_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setContentAreaFilled(false);
            }
        });

        if (cardName != null) {
            btn.addActionListener(e -> {
                if (cardName.equals("Clientes")) abrirModuloClientes();
                else if (cardName.equals("Produtos")) abrirModuloProdutos();
                else if (cardName.equals("Vendas")) abrirModuloVendas();
                else if (cardName.equals("Formas de pagamento")) abrirModuloFormasPagamento();
                else if (cardName.equals("Fornecedores")) abrirModuloFornecedores();
                else if (cardName.equals("Usuários")) abrirModuloUsuarios();
                else cardLayout.show(contentPanel, cardName);
            });
        }

        return btn;
    }

    private void abrirModuloFormasPagamento() {
        contentPanel.add(new FormaPagamentoView(), "Formas de pagamento");
        cardLayout.show(contentPanel, "Formas de pagamento");
    }

    private void abrirModuloFornecedores() {
        contentPanel.add(new FornecedorView(), "Fornecedores");
        cardLayout.show(contentPanel, "Fornecedores");
    }

    private void abrirModuloUsuarios() {
        contentPanel.add(new UsuarioView(), "Usuários");
        cardLayout.show(contentPanel, "Usuários");
    }

    private void setupStatusBar() {
        String msg = " Sistema ERP pronto.";
        if (usuarioLogado != null) {
            msg += " Logado como: " + usuarioLogado.getNome();
        }
        JLabel statusBar = new JLabel(msg);
        statusBar.setBorder(new EmptyBorder(5, 10, 5, 10));
        statusBar.setOpaque(true);
        statusBar.setBackground(new Color(240, 240, 240));
        add(statusBar, BorderLayout.SOUTH);
    }

    private void logoff() {
        int resposta = JOptionPane.showConfirmDialog(
                this, "Deseja realmente fazer logoff?", "Confirmar Logoff",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (resposta == JOptionPane.YES_OPTION) {
            this.dispose();
            SwingUtilities.invokeLater(() -> {
                UsuarioService service = new UsuarioService();
                if (service.temUsuariosCadastrados()) {
                    LoginDialog loginDialog = new LoginDialog(null);
                    loginDialog.setVisible(true);
                    
                    if (loginDialog.isAutenticado()) {
                        MainFrame frame = new MainFrame(loginDialog.getUsuarioLogado());
                        frame.setVisible(true);
                    } else {
                        System.exit(0);
                    }
                } else {
                    MainFrame frame = new MainFrame(null);
                    frame.setVisible(true);
                }
            });
        }
    }

    private void confirmarSaida() {
        int resposta = JOptionPane.showConfirmDialog(
                this, "Deseja realmente sair do sistema?", "Confirmar saída",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (resposta == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void abrirModuloClientes() {
        contentPanel.add(new ClienteView(), "Clientes");
        cardLayout.show(contentPanel, "Clientes");
    }

    private void abrirModuloProdutos() {
        contentPanel.add(new ProdutoView(), "Produtos");
        cardLayout.show(contentPanel, "Produtos");
    }

    private void abrirModuloVendas() {
        contentPanel.add(new VendasView(), "Vendas");
        cardLayout.show(contentPanel, "Vendas");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UsuarioService service = new UsuarioService();
            if (service.temUsuariosCadastrados()) {
                LoginDialog loginDialog = new LoginDialog(null);
                loginDialog.setVisible(true);
                
                if (loginDialog.isAutenticado()) {
                    MainFrame frame = new MainFrame(loginDialog.getUsuarioLogado());
                    frame.setVisible(true);
                } else {
                    System.exit(0);
                }
            } else {
                MainFrame frame = new MainFrame(null);
                frame.setVisible(true);
            }
        });
    }
}
