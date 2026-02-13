import utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║  TEST DE CONNEXION À LA BASE DE DONNÉES  ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println();

        // Test 1 : Connexion basique
        System.out.println("📌 Test 1 : Connexion basique");
        Connection conn = DatabaseConnection.getConnection();

        if (conn != null) {
            System.out.println("✅ Connexion réussie!");

            // Test 2 : Vérifier les tables
            System.out.println("\n📌 Test 2 : Vérification des tables");
            try {
                Statement stmt = conn.createStatement();

                // Compter les abonnements
                ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) as total FROM abonnement");
                if (rs1.next()) {
                    System.out.println("   📋 Abonnements: " + rs1.getInt("total"));
                }

                // Compter les paiements
                ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) as total FROM paiement");
                if (rs2.next()) {
                    System.out.println("   💳 Paiements: " + rs2.getInt("total"));
                }

                // Compter les utilisateurs
                ResultSet rs3 = stmt.executeQuery("SELECT COUNT(*) as total FROM utilisateur");
                if (rs3.next()) {
                    System.out.println("   👤 Utilisateurs: " + rs3.getInt("total"));
                }

                System.out.println("\n✅ Toutes les tables sont accessibles!");

            } catch (Exception e) {
                System.err.println("❌ Erreur lors de la vérification des tables:");
                e.printStackTrace();
            }

        } else {
            System.err.println("❌ Échec de la connexion!");
            System.err.println("\n💡 Solutions possibles:");
            System.err.println("   1. Vérifiez que XAMPP MySQL est démarré");
            System.err.println("   2. Vérifiez que la base 'mindgrow' existe dans phpMyAdmin");
            System.err.println("   3. Vérifiez les identifiants dans DatabaseConnection.java");
        }

        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║            FIN DES TESTS               ║");
        System.out.println("╚════════════════════════════════════════╝");
    }
}