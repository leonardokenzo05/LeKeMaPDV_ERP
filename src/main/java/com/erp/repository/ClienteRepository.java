package com.erp.repository;

import com.erp.model.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository {
    Cliente salvar(Cliente cliente);
    Optional<Cliente> buscarPorId(Long id);
    Optional<Cliente> buscarPorCpf(String cpf);
    List<Cliente> buscarTodos();
    void deletar(Long id);
    Cliente atualizar(Cliente cliente);
}
