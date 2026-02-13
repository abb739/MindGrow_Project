package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.paint.Color;
import javafx.scene.Scene;
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
    @FXML private Button cancelButton;

    private User existingUser;
    private User resultUser;
    private boolean isEditMode = false;
    
    // ...

    /**
     * Configures the form for "Profile Mode" (Single User View).
     * Hides Cancel button and Role selection.
     */
    public void setProfileMode() {
        if (cancelButton != null) {
            cancelButton.setVisible(false);
            cancelButton.setManaged(false);
        }
        if (roleCombo != null) {
            roleCombo.setDisable(true); // User cannot change their own role
            // Or hide it:
            // roleCombo.setVisible(false);
            // roleCombo.setManaged(false);
        }
        if (headerLabel != null) {
            headerLabel.setText("My Profile");
        }
    }

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
        updateThemeIcon(false); // Default to Light Mode icon
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

    private java.util.function.Consumer<User> onSaveHandler;

    public void setSaveHandler(java.util.function.Consumer<User> handler) {
        this.onSaveHandler = handler;
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
            if (existingUser != null) resultUser.setId(existingUser.getId());
            
            if (!password.isEmpty()) {
                resultUser.setMotDePasse(password); 
            } else {
                resultUser.setMotDePasse("KEEP_EXISTING");
            }
        } else {
            resultUser.setMotDePasse(password);
        }

        if (onSaveHandler != null) {
            onSaveHandler.accept(resultUser);
        } else {
            closeDialog();
        }
    }

    @FXML
    void handleCancel(ActionEvent event) {
        resultUser = null;
        if (onSaveHandler == null) {
            closeDialog(); // Only close if in dialog mode
        }
    }

    public User getResult() {
        return resultUser;
    }



    @FXML private Button themeToggleBtn;
    
    // SVG Paths
    private static final String SVG_MOON = "M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z";
    private static final String SVG_SUN  = "M12 1v2M12 21v2M4.22 4.22l1.42 1.42M18.36 18.36l1.42 1.42M1 12h2M21 12h2M4.22 19.78l1.42-1.42M18.36 5.64l1.42-1.42M12 17a5 5 0 1 0 0-10 5 5 0 0 0 0 10z";

    @FXML
    void handleThemeToggle(ActionEvent event) {
        if (themeToggleBtn == null) return;
        
        Scene scene = themeToggleBtn.getScene();
        if (scene != null) {
            javafx.scene.Parent root = scene.getRoot();
            boolean isDark = root.getStyleClass().contains("dark-mode");
            
            if (isDark) {
                root.getStyleClass().remove("dark-mode");
                updateThemeIcon(false);
            } else {
                root.getStyleClass().add("dark-mode"); // Add dark mode logic
                updateThemeIcon(true);
            }
        }
    }
    
    private void updateThemeIcon(boolean isDark) {
        if (themeToggleBtn == null) return;
        themeToggleBtn.setText(""); // Clear text
        
        SVGPath icon = new SVGPath();
        icon.setContent(isDark ? SVG_SUN : SVG_MOON); // If Dark, show Sun to switch to Light
        icon.setFill(Color.web(isDark ? "#FFFFFF" : "#4A4A4A"));
        icon.setScaleX(1.2);
        icon.setScaleY(1.2);
        
        themeToggleBtn.setGraphic(icon);
    }
    
    public void setDarkMode(boolean active) {
        updateThemeIcon(active);
    }

    private void closeDialog() {
        if (nomField.getScene() != null && nomField.getScene().getWindow() instanceof Stage) {
            ((Stage) nomField.getScene().getWindow()).close();
        }
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
