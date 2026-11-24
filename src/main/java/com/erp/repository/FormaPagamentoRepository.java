package com.erp.repository;

import com.erp.model.FormaPagamento;
import java.util.List;
import java.util.Optional;

public interface FormaPagamentoRepository {
    FormaPagamento salvar(FormaPagamento formaPagamento);
    Optional<FormaPagamento> buscarPorId(Long id);
    Optional<FormaPagamento> buscarPorDescricao(String descricao);
    List<FormaPagamento> buscarTodos();
    void deletar(Long id);
    FormaPagamento atualizar(FormaPagamento formaPagamento);
}
