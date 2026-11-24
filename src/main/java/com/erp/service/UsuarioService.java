package com.erp.service;

import com.erp.model.Usuario;
import com.erp.repository.UsuarioRepository;
import com.erp.repository.UsuarioRepositoryImpl;

import java.util.List;
import java.util.Optional;

public class UsuarioService {
    private final UsuarioRepository repository;

    public UsuarioService() {
        this.repository = new UsuarioRepositoryImpl();
        garantirUsuarioTecnico();
    }

    private void garantirUsuarioTecnico() {
        Optional<Usuario> tecnico = repository.buscarPorLogin("1512");
        if (!tecnico.isPresent()) {
            Usuario u = new Usuario();
            u.setLogin("1512");
            u.setSenha("151204");
            u.setNome("USUÁRIO TÉCNICO");
            u.setAtivo(true);
            u.setTecnico(true);
            repository.salvar(u);
        }
    }

    public Usuario salvar(Usuario usuario) {
        validarLoginUnico(usuario.getLogin());
        return repository.salvar(usuario);
    }

    public List<Usuario> buscarTodos() {
        return repository.buscarTodos();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return repository.buscarPorId(id);
    }

    public void deletar(Long id) {
        repository.deletar(id);
    }

    public Usuario atualizar(Usuario usuario) {
        if (usuario.isTecnico() && !"1512".equals(usuario.getLogin())) {
             throw new RuntimeException("Dados do usuário técnico não podem ser alterados.");
        }
        
        Optional<Usuario> existente = repository.buscarPorLogin(usuario.getLogin());
        if (existente.isPresent() && !existente.get().getId().equals(usuario.getId())) {
            throw new RuntimeException("Login já cadastrado para outro usuário");
        }
        return repository.atualizar(usuario);
    }

    public Optional<Usuario> autenticar(String login, String senha) {
        Optional<Usuario> usuarioOpt = repository.buscarPorLogin(login);
        if (usuarioOpt.isPresent()) {
            Usuario u = usuarioOpt.get();
            if (u.isAtivo() && u.getSenha().equals(senha)) {
                return Optional.of(u);
            }
        }
        return Optional.empty();
    }

    public boolean temUsuariosCadastrados() {
        // O usuário técnico sempre existe; o login só é exigido quando há outros usuários.
        return repository.contarUsuarios() > 1;
    }

    private void validarLoginUnico(String login) {
        if (repository.buscarPorLogin(login).isPresent()) {
            throw new RuntimeException("Login já cadastrado");
        }
    }
}
