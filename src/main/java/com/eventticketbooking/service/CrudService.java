package com.eventticketbooking.service;

import java.util.List;

/**
 * Generic CRUD interface (demonstrates polymorphism via multiple implementations).
 */
public interface CrudService<T> {

    List<T> findAll();

    T findById(String id);

    void save(T entity);

    void update(T entity);

    void delete(String id);
}
