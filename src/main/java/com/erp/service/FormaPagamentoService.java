package com.erp.service;

import com.erp.model.FormaPagamento;
import com.erp.repository.FormaPagamentoRepository;
import com.erp.repository.FormaPagamentoRepositoryImpl;

import java.util.List;
import java.util.Optional;

public class FormaPagamentoService {
    private final FormaPagamentoRepository repository;

    public FormaPagamentoService() {
        this.repository = new FormaPagamentoRepositoryImpl();
    }

    public FormaPagamento salvar(FormaPagamento formaPagamento) {
        validarDescricaoUnica(formaPagamento);
        return repository.salvar(formaPagamento);
    }

    public List<FormaPagamento> buscarTodos() {
        return repository.buscarTodos();
    }

    public List<FormaPagamento> buscarAtivas() {
        return repository.buscarTodos().stream()
                .filter(FormaPagamento::isAtivo)
                .collect(java.util.stream.Collectors.toList());
    }

    public void deletar(Long id) {
        repository.deletar(id);
    }

    public FormaPagamento atualizar(FormaPagamento formaPagamento) {
        validarDescricaoUnica(formaPagamento);
        return repository.atualizar(formaPagamento);
    }

    private void validarDescricaoUnica(FormaPagamento formaPagamento) {
        if (formaPagamento.getDescricao() == null || formaPagamento.getDescricao().trim().isEmpty()) {
            throw new RuntimeException("Descrição obrigatória");
        }

        Optional<FormaPagamento> existente = repository.buscarPorDescricao(formaPagamento.getDescricao());
        if (existente.isPresent() && !existente.get().getId().equals(formaPagamento.getId())) {
            throw new RuntimeException("Já existe uma forma de pagamento com esta descrição");
        }
    }
}
