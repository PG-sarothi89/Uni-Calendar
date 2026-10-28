package unitrack.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Step 5: Concrete Teacher User Class
 * Extends User and implements Teacher-specific capabilities:
 * Managing assigned courses, posting lecture events, approving/declining student consultancies,
 * and updating syllabus progress.
 */
public class Teacher extends User {
    private static final long serialVersionUID = 1L;

    private String department = "Department of Computer Science & Engineering";
    // List of Course IDs assigned to this instructor
    private final List<Integer> assignedCourseIds = new ArrayList<>();

    public Teacher(int id, String name, String email, String passwordHash) {
        super(id, name, email, passwordHash, Role.TEACHER);
    }

    public Teacher(int id, String name, String email, String passwordHash, String department) {
        super(id, name, email, passwordHash, Role.TEACHER);
        if (department != null && !department.trim().isEmpty()) {
            this.department = department.trim();
        }
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void assignCourse(int courseId) {
        if (!assignedCourseIds.contains(courseId)) {
            assignedCourseIds.add(courseId);
        }
    }

    public List<Integer> getAssignedCourseIds() {
        return Collections.unmodifiableList(assignedCourseIds);
    }

    // Role-specific permission enforcement (Polymorphism)
    @Override
    public boolean canCreateEvent() {
        return true;
    }

    @Override
    public boolean canUpdateSyllabus() {
        return true;
    }

    @Override
    public boolean canBroadcastHolidays() {
        return false;
    }
}
