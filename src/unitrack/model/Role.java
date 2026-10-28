package unitrack.model;

/**
 * Step 1: User Roles Definition
 * Enumeration representing the user roles within the UniTrack academic system.
 * The Administrator serves as the Batch Advisor with faculty and class assignment privileges.
 * Demonstrates Role-Based Access Control (RBAC) in Object-Oriented Design.
 */
public enum Role {
    STUDENT("Student"),         // Can view personal schedule, enroll in courses, check to-do list, book consultancies
    TEACHER("Teacher"),         // Can create/edit academic events, approve/decline student consultancies, update syllabus
    ADMIN("Batch Advisor");     // Batch Advisor: Can add new teachers, assign dedicated classes, broadcast holidays

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
