package com.erp.service;

import com.erp.model.Cliente;
import com.erp.repository.ClienteRepository;
import com.erp.repository.ClienteRepositoryImpl;

import java.util.List;
import java.util.Optional;

public class ClienteService {
    private final ClienteRepository repository;

    public ClienteService() {
        this.repository = new ClienteRepositoryImpl();
    }

    public Cliente salvar(Cliente cliente) {
        validarCpfUnico(cliente.getCpf());
        validarNomeObrigatorio(cliente.getNome());
        return repository.salvar(cliente);
    }

    public List<Cliente> buscarTodos() {
        return repository.buscarTodos();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return repository.buscarPorId(id);
    }

    public void deletar(Long id) {
        repository.deletar(id);
    }

    public Cliente atualizar(Cliente cliente) {
        validarNomeObrigatorio(cliente.getNome());
        // Se o CPF mudou, validar se o novo CPF já existe
        Optional<Cliente> existente = repository.buscarPorCpf(cliente.getCpf());
        if (existente.isPresent() && !existente.get().getId().equals(cliente.getId())) {
            throw new RuntimeException("CPF já cadastrado para outro cliente");
        }
        return repository.atualizar(cliente);
    }

    private void validarCpfUnico(String cpf) {
        if (repository.buscarPorCpf(cpf).isPresent()) {
            throw new RuntimeException("CPF já cadastrado");
        }
    }

    private void validarNomeObrigatorio(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new RuntimeException("Nome é obrigatório");
        }
    }
}
