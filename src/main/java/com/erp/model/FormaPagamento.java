package com.erp.model;

import javax.persistence.*;

@Entity
@Table(name = "forma_pagamento")
public class FormaPagamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String descricao;

    @Column(name = "permite_desconto", nullable = false)
    private boolean permiteDesconto;

    @Column(nullable = false)
    private boolean ativo = true;

    public FormaPagamento() {}

    public FormaPagamento(String descricao, boolean permiteDesconto) {
        this.descricao = descricao;
        this.permiteDesconto = permiteDesconto;
        this.ativo = true;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public boolean isPermiteDesconto() { return permiteDesconto; }
    public void setPermiteDesconto(boolean permiteDesconto) { this.permiteDesconto = permiteDesconto; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Override
    public String toString() {
        return descricao;
    }
}
