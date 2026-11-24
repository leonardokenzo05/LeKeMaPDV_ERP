package com.erp.service;

import com.erp.model.Produto;
import com.erp.repository.ProdutoRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ProdutoService {
    private final ProdutoRepository repository;

    public ProdutoService() {
        this.repository = new ProdutoRepository();
    }

    public Produto salvar(Produto produto) {
        processarDados(produto);
        validarProduto(produto);
        verificarDuplicados(produto);
        
        if (produto.getDataCadastro() == null) {
            produto.setDataCadastro(LocalDate.now());
        }
        
        return repository.salvar(produto);
    }

    public List<Produto> buscarTodos() {
        return repository.buscarTodos();
    }

    public Optional<Produto> buscarPorId(Long id) {
        return repository.buscarPorId(id);
    }

    public void deletar(Long id) {
        if (repository.existeVendaParaProduto(id)) {
            throw new RuntimeException("Nao e possivel deletar um produto que ja possui vendas.");
        }
        repository.deletar(id);
    }

    public Produto atualizar(Produto produto) {
        processarDados(produto);
        validarProduto(produto);
        
        Produto produtoExistente = repository.buscarPorId(produto.getId())
                .orElseThrow(() -> new RuntimeException("Produto nao encontrado"));

        // Regra: Nao permitir alterar codigo do produto apos fazer uma venda com ele
        if (repository.existeVendaParaProduto(produto.getId())) {
            if (!produtoExistente.getCodigoBarras().equals(produto.getCodigoBarras()) ||
                !produtoExistente.getCodigoInterno().equals(produto.getCodigoInterno())) {
                throw new RuntimeException("Nao e possivel alterar os codigos de um produto que ja possui vendas.");
            }
        }

        verificarDuplicados(produto);
        
        return repository.atualizar(produto);
    }

    private void processarDados(Produto produto) {
        if (produto.getNome() != null) {
            produto.setNome(produto.getNome().toUpperCase());
        }
        if (produto.getDescricaoResumida() != null) {
            produto.setDescricaoResumida(produto.getDescricaoResumida().toUpperCase());
        }
        
        // Geracao sequencial
        if (produto.getCodigoBarras() == null || produto.getCodigoBarras().trim().isEmpty()) {
            produto.setCodigoBarras(repository.buscarUltimoCodigoSequencial());
        }
        if (produto.getCodigoInterno() == null || produto.getCodigoInterno().trim().isEmpty()) {
            produto.setCodigoInterno(repository.buscarUltimoCodigoSequencial());
        }
    }

    private void verificarDuplicados(Produto produto) {
        repository.buscarPorCodigoBarras(produto.getCodigoBarras()).ifPresent(p -> {
            if (!p.getId().equals(produto.getId())) {
                throw new RuntimeException("Codigo de barras ja cadastrado: " + produto.getCodigoBarras());
            }
        });

        repository.buscarPorCodigoInterno(produto.getCodigoInterno()).ifPresent(p -> {
            if (!p.getId().equals(produto.getId())) {
                throw new RuntimeException("Codigo interno ja cadastrado: " + produto.getCodigoInterno());
            }
        });
    }

    public String gerarAlertaEstoque(Produto produto) {
        if (produto.getEstoqueMinimo() != null && produto.getEstoque() <= produto.getEstoqueMinimo()) {
            return "Atencao: o produto \"" + produto.getNome() + "\" chegou ao estoque minimo. "
                    + "Estoque atual: " + produto.getEstoque()
                    + " | Minimo: " + produto.getEstoqueMinimo();
        }
        if (produto.getEstoqueMaximo() != null && produto.getEstoque() >= produto.getEstoqueMaximo()) {
            return "Atencao: o produto \"" + produto.getNome() + "\" chegou ao estoque maximo. "
                    + "Estoque atual: " + produto.getEstoque()
                    + " | Maximo: " + produto.getEstoqueMaximo();
        }
        return null;
    }

    private void validarProduto(Produto produto) {
        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new RuntimeException("Nome do produto e obrigatorio");
        }
        if (produto.getDescricaoResumida() == null || produto.getDescricaoResumida().trim().isEmpty()) {
            throw new RuntimeException("Descricao resumida e obrigatoria");
        }
        if (produto.getDescricaoResumida().length() > 60) {
            throw new RuntimeException("Descricao resumida deve ter no maximo 60 caracteres");
        }
        if (produto.getPreco() == null || produto.getPreco().compareTo(new BigDecimal("0.01")) <= 0) {
            throw new RuntimeException("Preco de venda deve ser maior que 0.01");
        }
        if (produto.getUnidadeMedida() == null || (!produto.getUnidadeMedida().equals("UN") && !produto.getUnidadeMedida().equals("KG"))) {
            throw new RuntimeException("Unidade de medida deve ser UN ou KG");
        }
        if (produto.getNcm() != null && !produto.getNcm().isEmpty() && produto.getNcm().length() != 8) {
            throw new RuntimeException("NCM deve ter exatamente 8 caracteres");
        }
        if (produto.getCfop() != null && !produto.getCfop().isEmpty() && produto.getCfop().length() != 4) {
            throw new RuntimeException("CFOP deve ter exatamente 4 caracteres");
        }
        if (produto.getEstoque() == null || produto.getEstoque() < 0) {
            throw new RuntimeException("Estoque nao pode ser negativo");
        }
        if (produto.getPrecoCompra() == null || produto.getPrecoCompra() < 0) {
            throw new RuntimeException("Preco de compra nao pode ser negativo");
        }
    }
}
