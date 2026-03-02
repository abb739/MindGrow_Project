package org.example.controller.Frontoffice;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import org.example.entities.Utilisateur;
import org.example.services.UtilisateurService;

import java.io.IOException;
import java.util.Optional;

public class ClientDashboardController {

    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label messageLabel;
    @FXML
    private VBox profilView;
    @FXML
    private VBox homeView;
    @FXML
    private VBox programmesView;
    @FXML
    private VBox seancesView;
    @FXML
    private VBox abonnementsView;
    @FXML
    private VBox therapeutesView;

    private Utilisateur utilisateurConnecte;
    private SeanceFrontController seanceFrontController;
    private final UtilisateurService utilisateurService = new UtilisateurService();

    /**
     * Cette méthode sert à initialiser le dashboard avec l'utilisateur qui vient de
     * se connecter
     */
    public void initData(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;

        // Remplir les champs avec les données actuelles
        nomField.setText(utilisateur.getNom());
        prenomField.setText(utilisateur.getPrenom());
        emailField.setText(utilisateur.getEmail());
        // On ne pré-remplit pas le mot de passe pour des raisons de sécurité

        System.out.println("Espace Client chargé pour : " + utilisateur.getEmail());

        // Show home view by default
        showHomeView(null);
    }

    @FXML
    void handleUpdate(ActionEvent event) {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Nom, Prénom et Email sont obligatoires.");
            return;
        }

        // Mettre à jour l'objet utilisateur en mémoire
        utilisateurConnecte.setNom(nom);
        utilisateurConnecte.setPrenom(prenom);
        utilisateurConnecte.setEmail(email);

        // Si le mot de passe est renseigné, on le met à jour
        if (!password.isEmpty()) {
            if (password.length() < 8) {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("Mot de passe trop court.");
                return;
            }
            utilisateurConnecte.setMotDePasse(password);
        }

        // Appel au service pour mettre à jour la BDD
        utilisateurService.modifierUtilisateur(utilisateurConnecte);

        messageLabel.setStyle("-fx-text-fill: green;");
        messageLabel.setText("Profil mis à jour avec succès !");
        passwordField.clear(); // Effacer le champ après MAJ
    }

    @FXML
    void handleDelete(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Suppression définitive du compte");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer votre profil ? Cette action est irréversible.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Suppression en BDD
            utilisateurService.supprimerUtilisateur(utilisateurConnecte.getIdUtilisateur());

            System.out.println("Utilisateur supprimé, redirection vers la connexion.");
            handleLogout(event);
        }
    }

    @FXML
    void showHomeView(ActionEvent event) {
        profilView.setVisible(false);
        programmesView.setVisible(false);
        seancesView.setVisible(false);
        abonnementsView.setVisible(false);
        therapeutesView.setVisible(false);
        homeView.setVisible(true);

        if (homeView.getChildren().isEmpty()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Frontoffice/HomeFront.fxml"));
                Node node = loader.load();

                HomeFrontController controller = loader.getController();
                controller.setParentController(this);

                homeView.getChildren().add(node);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void showProfilView(ActionEvent event) {
        homeView.setVisible(false);
        programmesView.setVisible(false);
        abonnementsView.setVisible(false);
        therapeutesView.setVisible(false);
        profilView.setVisible(true);
    }

    @FXML
    void showProgrammesView(ActionEvent event) {
        homeView.setVisible(false);
        profilView.setVisible(false);
        seancesView.setVisible(false);
        abonnementsView.setVisible(false);
        therapeutesView.setVisible(false);
        programmesView.setVisible(true);

        if (programmesView.getChildren().isEmpty()) {
            try {
                Node node = FXMLLoader.load(getClass().getResource("/Frontoffice/ProgrammeFront.fxml"));
                programmesView.getChildren().add(node);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void showSeancesView(ActionEvent event) {
        homeView.setVisible(false);
        profilView.setVisible(false);
        programmesView.setVisible(false);
        abonnementsView.setVisible(false);
        therapeutesView.setVisible(false);
        seancesView.setVisible(true);

        if (seancesView.getChildren().isEmpty()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Frontoffice/SeanceFront.fxml"));
                Node node = loader.load();

                seanceFrontController = loader.getController();
                seanceFrontController.setUtilisateur(utilisateurConnecte);

                seancesView.getChildren().add(node);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (seanceFrontController != null) {
            // Déclencher la notification à chaque affichage
            seanceFrontController.setUtilisateur(utilisateurConnecte);
        }
    }

    @FXML
    void showAbonnementsView(ActionEvent event) {
        homeView.setVisible(false);
        profilView.setVisible(false);
        programmesView.setVisible(false);
        seancesView.setVisible(false);
        therapeutesView.setVisible(false);
        abonnementsView.setVisible(true);

        if (abonnementsView.getChildren().isEmpty()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Frontoffice/AbonnementFront.fxml"));
                Node node = loader.load();
                AbonnementFrontController controller = loader.getController();
                controller.setUtilisateur(utilisateurConnecte);
                abonnementsView.getChildren().add(node);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void showTherapeutesView(ActionEvent event) {
        homeView.setVisible(false);
        profilView.setVisible(false);
        programmesView.setVisible(false);
        seancesView.setVisible(false);
        abonnementsView.setVisible(false);
        therapeutesView.setVisible(true);

        if (therapeutesView.getChildren().isEmpty()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Frontoffice/TherapeuteFront.fxml"));
                Node node = loader.load();

                TherapeuteFrontController controller = loader.getController();
                controller.setUtilisateur(utilisateurConnecte);

                therapeutesView.getChildren().add(node);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void handleLogout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Frontoffice/SignIn.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
