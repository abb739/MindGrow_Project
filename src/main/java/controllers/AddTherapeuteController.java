package controllers;

import models.Specialite;
import models.Therapeute;
import services.SpecialiteService;
import services.TherapeuteService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.UUID;
import java.util.regex.Pattern;

public class AddTherapeuteController {

    @FXML private TextField nomField, prenomField, emailField;
    @FXML private ComboBox<Specialite> specialiteCombo;
    @FXML private Label errorLabel, imagePathLabel;
    @FXML private Label titleLabel; // Ajoute fx:id="titleLabel" dans ton FXML sur le titre
    @FXML private Button submitButton; // Ajoute fx:id="submitButton" dans ton FXML sur le bouton

    private final String STORAGE_FOLDER = System.getProperty("user.home") + "/mindgrow_images/";

    private File selectedFileSource = null;
    private String selectedImageName = "default_avatar.png";

    // Variables pour le mode Édition
    private boolean isUpdateMode = false;
    private int currentTherapeuteId = 0;

    private TherapeuteService tService = new TherapeuteService();
    private SpecialiteService sService = new SpecialiteService();
    private MainLayoutController mainController;

    @FXML
    public void initialize() {
        try {
            specialiteCombo.getItems().addAll(sService.afficher());
            File folder = new File(STORAGE_FOLDER);
            if (!folder.exists()) folder.mkdirs();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void setMainController(MainLayoutController main) {
        this.mainController = main;
    }

    // --- NOUVELLE MÉTHODE : Active le mode Modification ---
    public void setUpdateMode(Therapeute t) {
        this.isUpdateMode = true;
        this.currentTherapeuteId = t.getId();

        // 1. Remplir les champs
        nomField.setText(t.getNom());
        prenomField.setText(t.getPrenom());
        emailField.setText(t.getEmail());

        // 2. Sélectionner la bonne spécialité
        for (Specialite s : specialiteCombo.getItems()) {
            if (s.getId() == t.getSpecialiteId()) {
                specialiteCombo.setValue(s);
                break;
            }
        }

        // 3. Gérer l'image existante
        this.selectedImageName = t.getPhotoProfil();
        imagePathLabel.setText("Actuelle : " + selectedImageName);

        // 4. Changer visuellement l'interface (Optionnel si tu as mis les fx:id)
        if (titleLabel != null) titleLabel.setText("Modifier l'Expert");
        if (submitButton != null) submitButton.setText("Modifier");
    }

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(nomField.getScene().getWindow());

        if (file != null) {
            this.selectedFileSource = file;
            this.selectedImageName = file.getName();
            imagePathLabel.setText("Nouvelle image : " + selectedImageName);
            imagePathLabel.setStyle("-fx-text-fill: #4F46E5;");
        }
    }

    @FXML
    private void handleSave() {
        if (!validateInputs()) return;

        String finalFileName = selectedImageName; // Par défaut, on garde l'ancienne ou default

        // Si une NOUVELLE image a été choisie, on la copie
        if (selectedFileSource != null) {
            try {
                String uniqueName = "expert_" + UUID.randomUUID().toString() + "_" + selectedFileSource.getName();
                File destFile = new File(STORAGE_FOLDER + uniqueName);
                Files.copy(selectedFileSource.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                finalFileName = uniqueName;
            } catch (Exception e) {
                errorLabel.setText("Erreur copie image : " + e.getMessage());
                return;
            }
        }

        // On crée l'objet (ID est 0 pour ajout, mais sera ignoré par le service modifier si on passe les params séparément,
        // ou alors on utilise l'ID stocké pour la modification)
        Therapeute t = new Therapeute(
                currentTherapeuteId, // Important pour le UPDATE WHERE id = ...
                nomField.getText().trim(),
                prenomField.getText().trim(),
                emailField.getText().trim(),
                specialiteCombo.getValue().getId(),
                null,
                finalFileName,
                false
        );

        try {
            if (isUpdateMode) {
                tService.modifier(t); // Appel SQL UPDATE
            } else {
                tService.ajouter(t);  // Appel SQL INSERT
            }

            if (mainController != null) mainController.handleShowTherapeutes();
        } catch (SQLException e) {
            errorLabel.setText("Erreur SQL : " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        if (nomField.getText().isBlank() || prenomField.getText().isBlank()) {
            errorLabel.setText("Nom et Prénom obligatoires !");
            return false;
        }
        if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@gmail\\.com$")) {
            errorLabel.setText("Email @gmail.com requis !");
            return false;
        }
        if (specialiteCombo.getValue() == null) {
            errorLabel.setText("Sélectionnez une spécialité !");
            return false;
        }
        return true;
    }

    @FXML private void handleCancel() {
        if(mainController != null) mainController.handleShowTherapeutes();
    }
}