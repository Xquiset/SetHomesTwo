package com.samleighton.sethomestwo.dao;

import java.util.List;

public interface Dao<T> {

    /**
     * Retrieve all models
     * @param keys Keys used to retrieve the model
     */
    List<T> getAll(Object... keys);

    /**
     * Retrieve a single model
     * @param keys Keys used to retrieve the model
     */
    T get(Object... keys);

    /**
     * Save a single model
     * @param object The model to save
     */
    boolean save(Object object);

    /**
     * Delete a single model
     * @param object The model to delete
     */
    boolean delete(Object object);
}
