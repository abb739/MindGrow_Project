package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe utilitaire pour gérer la connexion à la base de données MySQL
 */
public class DatabaseConnection {

    // Configuration de la base de données
    private static final String URL = "jdbc:mysql://localhost:3306/mindgrow?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Vide pour XAMPP par défaut

    private static Connection connection;

    /**
     * Obtenir une connexion à la base de données
     * @return Connection ou null en cas d'erreur
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Charger le driver MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Créer la connexion
                connection = DriverManager.getConnection(URL, USER, PASSWORD);

                System.out.println("✅ Connexion réussie à la base de données mindgrow!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver MySQL introuvable!");
            System.err.println("💡 Assurez-vous que mysql-connector-java est ajouté au projet.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion à la base de données!");
            System.err.println("💡 Vérifiez que XAMPP MySQL est démarré et que la base 'mindgrow' existe.");
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Fermer la connexion à la base de données
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔒 Connexion fermée.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la fermeture de la connexion.");
            e.printStackTrace();
        }
    }

    /**
     * Tester la connexion
     */
    public static boolean testConnection() {
        Connection conn = getConnection();
        return conn != null;
    }
}