package com.auca.library.service;

import com.auca.library.dao.UserDao;
import com.auca.library.domain.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

/**
 * Requirement 4: authenticate a user by username/password.
 */
public class AuthService {

    private final UserDao userDao;

    public AuthService() {
        this(new UserDao());
    }

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Returns true only if the username exists and the raw password matches
     * the stored BCrypt hash. Never throws for bad input -- always false.
     */
    public boolean authenticate(String username, String rawPassword) {
        if (username == null || username.isBlank() || rawPassword == null || rawPassword.isBlank()) {
            return false;
        }

        Optional<User> userOpt = userDao.findByUsername(username);
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        try {
            return BCrypt.checkpw(rawPassword, user.getPassword());
        } catch (IllegalArgumentException e) {
            // stored hash is malformed / not a bcrypt hash
            return false;
        }
    }

    /**
     * Helper used when creating accounts: hashes a raw password for storage.
     */
    public static String hashPassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }
}
