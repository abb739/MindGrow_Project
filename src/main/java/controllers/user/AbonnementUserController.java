package controllers.user;

import entities.Abonnement;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.AbonnementService;
import utils.SessionManager;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Controller pour la gestion des abonnements côté UTILISATEUR
 * Note: Le type GRATUIT n'est plus disponible
 */
public class AbonnementUserController {

    // ========== Labels d'information ==========
    @FXML private Label typeActuelLabel;
    @FXML private Label montantActuelLabel;
    @FXML private Label dateExpirationLabel;
    @FXML private Label statutLabel;
    @FXML private Label bienvenuLabel;

    // ========== Cartes de forfaits ==========
    // Note: btnChoisirGratuit retiré car le type GRATUIT n'est plus autorisé
    @FXML private Button btnChoisirMensuel;
    @FXML private Button btnChoisirTrimestriel;
    @FXML private Button btnChoisirAnnuel;
    @FXML private Button btnAnnuler;

    // ========== Service ==========
    private final AbonnementService service = new AbonnementService();
    private Abonnement monAbonnement;

    /**
     * Initialisation du controller
     */
    @FXML
    public void initialize() {
        System.out.println("🎬 Initialisation du controller Abonnement User...");

        // Vérifier qu'un utilisateur est connecté
        if (!SessionManager.isConnecte()) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur",
                    "Non connecté",
                    "Vous devez être connecté pour accéder à cette page.");
            return;
        }

        // Afficher le nom de l'utilisateur
        bienvenuLabel.setText("Bienvenue, " + SessionManager.getNomComplet());

        // Charger l'abonnement actuel
        chargerMonAbonnement();

        System.out.println("✅ Controller Abonnement User initialisé!");
    }

    /**
     * Charger l'abonnement de l'utilisateur connecté
     */
    private void chargerMonAbonnement() {
        int userId = SessionManager.getUserId();
        monAbonnement = service.getAbonnementByUserId(userId);

        if (monAbonnement != null) {
            // Afficher les informations de l'abonnement actuel
            typeActuelLabel.setText(monAbonnement.getType());
            montantActuelLabel.setText(String.format("%.2f €", monAbonnement.getMontant()));
            dateExpirationLabel.setText(monAbonnement.getDateExpirationFormatted());
            statutLabel.setText(monAbonnement.getStatut());

            // Appliquer une couleur selon le statut
            switch (monAbonnement.getStatut()) {
                case "ACTIF":
                    statutLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    break;
                case "EXPIRE":
                    statutLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    break;
                default:
                    statutLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-weight: bold;");
            }

            System.out.println("✅ Abonnement chargé: " + monAbonnement.getType());
        } else {
            // Aucun abonnement trouvé
            typeActuelLabel.setText("AUCUN");
            montantActuelLabel.setText("0.00 €");
            dateExpirationLabel.setText("-");
            statutLabel.setText("INACTIF");
            statutLabel.setStyle("-fx-text-fill: #95a5a6;");

            System.out.println("ℹ️ Aucun abonnement trouvé pour cet utilisateur");
        }
    }

    /**
     * Choisir le forfait MENSUEL
     */
    @FXML
    private void choisirForfaitMensuel() {
        System.out.println("💳 Choix du forfait MENSUEL - 9.99€");

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Souscrire au forfait MENSUEL ?");
        confirmation.setContentText("Montant: 9.99€ / mois\n" +
                "Durée: 1 mois\n" +
                "Accès aux ateliers et séances\n" +
                "Voulez-vous continuer ?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            upgrader("MENSUEL", 9.99);
        }
    }

    /**
     * Choisir le forfait TRIMESTRIEL
     */
    @FXML
    private void choisirForfaitTrimestriel() {
        System.out.println("💳 Choix du forfait TRIMESTRIEL - 24.99€");

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Souscrire au forfait TRIMESTRIEL ?");
        confirmation.setContentText("Montant: 24.99€ pour 3 mois\n" +
                "Économisez 16% par rapport au mensuel!\n" +
                "Accès illimité aux ateliers et séances\n" +
                "Voulez-vous continuer ?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            upgrader("TRIMESTRIEL", 24.99);
        }
    }

    /**
     * Choisir le forfait ANNUEL
     */
    @FXML
    private void choisirForfaitAnnuel() {
        System.out.println("💳 Choix du forfait ANNUEL - 89.99€");

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Souscrire au forfait ANNUEL ?");
        confirmation.setContentText("Montant: 89.99€ / an (économisez 25%!)\n" +
                "Durée: 12 mois\n" +
                "Accès illimité + avantages exclusifs\n" +
                "Voulez-vous continuer ?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            upgrader("ANNUEL", 89.99);
        }
    }

    /**
     * Mettre à jour l'abonnement - Redirige vers la page de paiement
     */
    private void upgrader(String type, double montant) {
        try {
            // Stocker les informations de l'abonnement sélectionné dans SessionManager
            SessionManager.setSelectedSubscriptionType(type);
            SessionManager.setSelectedSubscriptionAmount(montant);

            // Rediriger vers la page de paiement
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/paiement_user_view.fxml"));
            Parent root = loader.load();

            // Obtenir le stage actuel
            Stage stage = (Stage) btnChoisirMensuel.getScene().getWindow();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("MindGrow - Paiement");
            stage.show();

            System.out.println("✅ Redirection vers la page de paiement pour " + type);

        } catch (IOException e) {
            System.err.println("❌ Erreur lors de la redirection vers la page de paiement: " + e.getMessage());
            e.printStackTrace();
            afficherAlerte(Alert.AlertType.ERROR, "Erreur",
                    "Erreur de navigation",
                    "Impossible d'ouvrir la page de paiement.");
        }
    }

    /**
     * Annuler l'abonnement
     */
    @FXML
    private void annulerAbonnement() {
        System.out.println("❌ Demande d'annulation d'abonnement");

        if (monAbonnement == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Attention",
                    "Aucun abonnement",
                    "Vous n'avez pas d'abonnement actif à annuler.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation d'annulation");
        confirmation.setHeaderText("Êtes-vous sûr de vouloir annuler votre abonnement ?");
        confirmation.setContentText("Votre abonnement " + monAbonnement.getType() + " sera annulé.\n" +
                "Vous pourrez toujours utiliser vos droits jusqu'au " +
                monAbonnement.getDateExpirationFormatted());

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            int userId = SessionManager.getUserId();

            if (service.annulerAbonnement(userId)) {
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès",
                        "Abonnement annulé",
                        "Votre abonnement a été annulé avec succès.");
                chargerMonAbonnement();
            } else {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur",
                        "Impossible d'annuler",
                        "Une erreur est survenue.");
            }
        }
    }

    /**
     * Afficher une alerte
     */
    private void afficherAlerte(Alert.AlertType type, String titre, String entete, String contenu) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(entete);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}