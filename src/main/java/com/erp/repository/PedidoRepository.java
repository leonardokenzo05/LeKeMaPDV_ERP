package com.erp.repository;

import com.erp.model.Pedido;
import com.erp.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PedidoRepository {
    private static final Logger LOGGER = Logger.getLogger(PedidoRepository.class.getName());

    public Pedido salvar(Pedido pedido) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(pedido);
            transaction.commit();
            return pedido;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            LOGGER.log(Level.SEVERE, "Erro ao salvar pedido", e);
            throw new RuntimeException("Erro ao salvar pedido", e);
        }
    }

    public Optional<Pedido> buscarPorId(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Fetch join para carregar itens e cliente para evitar LazyInitializationException
            return session.createQuery(
                    "select p from Pedido p " +
                    "join fetch p.cliente " +
                    "join fetch p.itens i " +
                    "join fetch i.produto " +
                    "where p.id = :id", Pedido.class)
                    .setParameter("id", id)
                    .uniqueResultOptional();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar pedido por ID", e);
            return Optional.empty();
        }
    }

    public List<Pedido> buscarTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Pedido", Pedido.class).list();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar todos os pedidos", e);
            return List.of();
        }
    }

    public long contarVendasDoDia() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return (long) session.createQuery(
                    "select count(p) from Pedido p where p.data >= :inicio and p.data <= :fim")
                    .setParameter("inicio", java.time.LocalDate.now().atStartOfDay())
                    .setParameter("fim", java.time.LocalDate.now().atTime(java.time.LocalTime.MAX))
                    .uniqueResult();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao contar vendas do dia", e);
            return 0;
        }
    }

    public java.math.BigDecimal somarVendasDoDia() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            java.math.BigDecimal total = (java.math.BigDecimal) session.createQuery(
                    "select sum(p.valorTotal) from Pedido p where p.data >= :inicio and p.data <= :fim")
                    .setParameter("inicio", java.time.LocalDate.now().atStartOfDay())
                    .setParameter("fim", java.time.LocalDate.now().atTime(java.time.LocalTime.MAX))
                    .uniqueResult();
            return total != null ? total : java.math.BigDecimal.ZERO;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao somar vendas do dia", e);
            return java.math.BigDecimal.ZERO;
        }
    }
}
