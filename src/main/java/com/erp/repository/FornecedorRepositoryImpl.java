package com.erp.repository;

import com.erp.model.Fornecedor;
import com.erp.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FornecedorRepositoryImpl implements FornecedorRepository {
    private static final Logger LOGGER = Logger.getLogger(FornecedorRepositoryImpl.class.getName());

    @Override
    public Fornecedor salvar(Fornecedor fornecedor) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(fornecedor);
            transaction.commit();
            return fornecedor;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            LOGGER.log(Level.SEVERE, "Erro ao salvar fornecedor", e);
            throw new RuntimeException("Erro ao salvar fornecedor", e);
        }
    }

    @Override
    public Optional<Fornecedor> buscarPorId(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Fornecedor.class, id));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar fornecedor por ID", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Fornecedor> buscarPorCpfCnpj(String cpfCnpj) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Fornecedor> query = session.createQuery("from Fornecedor where cpfCnpj = :cpfCnpj", Fornecedor.class);
            query.setParameter("cpfCnpj", cpfCnpj);
            return query.uniqueResultOptional();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar fornecedor por CPF/CNPJ", e);
            return Optional.empty();
        }
    }

    @Override
    public List<Fornecedor> buscarTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Fornecedor", Fornecedor.class).list();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar todos os fornecedores", e);
            return List.of();
        }
    }

    @Override
    public void deletar(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Fornecedor fornecedor = session.get(Fornecedor.class, id);
            if (fornecedor != null) {
                session.delete(fornecedor);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            LOGGER.log(Level.SEVERE, "Erro ao deletar fornecedor", e);
            throw new RuntimeException("Erro ao deletar fornecedor", e);
        }
    }

    @Override
    public Fornecedor atualizar(Fornecedor fornecedor) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(fornecedor);
            transaction.commit();
            return fornecedor;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            LOGGER.log(Level.SEVERE, "Erro ao atualizar fornecedor", e);
            throw new RuntimeException("Erro ao atualizar fornecedor", e);
        }
    }
}
