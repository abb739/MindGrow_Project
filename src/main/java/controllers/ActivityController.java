package controllers;

import Entities.Activity;
import Entities.ActivityType;
import GUI.HomePage;
import Services.ActivityCRUD;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;

public class ActivityController implements Initializable {

    @FXML private ComboBox<ActivityType> comboType;
    @FXML private TextField txtDescription;
    @FXML private TextField txtDuree;
    @FXML private Spinner<LocalTime> txtStartTime;
    @FXML private ComboBox<Integer> comboSession;

    @FXML private TableView<Activity> tableActivity;
    @FXML private TableColumn<Activity, Integer> IDColmn;
    @FXML private TableColumn<Activity, String> typeColmn;
    @FXML private TableColumn<Activity, String> descColmn;
    @FXML private TableColumn<Activity, Integer> dureeColmn;
    @FXML private TableColumn<Activity, String> startTimeColmn;
    @FXML private TableColumn<Activity, Integer> sessionColmn;

    @FXML private Button btnAdd;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;
    @FXML private Button btnBackToSession; // bouton retour

    private ActivityCRUD activityCRUD;
    private Activity selectedActivity;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        activityCRUD = new ActivityCRUD();

        // Spinner heure
        SpinnerValueFactory<LocalTime> valueFactory = new SpinnerValueFactory<LocalTime>() {
            { setValue(LocalTime.now()); }
            @Override public void decrement(int steps) { setValue(getValue().minusMinutes(30)); }
            @Override public void increment(int steps) { setValue(getValue().plusMinutes(30)); }
        };
        txtStartTime.setValueFactory(valueFactory);

        // Charger ComboBox Type
        comboType.getItems().setAll(ActivityType.values());

        // Charger IDs des sessions
        loadSessionIds();

        // Charger table
        loadTable();

        // Sélection TableView
        tableActivity.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                selectedActivity = newSel;
                fillFields(selectedActivity);
            }
        });
    }

    private void loadSessionIds() {
        try {
            comboSession.getItems().clear();
            var conn = Utils.MyBD.getInstance().getConn();
            var stmt = conn.createStatement();
            var rs = stmt.executeQuery("SELECT id FROM seance");
            while (rs.next()) comboSession.getItems().add(rs.getInt("id"));
            rs.close(); stmt.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private String getColorForType(ActivityType type) {
        switch (type) {
            case MEDITATION: return "#6fa8dc";
            case ATELIER_ESTIME_DE_SOI: return "#8e7cc3";
            case THERAPIE_DE_GROUPE: return "#e06666";
            case EXERCICES_DE_RESPIRATION: return "#93c47d";
            case DEFI_GRATITUDE: return "#f6b26b";
            case COACHING_INDIVIDUEL: return "#ffd966";
            case MINDFULNESS: return "#76a5af";
            default: return "#000000";
        }
    }

    private void loadTable() {
        try {
            List<Activity> list = activityCRUD.afficher();
            ObservableList<Activity> obsList = FXCollections.observableArrayList(list);

            IDColmn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getIdActivity()));
            typeColmn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getType().toString()));
            descColmn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getDescription()));
            dureeColmn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getDuree()));
            startTimeColmn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getStartTime().toString()));
            sessionColmn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getSessionId()));

            tableActivity.setItems(obsList);

            // Colorer toute la ligne selon le type
            tableActivity.setRowFactory(tv -> new TableRow<Activity>() {
                @Override
                protected void updateItem(Activity item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) setStyle("");
                    else {
                        String color = getColorForType(item.getType());
                        setStyle("-fx-background-color: " + color + "; -fx-text-fill:white; -fx-font-weight:bold;");
                    }
                }
            });
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void fillFields(Activity a) {
        comboType.setValue(a.getType());
        txtDescription.setText(a.getDescription());
        txtDuree.setText(String.valueOf(a.getDuree()));
        txtStartTime.getValueFactory().setValue(a.getStartTime());
        comboSession.setValue(a.getSessionId());
    }

    private void clearFields() {
        comboType.setValue(null);
        txtDescription.clear();
        txtDuree.clear();
        txtStartTime.getValueFactory().setValue(LocalTime.now());
        comboSession.setValue(null);
        selectedActivity = null;
    }

    private boolean validateFields() {
        if (comboType.getValue() == null || txtDescription.getText().isEmpty()) {
            showAlert("Erreur", "Type et description sont obligatoires !");
            return false;
        }
        try {
            int duree = Integer.parseInt(txtDuree.getText());
            if (duree <= 0) { showAlert("Erreur", "Durée doit être supérieure à 0 !"); return false; }
        } catch (NumberFormatException e) { showAlert("Erreur", "Durée invalide !"); return false; }
        if (comboSession.getValue() == null) { showAlert("Erreur", "Sélectionne une session !"); return false; }
        return true;
    }

    @FXML
    private void Add(ActionEvent event) {
        try {
            if (!validateFields()) return;
            Activity a = new Activity();
            a.setType(comboType.getValue());
            a.setDescription(txtDescription.getText());
            a.setDuree(Integer.parseInt(txtDuree.getText()));
            a.setStartTime(txtStartTime.getValue());
            a.setSessionId(comboSession.getValue());

            if (activityCRUD.existsSameTime(a.getSessionId(), a.getStartTime(), null)) {
                showAlert("Erreur", "Une activité existe déjà pour cette session à cette heure !");
                return;
            }

            activityCRUD.ajouter(a);
            showAlert("Succès", "Activité ajoutée !");
            loadTable();
            clearFields();
        } catch (Exception e) { e.printStackTrace(); showAlert("Erreur", "Vérifie les champs !"); }
    }

    @FXML
    private void Update(ActionEvent event) {
        selectedActivity = tableActivity.getSelectionModel().getSelectedItem();
        if (selectedActivity == null) { showAlert("Erreur", "Sélectionne d'abord une activité !"); return; }

        try {
            if (!validateFields()) return;

            selectedActivity.setType(comboType.getValue());
            selectedActivity.setDescription(txtDescription.getText());
            selectedActivity.setDuree(Integer.parseInt(txtDuree.getText()));
            selectedActivity.setStartTime(txtStartTime.getValue());
            selectedActivity.setSessionId(comboSession.getValue());

            if (activityCRUD.existsSameTime(selectedActivity.getSessionId(), selectedActivity.getStartTime(), selectedActivity.getIdActivity())) {
                showAlert("Erreur", "Une activité existe déjà pour cette session à cette heure !");
                return;
            }

            activityCRUD.modifier(selectedActivity);
            showAlert("Succès", "Activité modifiée !");
            loadTable();
            clearFields();
        } catch (Exception e) { e.printStackTrace(); showAlert("Erreur", "Vérifie les champs !"); }
    }

    @FXML
    private void Delete(ActionEvent event) {
        selectedActivity = tableActivity.getSelectionModel().getSelectedItem();
        if (selectedActivity == null) { showAlert("Erreur", "Sélectionne d'abord une activité !"); return; }
        try {
            activityCRUD.supprimer(selectedActivity.getIdActivity());
            loadTable();
            clearFields();
            showAlert("Succès", "Activité supprimée !");
        } catch (Exception e) { e.printStackTrace(); showAlert("Erreur", "Impossible de supprimer !"); }
    }

    @FXML
    private void goToSession() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/new.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnBackToSession.getScene().getWindow();
            stage.setScene(new Scene(root, HomePage.WIDTH, HomePage.HEIGHT));
            stage.setTitle("MindGrow - Gestion des Séances");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(msg); alert.showAndWait();
    }
}
