package com.erp.model;

import javax.persistence.*;

@Entity
@Table(name = "permissao_usuario")
public class PermissaoUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "nome_permissao", nullable = false)
    private String nomePermissao;

    public PermissaoUsuario() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getNomePermissao() { return nomePermissao; }
    public void setNomePermissao(String nomePermissao) { this.nomePermissao = nomePermissao; }
}
