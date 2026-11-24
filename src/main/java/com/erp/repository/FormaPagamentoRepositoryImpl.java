package com.erp.repository;

import com.erp.model.FormaPagamento;
import com.erp.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FormaPagamentoRepositoryImpl implements FormaPagamentoRepository {
    private static final Logger LOGGER = Logger.getLogger(FormaPagamentoRepositoryImpl.class.getName());

    @Override
    public FormaPagamento salvar(FormaPagamento formaPagamento) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(formaPagamento);
            transaction.commit();
            return formaPagamento;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            LOGGER.log(Level.SEVERE, "Erro ao salvar forma de pagamento", e);
            throw new RuntimeException("Erro ao salvar forma de pagamento", e);
        }
    }

    @Override
    public Optional<FormaPagamento> buscarPorId(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(FormaPagamento.class, id));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar forma de pagamento por ID", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<FormaPagamento> buscarPorDescricao(String descricao) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<FormaPagamento> query = session.createQuery("from FormaPagamento where lower(trim(descricao)) = lower(trim(:descricao))", FormaPagamento.class);
            query.setParameter("descricao", descricao);
            return query.uniqueResultOptional();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar forma de pagamento por descrição", e);
            return Optional.empty();
        }
    }

    @Override
    public List<FormaPagamento> buscarTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from FormaPagamento", FormaPagamento.class).list();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar todas as formas de pagamento", e);
            return List.of();
        }
    }

    @Override
    public void deletar(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            FormaPagamento formaPagamento = session.get(FormaPagamento.class, id);
            if (formaPagamento != null) {
                session.delete(formaPagamento);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            LOGGER.log(Level.SEVERE, "Erro ao deletar forma de pagamento", e);
            throw new RuntimeException("Erro ao deletar forma de pagamento", e);
        }
    }

    @Override
    public FormaPagamento atualizar(FormaPagamento formaPagamento) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(formaPagamento);
            transaction.commit();
            return formaPagamento;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            LOGGER.log(Level.SEVERE, "Erro ao atualizar forma de pagamento", e);
            throw new RuntimeException("Erro ao atualizar forma de pagamento", e);
        }
    }
}
