import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.DatabaseConnection;

/**
 * Classe principale pour lancer la vue des Paiements
 */
public class MainPaiement extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("🚀 Démarrage de l'application MindGrow - Paiements...");

            // Tester la connexion
            if (!DatabaseConnection.testConnection()) {
                showErrorAlert("Erreur de connexion",
                        "Impossible de se connecter à la base de données.");
                return;
            }

            // Charger la vue des paiements
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/paiement_view.fxml"));
            Parent root = loader.load();

            // Créer la scène
            Scene scene = new Scene(root, 1200, 800);

            // Configurer la fenêtre
            primaryStage.setTitle("MindGrow - Gestion des Paiements");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(700);

            // Afficher
            primaryStage.show();

            System.out.println("✅ Application démarrée avec succès!");
            System.out.println("💳 Vue active: Gestion des Paiements");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du démarrage!");
            e.printStackTrace();
            showErrorAlert("Erreur de démarrage",
                    "Impossible de démarrer l'application.\n" + e.getMessage());
        }
    }

    @Override
    public void stop() {
        System.out.println("🛑 Arrêt de l'application...");
        DatabaseConnection.closeConnection();
        System.out.println("👋 Au revoir!");
    }

    private void showErrorAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR
        );
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║   MindGrow - Module Paiements & Abo   ║");
        System.out.println("║         Gestion des Paiements         ║");
        System.out.println("╚═══════════════════════════════════════╝");
        System.out.println();

        launch(args);
    }
}