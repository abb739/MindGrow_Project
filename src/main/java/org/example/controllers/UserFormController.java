package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.models.User;
import org.example.services.UserService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Controller for the custom User Form dialog.
 * Handles adding and editing users with validation.
 */
public class UserFormController {

    @FXML private Label headerLabel;
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField dobField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private PasswordField passwordField;
    @FXML private Label passwordHint;
    @FXML private Label errorLabel;
    @FXML private VBox passwordContainer;

    private User existingUser;
    private User resultUser;
    private boolean isEditMode = false;

    // Supported input date formats
    private static final DateTimeFormatter[] INPUT_FORMATS = {
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("d/M/yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
    };

    @FXML
    public void initialize() {
        roleCombo.setItems(FXCollections.observableArrayList(
                "[\"ROLE_ADMIN\"]", "[\"ROLE_MEMBRE\"]", "[\"ROLE_COACH\"]"
        ));
    }

    /**
     * Sets the user to edit. If null, assumes "Add Mode".
     */
    public void setUser(User user) {
        this.existingUser = user;
        if (user != null) {
            isEditMode = true;
            headerLabel.setText("Edit User");
            nomField.setText(user.getNom());
            prenomField.setText(user.getPrenom());
            emailField.setText(user.getEmail());
            phoneField.setText(user.getTelephone());
            dobField.setText(user.getDateNaissance());
            roleCombo.setValue(user.getRoles());
            
            passwordField.setPromptText("New Password (Optional)");
            passwordHint.setVisible(true);
            passwordHint.setManaged(true);
        } else {
            isEditMode = false;
            headerLabel.setText("Add New User");
            passwordHint.setVisible(false);
            passwordHint.setManaged(false);
        }
    }
    
    /**
     * Pre-selects a role (e.g. when adding from "Patients" view).
     */
    public void setPreselectedRole(String role) {
        if (!isEditMode && role != null) {
            roleCombo.setValue(role);
        }
    }

    @FXML
    void handleSave(ActionEvent event) {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String dob = dobField.getText().trim();
        String role = roleCombo.getValue();
        String password = passwordField.getText();

        // Validation
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || role == null) {
            showError("Please fill in all required fields (Name, Email, Role).");
            return;
        }

        if (!isEditMode && password.isEmpty()) {
            showError("Password is required for new users.");
            return;
        }

        // Date conversion
        String mysqlDate = convertToMySQLDate(dob);
        if (mysqlDate == null) {
            showError("Invalid date. Use DD/MM/YYYY.");
            return;
        }

        // Create result user
        resultUser = new User();
        resultUser.setNom(nom);
        resultUser.setPrenom(prenom);
        resultUser.setEmail(email);
        resultUser.setTelephone(phone);
        resultUser.setDateNaissance(mysqlDate);
        resultUser.setRoles(role);
        
        // Handle password
        if (isEditMode) {
            resultUser.setId(existingUser.getId());
            // If password is empty in edit mode, use a special flag or keep existing in service logic
            // But here we construct the object to pass back
            if (!password.isEmpty()) {
                resultUser.setMotDePasse(password); 
            } else {
                resultUser.setMotDePasse("KEEP_EXISTING");
            }
        } else {
            resultUser.setMotDePasse(password);
        }

        closeDialog();
    }

    @FXML
    void handleCancel(ActionEvent event) {
        resultUser = null;
        closeDialog();
    }

    public User getResult() {
        return resultUser;
    }

    private void closeDialog() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
    }
    
    private String convertToMySQLDate(String input) {
        if (input == null || input.isEmpty()) return null;
        for (DateTimeFormatter fmt : INPUT_FORMATS) {
            try {
                LocalDate date = LocalDate.parse(input, fmt);
                return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException ignored) {}
        }
        return null;
    }
}
