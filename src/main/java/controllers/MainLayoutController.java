package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import models.Therapeute;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainLayoutController implements Initializable {

    @FXML
    private StackPane contentArea;

    @FXML
    private VBox sidebar; // Matches fx:id="sidebar" in FXML

    @FXML
    private Label pageTitle; // Matches fx:id="pageTitle"

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadView("/views/HomeView.fxml");
        pageTitle.setText("Tableau de Bord");
    }

    // --- NEW TOGGLE FUNCTION ---
    @FXML
    public void handleToggleSidebar() {
        if (sidebar.isVisible()) {
            // Hide it
            sidebar.setVisible(false);
            sidebar.setManaged(false); // Removes the space it takes up
        } else {
            // Show it
            sidebar.setVisible(true);
            sidebar.setManaged(true);
        }
    }

    // --- NAVIGATION ---

    @FXML
    public void handleShowHome() {
        loadView("/views/HomeView.fxml");
        pageTitle.setText("Tableau de Bord");
    }

    @FXML
    public void handleShowTherapeutes() {
        loadView("/views/GestionTherapeutes.fxml");
        pageTitle.setText("Gestion des Thérapeutes");
    }

    // ... (Keep your other navigation methods here: Patients, Planning, etc.)
    @FXML public void handleShowPatients() { System.out.println("Navigating..."); }
    @FXML public void handleShowPlanning() { System.out.println("Navigating..."); }
    @FXML public void handleShowProgrammes() { System.out.println("Navigating..."); }
    @FXML public void handleShowAbonnements() { System.out.println("Navigating..."); }


    // Inside MainLayoutController.java
    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            // FIX: Ensure the scene is ready before setting UserData
            if (contentArea.getScene() != null) {
                contentArea.getScene().setUserData(this);
            }

            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            System.err.println("ERROR: Could not find FXML file at: " + fxmlPath);
            e.printStackTrace();
        }
    }
    // Dans MainLayoutController.java

    @FXML
    public void handleAddTherapeutePage() {
        try {
            // Make sure the file name matches exactly in your /views folder
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddTherapeuteView.fxml"));
            Parent view = loader.load();

            AddTherapeuteController ctrl = loader.getController();
            ctrl.setMainController(this); // Connect the navigation

            contentArea.getChildren().setAll(view);
            pageTitle.setText("Nouvel Expert");
        } catch (IOException e) {
            System.err.println("Could not load AddTherapeuteView: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // Ajoute cette méthode dans MainLayoutController.java

    public void handleEditTherapeutePage(Therapeute t) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddTherapeuteView.fxml"));
            Parent view = loader.load();

            AddTherapeuteController ctrl = loader.getController();
            ctrl.setMainController(this);
            // C'est ici que la magie opère : on remplit le formulaire avec les données existantes
            ctrl.setUpdateMode(t);

            contentArea.getChildren().setAll(view);
            pageTitle.setText("Modifier l'Expert"); // On change le titre de la page
        } catch (IOException e) {
            System.err.println("Erreur chargement vue édition : " + e.getMessage());
            e.printStackTrace();
        }
    }
}