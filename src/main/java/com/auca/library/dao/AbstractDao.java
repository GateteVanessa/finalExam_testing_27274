package com.auca.library.dao;

import com.auca.library.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractDao<T> implements GenericDao<T> {

    private final Class<T> entityClass;

    protected AbstractDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public T save(T entity) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
            return entity;
        } catch (RuntimeException e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }

    @Override
    public Optional<T> findById(UUID id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            T entity = session.get(entityClass, id);
            return Optional.ofNullable(entity);
        }
    }

    @Override
    public List<T> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from " + entityClass.getSimpleName(), entityClass).list();
        }
    }

    @Override
    public T update(T entity) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            T merged = session.merge(entity);
            tx.commit();
            return merged;
        } catch (RuntimeException e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }

    @Override
    public void delete(T entity) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            T attached = (T) session.merge(entity);
            session.remove(attached);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }
}
