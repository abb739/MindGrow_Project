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
        // Liaison des colonnes avec le modèle [cite: 126, 127, 128]
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
            // Utilisation d'ObservableList pour la mise à jour en temps réel [cite: 106, 113, 124, 125]
            List<Therapeute> liste = st.afficherAll();
            therapeuteTable.setItems(FXCollections.observableArrayList(liste));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void ajouterTherapeute() {
        // Contrôle de saisie [cite: 98, 101]
        if (txtNom.getText().isEmpty() || txtPrenom.getText().isEmpty() || comboSpecialite.getValue() == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Erreur de saisie", "Veuillez remplir tous les champs !");
            return;
        }
        try {
            Therapeute t = new Therapeute(txtNom.getText(), txtPrenom.getText(), comboSpecialite.getValue(), txtDiplome.getText(), "");
            st.ajouter(t);
            chargerDonnees();
            viderChamps();
            afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Thérapeute inséré avec succès !");
        } catch (SQLException e) { e.printStackTrace(); }
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
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML
    void modifierTherapeute() {
        Therapeute t = therapeuteTable.getSelectionModel().getSelectedItem();
        if (t != null) {
            try {
                t.setNom(txtNom.getText());
                t.setPrenom(txtPrenom.getText());
                t.setSpecialite(comboSpecialite.getValue());
                t.setDiplome(txtDiplome.getText());
                st.modifier(t);
                chargerDonnees();
            } catch (SQLException e) { e.printStackTrace(); }
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
        txtNom.clear(); txtPrenom.clear(); txtDiplome.clear(); comboSpecialite.setValue(null);
    }
}