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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.models.User;
import org.example.services.EmailService;
import org.example.services.UserService;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Controls the Forgot Password flow:
 * Step 1: Enter email -> send reset code
 * Step 2: Enter code + new password -> reset
 */
public class ForgotPasswordController {

    @FXML private VBox step1Pane;
    @FXML private VBox step2Pane;
    @FXML private TextField emailField;
    @FXML private TextField tokenField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label statusLabel;

    private final UserService userService = new UserService();
    private final EmailService emailService = new EmailService();

    /**
     * Step 1: Send reset code via email.
     */
    @FXML
    void handleSendCode(ActionEvent event) {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            showError("Please enter your email address.");
            return;
        }

        try {
            User user = userService.getUserByEmail(email);
            if (user == null) {
                showError("No account found with that email.");
                return;
            }

            // Generate token and send email
            String token = userService.generateResetToken(user.getId());
            boolean sent = emailService.sendResetEmail(email, token);

            if (sent) {
                showSuccess("Reset code sent! Check your inbox.");
                // Show step 2
                step1Pane.setVisible(false);
                step1Pane.setManaged(false);
                step2Pane.setVisible(true);
                step2Pane.setManaged(true);
            } else {
                showError("Failed to send email. Check your email configuration.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database error: " + e.getMessage());
        }
    }

    /**
     * Step 2: Validate token and reset password.
     */
    @FXML
    void handleResetPassword(ActionEvent event) {
        String token = tokenField.getText().trim();
        String newPass = newPasswordField.getText();
        String confirmPass = confirmPasswordField.getText();

        if (token.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            showError("Passwords do not match.");
            return;
        }

        if (newPass.length() < 6) {
            showError("Password must be at least 6 characters.");
            return;
        }

        try {
            User user = userService.getUserByResetToken(token);
            if (user == null) {
                showError("Invalid or expired reset code.");
                return;
            }

            userService.updatePassword(user.getId(), newPass);
            showSuccess("Password reset successfully! You can now log in.");

            // Wait a moment then go back to login
            javafx.application.Platform.runLater(() -> {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException ignored) {}
                goToLogin(event);
            });

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error resetting password: " + e.getMessage());
        }
    }

    /**
     * Navigate back to login.
     */
    @FXML
    void goToLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/views/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showError(String msg) {
        statusLabel.setText(msg);
        statusLabel.setStyle("-fx-text-fill: #E17055;");
    }

    private void showSuccess(String msg) {
        statusLabel.setText(msg);
        statusLabel.setStyle("-fx-text-fill: #5E8B6E;");
    }
}
