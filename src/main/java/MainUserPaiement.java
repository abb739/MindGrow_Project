import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import entities.Utilisateur;
import utils.DatabaseConnection;
import utils.SessionManager;

public class MainUserPaiement extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("🚀 Démarrage - Mes Paiements...");

            if (!DatabaseConnection.testConnection()) {
                showErrorAlert("Erreur", "Connexion base de données impossible.");
                return;
            }

            // Simulation connexion utilisateur
            Utilisateur userTest = new Utilisateur();
            userTest.setId(1);
            userTest.setNom("Dupont");
            userTest.setPrenom("Jean");
            userTest.setRoles("[\"ROLE_MEMBRE\"]");
            SessionManager.login(userTest);

            // Charger la vue paiements user
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/paiement_user_view.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1100, 750);

            primaryStage.setTitle("MindGrow - Mes Paiements");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);

            primaryStage.show();

            System.out.println("✅ Interface paiements user lancée!");

        } catch (Exception e) {
            System.err.println("❌ Erreur!");
            e.printStackTrace();
            showErrorAlert("Erreur", e.getMessage());
        }
    }

    @Override
    public void stop() {
        SessionManager.logout();
        DatabaseConnection.closeConnection();
    }

    private void showErrorAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR
        );
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║   MindGrow - Mes Paiements            ║");
        System.out.println("╚═══════════════════════════════════════╝");
        launch(args);
    }
}