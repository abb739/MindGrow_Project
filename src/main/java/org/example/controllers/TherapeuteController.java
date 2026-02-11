package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.entities.Therapeute;
import org.example.services.ServiceTherapeute;
import java.sql.SQLException;
import java.util.List;

public class TherapeuteController {
    @FXML private TableView<Therapeute> therapeuteTable;
    @FXML private TableColumn<Therapeute, String> colNom, colPrenom, colSpecialite, colDiplome;
    @FXML private TextField txtNom, txtPrenom, txtDiplome;
    @FXML private ComboBox<String> comboSpecialite;

    private ServiceTherapeute st = new ServiceTherapeute();

    @FXML
    public void initialize() {
        // Liaison des colonnes avec le modèle
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colSpecialite.setCellValueFactory(new PropertyValueFactory<>("specialite"));
        colDiplome.setCellValueFactory(new PropertyValueFactory<>("diplome"));

        // Remplissage de la ComboBox (Simulant la deuxième table)
        comboSpecialite.setItems(FXCollections.observableArrayList("TCC", "Méditation", "Yoga", "Gestion du stress"));

        chargerDonnees();
    }

    private void chargerDonnees() {
        try {
            // Utilisation d'ObservableList pour la mise à jour en temps réel
            List<Therapeute> liste = st.afficherAll();
            therapeuteTable.setItems(FXCollections.observableArrayList(liste));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- LOGIQUE DE VALIDATION SÉPARÉE ---
    private boolean estValide() {
        String messageErreur = "";

        if (txtNom.getText().trim().isEmpty()) {
            messageErreur += "Le champ Nom est vide.\n";
        } else if (!txtNom.getText().matches("[a-zA-Z\\s]+")) {
            // Validation spécifique : pas de chiffres dans le nom
            messageErreur += "Le nom ne doit contenir que des lettres.\n";
        }

        if (txtPrenom.getText().trim().isEmpty()) {
            messageErreur += "Le champ Prénom est vide.\n";
        }

        if (comboSpecialite.getValue() == null) {
            messageErreur += "Veuillez sélectionner une spécialité.\n";
        }

        if (!messageErreur.isEmpty()) {
            afficherAlerte(Alert.AlertType.ERROR, "Données invalides", messageErreur);
            return false;
        }
        return true;
    }

    @FXML
    void ajouterTherapeute() {
        // Utilisation de la méthode de validation
        if (estValide()) {
            try {
                Therapeute t = new Therapeute(txtNom.getText(), txtPrenom.getText(), comboSpecialite.getValue(), txtDiplome.getText(), "");
                st.ajouter(t);
                chargerDonnees();
                viderChamps();
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Thérapeute inséré avec succès !");
            } catch (SQLException e) {
                e.printStackTrace();
                afficherAlerte(Alert.AlertType.ERROR, "Erreur SQL", "Impossible d'ajouter le thérapeute.");
            }
        }
    }

    @FXML
    void supprimerTherapeute() {
        Therapeute t = therapeuteTable.getSelectionModel().getSelectedItem();
        if (t == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Sélection", "Veuillez sélectionner une ligne à supprimer.");
            return;
        }
        try {
            st.supprimer(t.getId());
            chargerDonnees();
            viderChamps();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML
    void modifierTherapeute() {
        Therapeute t = therapeuteTable.getSelectionModel().getSelectedItem();
        if (t != null && estValide()) {
            try {
                t.setNom(txtNom.getText());
                t.setPrenom(txtPrenom.getText());
                t.setSpecialite(comboSpecialite.getValue());
                t.setDiplome(txtDiplome.getText());
                st.modifier(t);
                chargerDonnees();
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Thérapeute modifié avec succès !");
            } catch (SQLException e) { e.printStackTrace(); }
        } else if (t == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Sélection", "Veuillez choisir un thérapeute à modifier.");
        }
    }

    @FXML
    void selectionnerTherapeute() {
        Therapeute t = therapeuteTable.getSelectionModel().getSelectedItem();
        if (t != null) {
            txtNom.setText(t.getNom());
            txtPrenom.setText(t.getPrenom());
            comboSpecialite.setValue(t.getSpecialite());
            txtDiplome.setText(t.getDiplome());
        }
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void viderChamps() {
        txtNom.clear();
        txtPrenom.clear();
        txtDiplome.clear();
        comboSpecialite.setValue(null);
    }
}