package unitrack.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Step 10: Student Personal To-Do Entity
 * Encapsulates a student's personal study action item, independent of public course events.
 * Has priority, completion flag, and deadline.
 */
public class TodoItem implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    private int id;
    private int ownerId;        // Student User ID
    private String task;
    private Priority priority;
    private LocalDate dueDate;
    private boolean completed;

    public TodoItem(int id, int ownerId, String task, Priority priority, LocalDate dueDate, boolean completed) {
        this.id = id;
        this.ownerId = ownerId;
        this.task = task;
        this.priority = priority;
        this.dueDate = dueDate;
        this.completed = completed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (Due: %s)", priority, task, dueDate);
    }
}
