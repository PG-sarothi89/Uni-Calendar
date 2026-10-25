package unitrack.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Step 8: Academic Course Model
 * Represents an enrolled subject (e.g. CSE 2102: Object-Oriented Design & Programming).
 * Holds curriculum syllabus topics for progress tracking.
 */
public class Course implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String code;         // e.g. "CSE 2102"
    private String title;        // e.g. "Object-Oriented Design and Programming"
    private int instructorId;    // Teacher User ID
    private String instructorName;
    private String semester;     // e.g. "Fall 2026"
    private final List<SyllabusTopic> syllabus = new ArrayList<>();

    public Course(int id, String code, String title, int instructorId, String instructorName, String semester) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.instructorId = instructorId;
        this.instructorName = instructorName;
        this.semester = semester;
    }

    public void addSyllabusTopic(SyllabusTopic topic) {
        syllabus.add(topic);
    }

    public List<SyllabusTopic> getSyllabus() {
        return Collections.unmodifiableList(syllabus);
    }

    /**
     * Calculates the percentage of syllabus topics completed.
     * Demonstrates Java Stream API & aggregation logic.
     */
    public int getSyllabusCompletionPercentage() {
        if (syllabus.isEmpty()) return 0;
        long completedCount = syllabus.stream().filter(SyllabusTopic::isCompleted).count();
        return (int) Math.round((double) completedCount / syllabus.size() * 100.0);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(int instructorId) {
        this.instructorId = instructorId;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    @Override
    public String toString() {
        return code + " - " + title;
    }
}
