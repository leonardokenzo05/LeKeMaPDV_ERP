package com.erp.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(nullable = false)
    private boolean tecnico = false;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PermissaoUsuario> permissoes = new ArrayList<>();

    public Usuario() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public boolean isTecnico() { return tecnico; }
    public void setTecnico(boolean tecnico) { this.tecnico = tecnico; }

    public List<PermissaoUsuario> getPermissoes() { return permissoes; }
    public void setPermissoes(List<PermissaoUsuario> permissoes) { 
        this.permissoes = permissoes; 
        if (permissoes != null) {
            for (PermissaoUsuario p : permissoes) {
                p.setUsuario(this);
            }
        }
    }

    public void addPermissao(String nomePermissao) {
        PermissaoUsuario p = new PermissaoUsuario();
        p.setNomePermissao(nomePermissao);
        p.setUsuario(this);
        permissoes.add(p);
    }
}
