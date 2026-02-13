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

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Controls the Registration screen.
 * Handles date format conversion (DD/MM/YYYY -> YYYY-MM-DD).
 */
public class RegisterController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField dobField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private final UserService userService = new UserService();

    // Supported input date formats
    private static final DateTimeFormatter[] INPUT_FORMATS = {
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("dd-MM-yyyy"),
        DateTimeFormatter.ofPattern("d/M/yyyy"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("MM/dd/yyyy"),
    };

    @FXML
    void handleRegister(ActionEvent event) {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String dob = dobField.getText().trim();
        String password = passwordField.getText();

        // Validation
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty() || dob.isEmpty()) {
            showError("Please fill all fields.");
            return;
        }

        if (!email.contains("@")) {
            showError("Please enter a valid email address.");
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters.");
            return;
        }

        // Convert date format to YYYY-MM-DD for MySQL
        String mysqlDate = convertToMySQLDate(dob);
        if (mysqlDate == null) {
            showError("Invalid date format. Use DD/MM/YYYY (e.g. 10/01/2004).");
            return;
        }

        User newUser = new User(nom, prenom, email, password, "[\"ROLE_MEMBRE\"]", phone, mysqlDate);

        try {
            userService.register(newUser);
            showSuccess("Registration successful! 🎉");

            // Wait then go to login
            new Thread(() -> {
                try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
                javafx.application.Platform.runLater(() -> goToLogin(event));
            }).start();

        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getMessage().contains("Email already exists")) {
                showError("This email is already registered.");
            } else {
                showError("Registration failed: " + e.getMessage());
            }
        }
    }

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

    /**
     * Converts various date formats (DD/MM/YYYY, DD-MM-YYYY, etc.) to MySQL YYYY-MM-DD.
     */
    private String convertToMySQLDate(String input) {
        for (DateTimeFormatter fmt : INPUT_FORMATS) {
            try {
                LocalDate date = LocalDate.parse(input, fmt);
                return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException ignored) {
                // Try next format
            }
        }
        return null;
    }

    private void showError(String msg) {
        statusLabel.setText(msg);
        statusLabel.setStyle("-fx-text-fill: #E17055; -fx-font-size: 13;");
    }

    private void showSuccess(String msg) {
        statusLabel.setText(msg);
        statusLabel.setStyle("-fx-text-fill: #5E8B6E; -fx-font-size: 13; -fx-font-weight: bold;");
    }
}
