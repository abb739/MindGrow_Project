package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.models.User;
import org.example.services.UserService;
import org.example.utils.SessionManager;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Controls the Login screen.
 * Authenticates the user and stores session on success.
 */
public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final UserService userService = new UserService();

    /**
     * Handles the Sign In button click.
     */
    @FXML
    void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both email and password.");
            return;
        }

        if (!org.example.utils.InputValidator.isValidEmail(email)) {
            errorLabel.setText("Invalid email format.");
            return;
        }

        try {
            User user = userService.authenticate(email, password);
            if (user != null) {
                // Store session
                SessionManager.setCurrentUser(user);
                System.out.println("Login successful: " + user.getEmail());
                // Navigate to dashboard
                // navigateTo(event, "/org/example/views/dashboard.fxml", true);
                
                // Fix resolution for small screens? No, keep maximized unless user wants otherwise.
                navigateTo(event, "/org/example/views/dashboard.fxml", true);

            } else {
                errorLabel.setText("Invalid email or password.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("Database error. Please try again.");
        }
    }

    /**
     * Navigates to the Forgot Password screen.
     */
    @FXML
    void handleForgotPassword(ActionEvent event) {
        navigateTo(event, "/org/example/views/forgotpassword.fxml", false);
    }

    /**
     * Navigates to the Register screen.
     */
    @FXML
    void goToRegister(ActionEvent event) {
        navigateTo(event, "/org/example/views/register.fxml", false);
    }

    /**
     * Helper: navigates to a new FXML view.
     * @param maximize if true, sets the window to maximized state (full screen)
     */
    private void navigateTo(ActionEvent event, String fxmlPath, boolean maximize) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            if (maximize) {
                stage.setMaximized(true);
            }
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Navigation error.");
        }
    }
}
