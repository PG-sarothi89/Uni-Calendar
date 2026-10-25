package unitrack.model;

import java.io.Serializable;

/**
 * Step 9: Syllabus Topic Entity
 * Represents an individual curriculum topic within a Course.
 * Teachers can toggle its completion state to update the course progress bar.
 */
public class SyllabusTopic implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int courseId;
    private int weekNumber;
    private String title;
    private boolean completed;

    public SyllabusTopic(int id, int courseId, int weekNumber, String title, boolean completed) {
        this.id = id;
        this.courseId = courseId;
        this.weekNumber = weekNumber;
        this.title = title;
        this.completed = completed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        return String.format("Week %d: %s [%s]", weekNumber, title, completed ? "✓" : "Pending");
    }
}
