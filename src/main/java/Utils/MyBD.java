package Utils;

import java.sql.*;

public class MyBD {
    private Connection conn;
    // Mise à jour avec le nom de ta base : ayy
    final private String URL = "jdbc:mysql://localhost:3306/ayy";
    final private String USER = "root";
    final private String PASS = "";

    private static MyBD instance;

    private MyBD() {
        try {
            conn = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Connexion à 'ayy' établie !");
        } catch (SQLException s) {
            System.out.println("Erreur : " + s.getMessage());
        }
    }

    public static MyBD getInstance() {
        if (instance == null) {
            instance = new MyBD();
        }
        return instance;
    }

    public Connection getConn() {
        return conn;
    }
}