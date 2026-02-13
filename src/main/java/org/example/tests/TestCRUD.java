package org.example.tests;

import org.example.models.User;
import org.example.services.UserService;

import java.sql.SQLException;
import java.util.List;

public class TestCRUD {
    public static void main(String[] args) {
        UserService userService = new UserService();

        try {
            // 1. Create (Register)
            System.out.println("--- Testing Create (Register) ---");
            User newUser = new User("TestNom", "TestPrenom", "testcrud@example.com", "password123", "[\"ROLE_MEMBRE\"]", "12345678", "1990-01-01");
            userService.register(newUser);
            System.out.println("User registered successfully.");

            // 2. Read (Get All & Authenticate)
            System.out.println("\n--- Testing Read (Authenticate) ---");
            User authenticatedUser = userService.authenticate("testcrud@example.com", "password123");
            if (authenticatedUser != null) {
                System.out.println("Authentication successful: " + authenticatedUser);
            } else {
                System.out.println("Authentication failed.");
                return; // Stop if auth fails
            }

            // 3. Update
            System.out.println("\n--- Testing Update ---");
            authenticatedUser.setNom("UpdatedNom");
            authenticatedUser.setTelephone("87654321");
            userService.update(authenticatedUser);
            System.out.println("User updated.");
            
            // Verify Update
            User updatedUser = userService.authenticate("testcrud@example.com", "password123");
            System.out.println("Fetched updated user: " + updatedUser);

            // 4. Delete
            System.out.println("\n--- Testing Delete ---");
            userService.delete(authenticatedUser.getId());
            System.out.println("User deleted.");

            // Verify Delete
            User deletedUser = userService.authenticate("testcrud@example.com", "password123");
            if (deletedUser == null) {
                System.out.println("Verification successful: User no longer exists.");
            } else {
                System.out.println("Verification failed: User still exists.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
