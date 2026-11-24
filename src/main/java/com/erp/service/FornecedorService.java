package com.erp.service;

import com.erp.model.Fornecedor;
import com.erp.repository.FornecedorRepository;
import com.erp.repository.FornecedorRepositoryImpl;

import java.util.List;
import java.util.Optional;

public class FornecedorService {
    private final FornecedorRepository repository;

    public FornecedorService() {
        this.repository = new FornecedorRepositoryImpl();
    }

    public Fornecedor salvar(Fornecedor fornecedor) {
        validarObrigatorios(fornecedor);
        validarDuplicidade(fornecedor.getCpfCnpj(), null);
        return repository.salvar(fornecedor);
    }

    public List<Fornecedor> buscarTodos() {
        return repository.buscarTodos();
    }

    public Optional<Fornecedor> buscarPorId(Long id) {
        return repository.buscarPorId(id);
    }

    public void deletar(Long id) {
        repository.deletar(id);
    }

    public Fornecedor atualizar(Fornecedor fornecedor) {
        validarObrigatorios(fornecedor);
        validarDuplicidade(fornecedor.getCpfCnpj(), fornecedor.getId());
        return repository.atualizar(fornecedor);
    }

    private void validarObrigatorios(Fornecedor f) {
        if (f.getNomeRazaoSocial() == null || f.getNomeRazaoSocial().trim().isEmpty()) {
            throw new RuntimeException("Nome/Razão Social é obrigatório");
        }
        if (f.getCpfCnpj() == null || f.getCpfCnpj().trim().isEmpty()) {
            String tipo = f.isFisica() ? "CPF" : "CNPJ";
            throw new RuntimeException(tipo + " é obrigatório");
        }
    }

    private void validarDuplicidade(String cpfCnpj, Long id) {
        Optional<Fornecedor> existente = repository.buscarPorCpfCnpj(cpfCnpj);
        if (existente.isPresent() && (id == null || !existente.get().getId().equals(id))) {
            throw new RuntimeException("Já existe um fornecedor cadastrado com este CPF/CNPJ");
        }
    }
}
