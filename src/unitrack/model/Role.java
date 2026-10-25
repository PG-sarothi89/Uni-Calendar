package unitrack.model;

/**
 * Step 1: User Roles Definition
 * Enumeration representing the user roles within the UniTrack academic system.
 * Demonstrates Role-Based Access Control (RBAC) in Object-Oriented Design.
 */
public enum Role {
    STUDENT,   // Can view personal schedule, enroll in courses, check to-do list, download notes
    TEACHER,   // Can create/edit academic events, upload lecture notes, update syllabus progress
    ADMIN      // Can manage semester bounds, broadcast holidays, and manage all records
}
