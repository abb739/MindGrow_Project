package org.example.utils;

import org.example.models.User;

/**
 * Manages the current user session (singleton pattern).
 * Stores the logged-in admin user after authentication.
 */
public class SessionManager {

    private static User currentUser;

    private SessionManager() {
        // Prevent instantiation
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void clear() {
        currentUser = null;
    }
}
