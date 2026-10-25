package unitrack.service;

import unitrack.model.TodoItem;
import unitrack.repository.TodoRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Step 20: Personal Task Service
 * Manages students' individual study checklists.
 */
public class TodoService {
    private static TodoService instance;
    private final TodoRepository todoRepository = new TodoRepository();

    private TodoService() {}

    public static synchronized TodoService getInstance() {
        if (instance == null) {
            instance = new TodoService();
        }
        return instance;
    }

    public List<TodoItem> getTasksForUser(int userId) {
        return todoRepository.findByOwner(userId);
    }

    public boolean addTodo(int userId, String task, TodoItem.Priority priority, LocalDate dueDate) {
        if (task == null || task.trim().isEmpty()) {
            throw new IllegalArgumentException("Task description cannot be empty.");
        }
        if (dueDate == null) {
            dueDate = LocalDate.now().plusDays(1);
        }
        TodoItem item = new TodoItem(0, userId, task.trim(), priority, dueDate, false);
        return todoRepository.save(item);
    }

    public boolean toggleComplete(int todoId) {
        return todoRepository.findById(todoId).map(item -> {
            item.setCompleted(!item.isCompleted());
            return todoRepository.update(item);
        }).orElse(false);
    }

    public boolean deleteTodo(int todoId) {
        return todoRepository.deleteById(todoId);
    }
}
