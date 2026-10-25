package unitrack.repository;

import java.util.List;
import java.util.Optional;

/**
 * Step 11: Generic Repository Pattern Interface
 * Demonstrates Java Generics <T, ID> and Data Access Object (DAO) abstraction.
 * Decouples the business logic from persistence mechanisms.
 *
 * @param <T>  The domain entity type
 * @param <ID> The entity's unique identifier type (e.g. Integer)
 */
public interface Repository<T, ID> {
    /**
     * Finds an entity by its unique ID
     */
    Optional<T> findById(ID id);

    /**
     * Retrieves all entities
     */
    List<T> findAll();

    /**
     * Persists or inserts a new entity
     */
    boolean save(T entity);

    /**
     * Updates an existing entity
     */
    boolean update(T entity);

    /**
     * Deletes an entity by its unique ID
     */
    boolean deleteById(ID id);
}
