package com.erp.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "produto")
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(nullable = false)
    private Integer estoque;

    @Column(name = "estoque_minimo")
    private Integer estoqueMinimo;

    @Column(name = "estoque_maximo")
    private Integer estoqueMaximo;

    @Column(name = "codigobarras")
    private String codigoBarras;

    @Column(length = 8)
    private String ncm;

    @Column(name = "descricaoresumida", length = 60)
    private String descricaoResumida;

    @Column(name = "unidademedida", length = 2)
    private String unidadeMedida;

    @Column(name = "precocompra")
    private Double precoCompra;

    @Column(name = "margemlucro")
    private Double margemLucro;

    @Column(name = "permitirdesconto")
    private boolean permitirDesconto;

    private Double desconto;

    @Column(nullable = false)
    private boolean ativo = true;

    private Long validade;

    @Column(length = 4)
    private String cfop;

    @Column(name = "codigointerno")
    private String codigoInterno;

    @ManyToOne
    @JoinColumn(name = "fornecedor_id")
    private Fornecedor fornecedor;

    @Column(name = "datacadastro")
    private LocalDate dataCadastro;

    public Produto() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Fornecedor getFornecedor() { return fornecedor; }
    public void setFornecedor(Fornecedor fornecedor) { this.fornecedor = fornecedor; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public Integer getEstoque() { return estoque; }
    public void setEstoque(Integer estoque) { this.estoque = estoque; }

    public Integer getEstoqueMinimo() { return estoqueMinimo; }
    public void setEstoqueMinimo(Integer estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; }

    public Integer getEstoqueMaximo() { return estoqueMaximo; }
    public void setEstoqueMaximo(Integer estoqueMaximo) { this.estoqueMaximo = estoqueMaximo; }

    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public String getNcm() { return ncm; }
    public void setNcm(String ncm) { this.ncm = ncm; }

    public String getDescricaoResumida() { return descricaoResumida; }
    public void setDescricaoResumida(String descricaoResumida) { this.descricaoResumida = descricaoResumida; }

    public String getUnidadeMedida() { return unidadeMedida; }
    public void setUnidadeMedida(String unidadeMedida) { this.unidadeMedida = unidadeMedida; }

    public Double getPrecoCompra() { return precoCompra; }
    public void setPrecoCompra(Double precoCompra) { this.precoCompra = precoCompra; }

    public Double getMargemLucro() { return margemLucro; }
    public void setMargemLucro(Double margemLucro) { this.margemLucro = margemLucro; }

    public boolean isPermitirDesconto() { return permitirDesconto; }
    public void setPermitirDesconto(boolean permitirDesconto) { this.permitirDesconto = permitirDesconto; }

    public Double getDesconto() { return desconto; }
    public void setDesconto(Double desconto) { this.desconto = desconto; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public Long getValidade() { return validade; }
    public void setValidade(Long validade) { this.validade = validade; }

    public String getCfop() { return cfop; }
    public void setCfop(String cfop) { this.cfop = cfop; }

    public String getCodigoInterno() { return codigoInterno; }
    public void setCodigoInterno(String codigoInterno) { this.codigoInterno = codigoInterno; }

    public LocalDate getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDate dataCadastro) { this.dataCadastro = dataCadastro; }

    @Override
    public String toString() {
        return "Produto{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", preco=" + preco +
                ", estoque=" + estoque +
                ", ativo=" + ativo +
                '}';
    }
}
