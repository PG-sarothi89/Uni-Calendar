package unitrack.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Step 4: Concrete Student User Class
 * Extends User and implements Student-specific capabilities and permissions.
 */
public class Student extends User {
    private static final long serialVersionUID = 1L;

    // List of Course IDs enrolled by this student
    private final List<Integer> enrolledCourseIds = new ArrayList<>();

    public Student(int id, String name, String email, String passwordHash) {
        super(id, name, email, passwordHash, Role.STUDENT);
    }

    public void enrollCourse(int courseId) {
        if (!enrolledCourseIds.contains(courseId)) {
            enrolledCourseIds.add(courseId);
        }
    }

    public List<Integer> getEnrolledCourseIds() {
        return Collections.unmodifiableList(enrolledCourseIds);
    }

    // Role-specific permission enforcement (Polymorphism)
    @Override
    public boolean canCreateEvent() {
        // Students can only create personal tasks, not public course events
        return false;
    }

    @Override
    public boolean canUpdateSyllabus() {
        // Students cannot alter curriculum syllabus status
        return false;
    }

    @Override
    public boolean canBroadcastHolidays() {
        return false;
    }
}
