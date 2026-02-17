package org.example.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/mindgrow";
    private static final String USER = "root"; // Default XAMPP/WAMP user
    private static final String PASSWORD = ""; // Default XAMPP/WAMP password

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Database Connection Failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
