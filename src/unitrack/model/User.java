package unitrack.model;

import java.io.Serializable;

/**
 * Step 3: Abstract Base User Model
 * Demonstrates Core OOP Principle: Abstraction & Inheritance.
 * Defines shared user attributes and declares polymorphic permission verification.
 */
public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;

    // Encapsulated fields
    protected int id;
    protected String name;
    protected String email;
    protected String passwordHash;
    protected Role role;

    /**
     * Base constructor initializing fundamental user details
     */
    public User(int id, String name, String email, String passwordHash, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // Abstract method: Every subclass must implement role-specific permissions
    public abstract boolean canCreateEvent();
    public abstract boolean canUpdateSyllabus();
    public abstract boolean canBroadcastHolidays();

    // Role verification helpers
    public boolean isBatchAdvisor() {
        return this.role == Role.ADMIN;
    }

    public boolean isTeacher() {
        return this.role == Role.TEACHER;
    }

    public boolean isStudent() {
        return this.role == Role.STUDENT;
    }

    // Standard Getters and Setters (Encapsulation)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s", name, email, role.getDisplayName());
    }
}
