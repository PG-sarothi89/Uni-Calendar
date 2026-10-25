package unitrack.repository;

import unitrack.model.TodoItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Step 16: Personal To-Do Repository Implementation
 * Stores and queries personal study tasks filtered by user ID.
 */
public class TodoRepository implements Repository<TodoItem, Integer> {
    private final DataStore dataStore = DataStore.getInstance();

    @Override
    public Optional<TodoItem> findById(Integer id) {
        return Optional.ofNullable(dataStore.getTodoItems().get(id));
    }

    @Override
    public List<TodoItem> findAll() {
        return new ArrayList<>(dataStore.getTodoItems().values());
    }

    /**
     * Filters tasks owned by a specific student user
     */
    public List<TodoItem> findByOwner(int ownerId) {
        return dataStore.getTodoItems().values().stream()
                .filter(t -> t.getOwnerId() == ownerId)
                .sorted((a, b) -> a.getDueDate().compareTo(b.getDueDate()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean save(TodoItem entity) {
        if (entity.getId() <= 0) {
            entity.setId(dataStore.getNextTodoId());
        }
        dataStore.getTodoItems().put(entity.getId(), entity);
        dataStore.saveToFile();
        return true;
    }

    @Override
    public boolean update(TodoItem entity) {
        if (dataStore.getTodoItems().containsKey(entity.getId())) {
            dataStore.getTodoItems().put(entity.getId(), entity);
            dataStore.saveToFile();
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteById(Integer id) {
        boolean removed = dataStore.getTodoItems().remove(id) != null;
        if (removed) dataStore.saveToFile();
        return removed;
    }
}
