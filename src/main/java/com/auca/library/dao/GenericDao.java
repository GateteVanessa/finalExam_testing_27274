package com.auca.library.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Minimal CRUD contract shared by every entity-specific DAO.
 */
public interface GenericDao<T> {

    T save(T entity);

    Optional<T> findById(UUID id);

    List<T> findAll();

    T update(T entity);

    void delete(T entity);
}
