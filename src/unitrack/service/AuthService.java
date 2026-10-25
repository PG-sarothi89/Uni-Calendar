package unitrack.service;

import unitrack.model.User;
import unitrack.repository.UserRepository;

import java.util.Optional;

/**
 * Step 17: Authentication & Session Service
 * Manages user logins, credential verification, and currently authenticated user session.
 * Implements Singleton Pattern for global session access.
 */
public class AuthService {
    private static AuthService instance;
    private final UserRepository userRepository = new UserRepository();
    private User currentUser;

    private AuthService() {}

    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    /**
     * Authenticates a user with email and password
     *
     * @param email    User email
     * @param password Raw password
     * @return Optional containing the authenticated User, or empty if invalid
     */
    public Optional<User> login(String email, String password) {
        if (email == null || password == null) return Optional.empty();

        Optional<User> userOpt = userRepository.findByEmail(email.trim());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Verify password (in academic demo, match plaintext or hash)
            if (password.trim().equals(user.getPasswordHash())) {
                this.currentUser = user;
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    /**
     * Logs out the current user session
     */
    public void logout() {
        this.currentUser = null;
    }

    /**
     * Returns the currently logged in user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Helper to check if a user is logged in
     */
    public boolean isAuthenticated() {
        return currentUser != null;
    }
}
