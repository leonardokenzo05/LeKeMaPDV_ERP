package com.erp.repository;

import com.erp.model.Fornecedor;
import java.util.List;
import java.util.Optional;

public interface FornecedorRepository {
    Fornecedor salvar(Fornecedor fornecedor);
    Optional<Fornecedor> buscarPorId(Long id);
    Optional<Fornecedor> buscarPorCpfCnpj(String cpfCnpj);
    List<Fornecedor> buscarTodos();
    void deletar(Long id);
    Fornecedor atualizar(Fornecedor fornecedor);
}
