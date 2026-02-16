package controllers;

import models.Therapeute;
import services.TherapeuteService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import java.io.File;
import java.sql.SQLException;

public class CardController {
    @FXML private Label nameLabel;
    @FXML private Label specialtyLabel;
    @FXML private Label statusBadge;
    @FXML private Circle photoCircle;

    private Therapeute therapeute;
    private TherapeuteController parentController;
    private TherapeuteService service = new TherapeuteService();

    // Le même dossier que pour l'ajout
    private final String STORAGE_FOLDER = System.getProperty("user.home") + "/mindgrow_images/";

    public void setData(Therapeute t, TherapeuteController parent) {
        this.therapeute = t;
        this.parentController = parent;

        // --- Textes (Noir) ---
        if (nameLabel != null) {
            nameLabel.setText("Dr. " + t.getPrenom() + " " + t.getNom());
            nameLabel.setStyle("-fx-text-fill: #1f2937; -fx-font-weight: bold; -fx-font-size: 16px;");
        }

        if (specialtyLabel != null) {
            String spec = (t.getSpecialiteNom() != null) ? t.getSpecialiteNom() : "#" + t.getSpecialiteId();
            specialtyLabel.setText(spec);
            specialtyLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12px;");
        }

        if (statusBadge != null) statusBadge.setVisible(t.isEstVerifie());

        // --- Image (Logique Hybride : PC > Ressources) ---
        if (photoCircle != null) {
            photoCircle.setFill(javafx.scene.paint.Color.web("#A2D5AB")); // Couleur de fond par défaut

            try {
                String imageName = (t.getPhotoProfil() != null && !t.getPhotoProfil().isEmpty())
                        ? t.getPhotoProfil() : "default_avatar.png";

                // 1. Chercher dans le dossier mindgrow_images (Images ajoutées par l'app)
                File externalFile = new File(STORAGE_FOLDER + imageName);
                if (externalFile.exists()) {
                    Image img = new Image(externalFile.toURI().toString());
                    photoCircle.setFill(new ImagePattern(img));
                }
                // 2. Sinon, chercher dans les ressources (Images par défaut)
                else {
                    java.net.URL resourceUrl = getClass().getResource("/images/default_avatar.png");
                    if (resourceUrl != null) {
                        Image img = new Image(resourceUrl.toExternalForm());
                        photoCircle.setFill(new ImagePattern(img));
                    }
                }
            } catch (Exception e) {
                System.out.println("Erreur chargement image : " + e.getMessage());
            }
        }
    }

    @FXML private void handleDelete() {
        try { service.supprimer(therapeute.getId()); parentController.refreshGrid(); } catch (SQLException e) {}
    }
    @FXML
    private void handleEdit() {
        try {
            // On récupère le MainLayoutController
            MainLayoutController main = (MainLayoutController) nameLabel.getScene().getUserData();
            if (main != null) {
                // On lance la page d'édition avec les données actuelles de la carte
                main.handleEditTherapeutePage(this.therapeute);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}