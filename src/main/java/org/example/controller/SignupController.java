package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.model.Role;
import org.example.service.UserService;
import org.example.util.ValidationUtils;

import java.io.IOException;

public class SignupController {

    @FXML
    private TextField fullNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField cinField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<Role> roleComboBox;
    @FXML
    private Label errorLabel;

    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        roleComboBox.getItems().setAll(Role.values());
    }

    @FXML
    private void handleSignup() {
        String fullName = fullNameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String cin = cinField.getText();
        String password = passwordField.getText();
        Role role = roleComboBox.getValue();

        if (ValidationUtils.isEmpty(fullName) || ValidationUtils.isEmpty(email) || ValidationUtils.isEmpty(phone)
                || ValidationUtils.isEmpty(cin) || ValidationUtils.isEmpty(password)
                || role == null) {
            errorLabel.setText("Please fill in all required fields.");
            return;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            errorLabel.setText("Invalid email format.");
            return;
        }

        if (!ValidationUtils.isValidPhone(phone)) {
            errorLabel.setText("Phone must be 8 digits.");
            return;
        }

        if (!ValidationUtils.isValidCIN(cin)) {
            errorLabel.setText("CIN must be 8 digits.");
            return;
        }

        if (password.length() < 6) {
            errorLabel.setText("Password must be at least 6 characters.");
            return;
        }

        if (userService.registerUser(fullName, email, phone, cin, role, password)) {
            navigateToLogin();
        } else {
            errorLabel.setText("Registration failed. Email or CIN might be taken.");
        }
    }

    @FXML
    private void navigateToLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/view/login.fxml"));
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
