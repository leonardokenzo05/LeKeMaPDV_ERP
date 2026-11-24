package com.erp.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "cliente")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codcadastroclientes")
    private Long id;

    @Column(nullable = false)
    private String nome;

    private LocalDate nascimento;
    
    @Column(nullable = false)
    private boolean ativo = true;

    private String endereco;
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;
    private String complemento;
    private String cep;
    private String email;
    private String pontoreferencia;
    private String telefone;
    private String celular;
    private String fax;

    @Column(nullable = false)
    private boolean fisica = true;

    @Column(nullable = false)
    private boolean juridica = false;

    @Column(unique = true, length = 14)
    private String cpf;

    private String rg;
    private String cnpj;
    private String inscricaoestadual;

    @Column(length = 1000)
    private String observacoes;

    @Column(nullable = false)
    private LocalDate datacadastro = LocalDate.now();

    private String nomefantasia;
    private boolean permiteconveniovinculado = false;
    private String pais;
    private boolean estrangeiro = false;
    private boolean consumidorfinal = false;

    public Cliente() {}

    // Construtor completo para facilitar
    public Cliente(Long id, String nome, String cpf, String email, String telefone) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public LocalDate getNascimento() { return nascimento; }
    public void setNascimento(LocalDate nascimento) { this.nascimento = nascimento; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPontoreferencia() { return pontoreferencia; }
    public void setPontoreferencia(String pontoreferencia) { this.pontoreferencia = pontoreferencia; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }

    public String getFax() { return fax; }
    public void setFax(String fax) { this.fax = fax; }

    public boolean isFisica() { return fisica; }
    public void setFisica(boolean fisica) { this.fisica = fisica; }

    public boolean isJuridica() { return juridica; }
    public void setJuridica(boolean juridica) { this.juridica = juridica; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getRg() { return rg; }
    public void setRg(String rg) { this.rg = rg; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getInscricaoestadual() { return inscricaoestadual; }
    public void setInscricaoestadual(String inscricaoestadual) { this.inscricaoestadual = inscricaoestadual; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public LocalDate getDatacadastro() { return datacadastro; }
    public void setDatacadastro(LocalDate datacadastro) { this.datacadastro = datacadastro; }

    public String getNomefantasia() { return nomefantasia; }
    public void setNomefantasia(String nomefantasia) { this.nomefantasia = nomefantasia; }

    public boolean isPermiteconveniovinculado() { return permiteconveniovinculado; }
    public void setPermiteconveniovinculado(boolean permiteconveniovinculado) { this.permiteconveniovinculado = permiteconveniovinculado; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public boolean isEstrangeiro() { return estrangeiro; }
    public void setEstrangeiro(boolean estrangeiro) { this.estrangeiro = estrangeiro; }

    public boolean isConsumidorfinal() { return consumidorfinal; }
    public void setConsumidorfinal(boolean consumidorfinal) { this.consumidorfinal = consumidorfinal; }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", cpf='" + cpf + '\'' +
                ", ativo=" + ativo +
                '}';
    }
}
