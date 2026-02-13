import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import utils.DatabaseConnection;

/**
 * Classe principale pour lancer l'application MindGrow
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("🚀 Démarrage de l'application MindGrow...");

            // Tester la connexion à la base de données
            if (!DatabaseConnection.testConnection()) {
                showErrorAlert("Erreur de connexion",
                        "Impossible de se connecter à la base de données.\n" +
                                "Vérifiez que XAMPP MySQL est démarré et que la base 'mindgrow' existe.");
                return;
            }

            // Charger la vue des abonnements par défaut
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/abonnement_view.fxml"));
            Parent root = loader.load();

            // Créer la scène
            Scene scene = new Scene(root, 1200, 800);

            // Configurer la fenêtre
            primaryStage.setTitle("MindGrow - Gestion des Abonnements");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(700);

            // Icône de l'application (optionnel)
            try {
                primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.png")));
            } catch (Exception e) {
                System.out.println("⚠️ Icône non trouvée (optionnel)");
            }

            // Afficher la fenêtre
            primaryStage.show();

            System.out.println("✅ Application démarrée avec succès!");
            System.out.println("📋 Vue active: Gestion des Abonnements");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du démarrage de l'application!");
            e.printStackTrace();
            showErrorAlert("Erreur de démarrage",
                    "Impossible de démarrer l'application.\n" +
                            "Erreur: " + e.getMessage());
        }
    }

    @Override
    public void stop() {
        System.out.println("🛑 Arrêt de l'application...");
        DatabaseConnection.closeConnection();
        System.out.println("👋 Application arrêtée. Au revoir!");
    }

    /**
     * Afficher une alerte d'erreur
     */
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
        System.out.println("║        Gestion des Abonnements        ║");
        System.out.println("╚═══════════════════════════════════════╝");
        System.out.println();

        launch(args);
    }
}