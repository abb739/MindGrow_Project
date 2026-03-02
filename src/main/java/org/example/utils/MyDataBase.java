package org.example.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {
    private static final String URL = "jdbc:mysql://localhost:3306/mindgrow";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static Connection connection;

    private MyDataBase() {
    }

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connexion établie avec succès!");
            } catch (SQLException e) {
                System.err.println("Erreur de connexion: " + e.getMessage());
            }
        }
        return connection;
    }
}