package unitrack.model;

/**
 * Step 6: Concrete Administrator User Class
 * Extends User with complete administrative permissions across the system.
 */
public class Admin extends User {
    private static final long serialVersionUID = 1L;

    public Admin(int id, String name, String email, String passwordHash) {
        super(id, name, email, passwordHash, Role.ADMIN);
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
        return true;
    }
}
