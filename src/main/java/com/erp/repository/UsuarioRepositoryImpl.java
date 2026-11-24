package com.erp.repository;

import com.erp.model.Usuario;
import com.erp.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsuarioRepositoryImpl implements UsuarioRepository {
    private static final Logger LOGGER = Logger.getLogger(UsuarioRepositoryImpl.class.getName());

    @Override
    public Usuario salvar(Usuario usuario) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(usuario);
            transaction.commit();
            return usuario;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            LOGGER.log(Level.SEVERE, "Erro ao salvar usuário", e);
            throw new RuntimeException("Erro ao salvar usuário", e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Usuario.class, id));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar usuário por ID", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Usuario> buscarPorLogin(String login) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Usuario> query = session.createQuery("from Usuario where login = :login", Usuario.class);
            query.setParameter("login", login);
            return query.uniqueResultOptional();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar usuário por login", e);
            return Optional.empty();
        }
    }

    @Override
    public List<Usuario> buscarTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Usuario", Usuario.class).list();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar todos os usuários", e);
            throw new RuntimeException("Erro ao buscar usuários", e);
        }
    }

    @Override
    public void deletar(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Usuario usuario = session.get(Usuario.class, id);
            if (usuario != null) {
                if (usuario.isTecnico()) {
                    throw new RuntimeException("Usuário técnico não pode ser excluído");
                }
                session.delete(usuario);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            LOGGER.log(Level.SEVERE, "Erro ao deletar usuário", e);
            throw new RuntimeException("Erro ao deletar usuário", e);
        }
    }

    @Override
    public Usuario atualizar(Usuario usuario) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(usuario);
            transaction.commit();
            return usuario;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            LOGGER.log(Level.SEVERE, "Erro ao atualizar usuário", e);
            throw new RuntimeException("Erro ao atualizar usuário", e);
        }
    }

    @Override
    public long contarUsuarios() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("select count(u) from Usuario u", Long.class).uniqueResult();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao contar usuários", e);
            return 0;
        }
    }
}
