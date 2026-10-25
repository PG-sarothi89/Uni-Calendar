package unitrack.repository;

import unitrack.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Step 13: User Repository Implementation
 * Provides CRUD operations for User entities using the Generic Repository interface.
 */
public class UserRepository implements Repository<User, Integer> {
    private final DataStore dataStore = DataStore.getInstance();

    @Override
    public Optional<User> findById(Integer id) {
        return Optional.ofNullable(dataStore.getUsers().get(id));
    }

    /**
     * Looks up user by unique email address (used during authentication)
     */
    public Optional<User> findByEmail(String email) {
        return dataStore.getUsers().values().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email.trim()))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(dataStore.getUsers().values());
    }

    @Override
    public boolean save(User entity) {
        if (entity.getId() <= 0) {
            entity.setId(dataStore.getNextUserId());
        }
        dataStore.getUsers().put(entity.getId(), entity);
        dataStore.saveToFile();
        return true;
    }

    @Override
    public boolean update(User entity) {
        if (dataStore.getUsers().containsKey(entity.getId())) {
            dataStore.getUsers().put(entity.getId(), entity);
            dataStore.saveToFile();
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteById(Integer id) {
        boolean removed = dataStore.getUsers().remove(id) != null;
        if (removed) dataStore.saveToFile();
        return removed;
    }
}
