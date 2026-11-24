package com.erp.repository;

import com.erp.model.Produto;
import com.erp.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProdutoRepository {
    private static final Logger LOGGER = Logger.getLogger(ProdutoRepository.class.getName());

    public Produto salvar(Produto produto) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(produto);
            transaction.commit();
            return produto;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            LOGGER.log(Level.SEVERE, "Erro ao salvar produto", e);
            throw new RuntimeException("Erro ao salvar produto", e);
        }
    }

    public Optional<Produto> buscarPorId(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Produto.class, id));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar produto por ID", e);
            return Optional.empty();
        }
    }

    public List<Produto> buscarTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Produto", Produto.class).list();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar todos os produtos", e);
            return List.of();
        }
    }

    public void deletar(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Produto produto = session.get(Produto.class, id);
            if (produto != null) {
                session.delete(produto);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            LOGGER.log(Level.SEVERE, "Erro ao deletar produto", e);
            throw new RuntimeException("Erro ao deletar produto", e);
        }
    }

    public Produto atualizar(Produto produto) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(produto);
            transaction.commit();
            return produto;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            LOGGER.log(Level.SEVERE, "Erro ao atualizar produto", e);
            throw new RuntimeException("Erro ao atualizar produto", e);
        }
    }

    public boolean existeVendaParaProduto(Long produtoId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery("select count(pi.id) from PedidoItem pi where pi.produto.id = :id", Long.class)
                    .setParameter("id", produtoId)
                    .uniqueResult();
            return count != null && count > 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao verificar venda para produto", e);
            return false;
        }
    }

    public Optional<Produto> buscarPorCodigoBarras(String codigo) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Produto where codigoBarras = :codigo", Produto.class)
                    .setParameter("codigo", codigo)
                    .uniqueResultOptional();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar produto por codigo de barras", e);
            return Optional.empty();
        }
    }

    public Optional<Produto> buscarPorCodigoInterno(String codigo) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Produto where codigoInterno = :codigo", Produto.class)
                    .setParameter("codigo", codigo)
                    .uniqueResultOptional();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar produto por codigo interno", e);
            return Optional.empty();
        }
    }

    public String buscarUltimoCodigoSequencial() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("select max(id) from Produto", Long.class)
                    .uniqueResultOptional()
                    .map(id -> String.format("%010d", id + 1))
                    .orElse("0000000001");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar ultimo codigo sequencial", e);
            return "0000000001";
        }
    }
}
