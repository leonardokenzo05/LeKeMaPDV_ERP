package com.erp.repository;

import com.erp.model.Cliente;
import com.erp.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClienteRepositoryImpl implements ClienteRepository {
    private static final Logger LOGGER = Logger.getLogger(ClienteRepositoryImpl.class.getName());

    @Override
    public Cliente salvar(Cliente cliente) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(cliente);
            transaction.commit();
            return cliente;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            LOGGER.log(Level.SEVERE, "Erro ao salvar cliente", e);
            throw new RuntimeException("Erro ao salvar cliente", e);
        }
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Cliente.class, id));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar cliente por ID", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Cliente> buscarPorCpf(String cpf) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Cliente> query = session.createQuery("from Cliente where cpf = :cpf", Cliente.class);
            query.setParameter("cpf", cpf);
            return query.uniqueResultOptional();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar cliente por CPF", e);
            return Optional.empty();
        }
    }

    @Override
    public List<Cliente> buscarTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Cliente", Cliente.class).list();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar todos os clientes", e);
            return List.of();
        }
    }

    @Override
    public void deletar(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Cliente cliente = session.get(Cliente.class, id);
            if (cliente != null) {
                session.delete(cliente);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            LOGGER.log(Level.SEVERE, "Erro ao deletar cliente", e);
            throw new RuntimeException("Erro ao deletar cliente", e);
        }
    }

    @Override
    public Cliente atualizar(Cliente cliente) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(cliente);
            transaction.commit();
            return cliente;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            LOGGER.log(Level.SEVERE, "Erro ao atualizar cliente", e);
            throw new RuntimeException("Erro ao atualizar cliente", e);
        }
    }
}
