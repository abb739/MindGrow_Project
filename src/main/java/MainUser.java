import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import entities.Utilisateur;
import utils.DatabaseConnection;
import utils.SessionManager;

public class MainUser extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("🚀 Démarrage de l'application MindGrow - Interface Utilisateur...");

            // Tester la connexion
            if (!DatabaseConnection.testConnection()) {
                showErrorAlert("Erreur de connexion",
                        "Impossible de se connecter à la base de données.");
                return;
            }

            // SIMULATION : Connecter un utilisateur de test
            // En production, ceci sera fait via un écran de connexion
            Utilisateur userTest = new Utilisateur();
            userTest.setId(1);
            userTest.setNom("Dupont");
            userTest.setPrenom("Jean");
            userTest.setEmail("jean.dupont@mail.com");
            userTest.setRoles("[\"ROLE_MEMBRE\"]");
            SessionManager.login(userTest);

            // Charger la vue des abonnements utilisateur
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/abonnement_user_view.fxml"));
            Parent root = loader.load();

            // Créer la scène
            Scene scene = new Scene(root, 1100, 750);

            // Configurer la fenêtre
            primaryStage.setTitle("MindGrow - Mon Espace Abonnement");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);

            // Afficher
            primaryStage.show();

            System.out.println("✅ Interface utilisateur lancée!");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du démarrage!");
            e.printStackTrace();
            showErrorAlert("Erreur de démarrage", e.getMessage());
        }
    }

    @Override
    public void stop() {
        System.out.println("🛑 Arrêt de l'application...");
        SessionManager.logout();
        DatabaseConnection.closeConnection();
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
        System.out.println("║   MindGrow - Espace Utilisateur       ║");
        System.out.println("║        Gérer Mon Abonnement           ║");
        System.out.println("╚═══════════════════════════════════════╝");
        System.out.println();

        launch(args);
    }
}