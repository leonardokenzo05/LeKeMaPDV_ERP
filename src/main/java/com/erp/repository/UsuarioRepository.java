package com.erp.repository;

import com.erp.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {
    Usuario salvar(Usuario usuario);
    Optional<Usuario> buscarPorId(Long id);
    Optional<Usuario> buscarPorLogin(String login);
    List<Usuario> buscarTodos();
    void deletar(Long id);
    Usuario atualizar(Usuario usuario);
    long contarUsuarios();
}
