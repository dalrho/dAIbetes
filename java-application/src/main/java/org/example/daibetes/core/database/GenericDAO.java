package org.example.daibetes.core.database;

import java.util.List;
import java.util.Optional;

/**
 * A generic template for Data Access Objects.
 * @param <T> The entity model type (e.g., Appointment, Doctor, Patient, User)
 * @param <ID> The primary key identifier type (usually Integer or String)
 */
public interface GenericDAO<T, ID> {

    /**
     * Inserts an entity into the database and returns the generated primary key ID.
     */
    ID save(T entity);

    /**
     * Retrieves an entity from the database by its primary key ID.
     * Wrapped in an Optional to safely handle cases where the ID does not exist.
     */
    Optional<T> findById(ID id);

    /**
     * Retrieves all rows of this entity type from the database.
     */
    List<T> findAll();

    /**
     * Updates an existing database row with matching entity primary key data.
     * @return true if the update was successful, false otherwise.
     */
    boolean update(T entity);

    /**
     * Deletes a database row by its primary key ID.
     * @return true if the deletion was successful, false otherwise.
     */
    boolean deleteById(ID id);
}
