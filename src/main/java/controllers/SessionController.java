package controllers;

import Entities.Session;
import Entities.SessionStatus;
import Entities.SessionType;
import GUI.HomePage;
import Services.SessionCRUD;
import Utils.MyBD;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class SessionController implements Initializable {

    @FXML private DatePicker txtDate;
    @FXML private Spinner<LocalTime> txtStartTime;
    @FXML private TextField txtTitre;
    @FXML private TextField txtDescription;
    @FXML private TextField txtDuree;
    @FXML private TextField txtLien;
    @FXML private ComboBox<SessionType> comboType;
    @FXML private ComboBox<SessionStatus> txtStatus;
    @FXML private ComboBox<Integer> txtCoach;

    @FXML private TableView<Session> tableSession;
    @FXML private TableColumn<Session, Integer> IDColmn;
    @FXML private TableColumn<Session, String> titreColmn;
    @FXML private TableColumn<Session, String> descColmn;
    @FXML private TableColumn<Session, String> DateColmn;
    @FXML private TableColumn<Session, String> timeColomn;
    @FXML private TableColumn<Session, Integer> DureeColmn;
    @FXML private TableColumn<Session, String> linkColmn;
    @FXML private TableColumn<Session, String> TypeColmn;
    @FXML private TableColumn<Session, String> statusColmn;
    @FXML private TableColumn<Session, Integer> CoachColmn;

    @FXML private Button btnADD;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;
    @FXML private Button btnGoToActivity; // ← bouton navigation

    private SessionCRUD sessionCRUD;
    private Session selectedSession;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sessionCRUD = new SessionCRUD();

        // Charger enums
        comboType.setItems(FXCollections.observableArrayList(SessionType.values()));
        txtStatus.setItems(FXCollections.observableArrayList(SessionStatus.values()));

        // Charger IDs des coachs
        loadCoachIds();

        // Spinner heure
        SpinnerValueFactory<LocalTime> valueFactory = new SpinnerValueFactory<LocalTime>() {
            { setValue(LocalTime.now()); }
            @Override public void decrement(int steps) { setValue(getValue().minusMinutes(30)); }
            @Override public void increment(int steps) { setValue(getValue().plusMinutes(30)); }
        };
        txtStartTime.setValueFactory(valueFactory);

        // Charger table
        loadTable();

        // Sélection TableView
        tableSession.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                selectedSession = newSel;
                fillFields(selectedSession);
            }
        });
    }

    // ==========================
    // LOAD COACH IDs
    // ==========================
    private void loadCoachIds() {
        try {
            txtCoach.getItems().clear();
            Connection conn = MyBD.getInstance().getConn();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id FROM coach");
            while (rs.next()) txtCoach.getItems().add(rs.getInt("id"));
            rs.close(); stmt.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ==========================
    // VALIDATION DES CHAMPS
    // ==========================
    private boolean validateFields() {
        String errorMsg = "";

        if (txtTitre.getText() == null || txtTitre.getText().trim().isEmpty()) errorMsg += "Le titre est requis.\n";
        if (txtDescription.getText() == null || txtDescription.getText().trim().isEmpty()) errorMsg += "La description est requise.\n";
        if (txtDate.getValue() == null) errorMsg += "La date de début est requise.\n";
        if (txtStartTime.getValue() == null) errorMsg += "L'heure de début est requise.\n";

        if (txtDuree.getText() == null || txtDuree.getText().trim().isEmpty()) errorMsg += "La durée est requise.\n";
        else {
            try {
                int duree = Integer.parseInt(txtDuree.getText());
                if (duree <= 0) errorMsg += "La durée doit être > 0.\n";
            } catch (NumberFormatException e) { errorMsg += "La durée doit être un nombre entier.\n"; }
        }

        if (comboType.getValue() == null) errorMsg += "Le type de session est requis.\n";
        if (txtStatus.getValue() == null) errorMsg += "Le status est requis.\n";
        if (txtCoach.getValue() == null) errorMsg += "Le coach doit être sélectionné.\n";

        if (txtLien.getText() != null && !txtLien.getText().trim().isEmpty()) {
            String urlPattern = "^(https?:\\/\\/)(www\\.)?[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}([\\/\\w\\.-]*)*\\/?$";
            if (!txtLien.getText().matches(urlPattern)) errorMsg += "Le lien doit être une URL valide.\n";
        }

        if (!errorMsg.isEmpty()) { showAlert("Erreur de saisie", errorMsg); return false; }
        return true;
    }

    // ==========================
    // ADD / UPDATE / DELETE
    // ==========================
    @FXML
    private void Add(ActionEvent event) {
        if (!validateFields()) return;
        try {
            Session s = new Session();
            s.setTitle(txtTitre.getText());
            s.setDescription(txtDescription.getText());
            s.setStart_Session(txtDate.getValue());
            s.setStart_Time(txtStartTime.getValue());
            s.setDureeSession(Integer.parseInt(txtDuree.getText()));
            s.setTypeSession(comboType.getValue());
            s.setLien(txtLien.getText());
            s.setStatus(txtStatus.getValue());
            s.setIdCoach(txtCoach.getValue());
            sessionCRUD.ajouter(s);
            showAlert("Succès", "Session ajoutée !");
            loadTable(); clearFields();
        } catch (Exception e) { e.printStackTrace(); showAlert("Erreur", "Impossible d'ajouter la session !"); }
    }

    @FXML
    private void Update(ActionEvent event) {
        selectedSession = tableSession.getSelectionModel().getSelectedItem();
        if (selectedSession == null) { showAlert("Erreur", "Sélectionne d'abord une session !"); return; }
        if (!validateFields()) return;
        try {
            selectedSession.setTitle(txtTitre.getText());
            selectedSession.setDescription(txtDescription.getText());
            selectedSession.setStart_Session(txtDate.getValue());
            selectedSession.setStart_Time(txtStartTime.getValue());
            selectedSession.setDureeSession(Integer.parseInt(txtDuree.getText()));
            selectedSession.setTypeSession(comboType.getValue());
            selectedSession.setLien(txtLien.getText());
            selectedSession.setStatus(txtStatus.getValue());
            selectedSession.setIdCoach(txtCoach.getValue());
            sessionCRUD.modifier(selectedSession);
            tableSession.refresh();
            showAlert("Succès", "Session modifiée !");
            clearFields();
        } catch (Exception e) { e.printStackTrace(); showAlert("Erreur", "Impossible de modifier la session !"); }
    }

    @FXML
    private void Delete(ActionEvent event) {
        selectedSession = tableSession.getSelectionModel().getSelectedItem();
        if (selectedSession == null) { showAlert("Erreur", "Sélectionne d'abord une session !"); return; }
        try {
            sessionCRUD.supprimer(selectedSession.getIdSession());
            showAlert("Succès", "Session supprimée !");
            loadTable(); clearFields();
        } catch (Exception e) { e.printStackTrace(); showAlert("Erreur", "Impossible de supprimer !"); }
    }

    // ==========================
    // LOAD TABLE
    // ==========================
    private void loadTable() {
        try {
            ObservableList<Session> list = FXCollections.observableArrayList(sessionCRUD.afficher());

            IDColmn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getIdSession()));
            titreColmn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTitle()));
            descColmn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getDescription()));
            DateColmn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getStart_Session().toString()));
            timeColomn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getStart_Time().toString()));
            DureeColmn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getDureeSession()));
            linkColmn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getLien()));
            TypeColmn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTypeSession().name()));
            statusColmn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getStatus().name()));
            CoachColmn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getIdCoach()));

            tableSession.setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ==========================
    // FILL / CLEAR FIELDS
    // ==========================
    private void fillFields(Session s) {
        txtTitre.setText(s.getTitle());
        txtDescription.setText(s.getDescription());
        txtDate.setValue(s.getStart_Session());
        txtStartTime.getValueFactory().setValue(s.getStart_Time());
        txtDuree.setText(String.valueOf(s.getDureeSession()));
        comboType.setValue(s.getTypeSession());
        txtLien.setText(s.getLien());
        txtStatus.setValue(s.getStatus());
        txtCoach.setValue(s.getIdCoach());
    }

    private void clearFields() {
        txtTitre.clear(); txtDescription.clear(); txtDate.setValue(null);
        txtStartTime.getValueFactory().setValue(LocalTime.now());
        txtDuree.clear(); comboType.setValue(null); txtLien.clear(); txtStatus.setValue(null); txtCoach.setValue(null);
        selectedSession = null;
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(msg); alert.showAndWait();
    }

    // ==========================
    // NAVIGATION → Activity.fxml
    // ==========================
    @FXML
    private void goToActivity() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ok.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnGoToActivity.getScene().getWindow();
            stage.setScene(new Scene(root, HomePage.WIDTH, HomePage.HEIGHT)); // même taille
            stage.setTitle("MindGrow - Gestion des Activités");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
