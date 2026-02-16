package org.example.utils;
import java.sql.*;

public class DataSource {
    private String url = "jdbc:mysql://localhost:3306/mindgrow"; // Nom de ta base
    private String user = "root";
    private String pwd = "";
    private Connection conn;
    private static DataSource instance;

    private DataSource() {
        try {
            conn = DriverManager.getConnection(url, user, pwd);
            System.out.println("Connexion établie !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static DataSource getInstance() {
        if (instance == null) instance = new DataSource();
        return instance;
    }

    public Connection getConn() {
        return conn;
    }
}