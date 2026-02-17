package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.model.User;
import org.example.model.Role;
import org.example.service.UserService;
import org.example.controller.PatientDashboardController;
import org.example.controller.TherapistDashboardController;
import org.example.util.ValidationUtils;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    private final UserService userService = new UserService();

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (ValidationUtils.isEmpty(email) || ValidationUtils.isEmpty(password)) {
            errorLabel.setText("Please enter email and password.");
            return;
        }

        User user = userService.loginUser(email, password);
        if (user != null) {
            System.out.println("Login Successful: " + user.getFullName());

            if (user.getRole() == Role.THERAPIST) {
                navigateToTherapistDashboard(user);
            } else {
                navigateToPatientDashboard(user);
            }
        } else {
            errorLabel.setText("Invalid email or password.");
        }
    }

    private void navigateToTherapistDashboard(User therapist) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/view/therapist_dashboard.fxml"));
            Parent root = loader.load();

            TherapistDashboardController controller = loader.getController();
            controller.setTherapist(therapist);

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setTitle("MindGrow - Therapist Dashboard");
            stage.setScene(new Scene(root, 1024, 768));
            stage.setResizable(true);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void navigateToPatientDashboard(User patient) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/view/patient_dashboard.fxml"));
            Parent root = loader.load();

            PatientDashboardController controller = loader.getController();
            controller.setPatient(patient);

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setTitle("MindGrow - Patient Dashboard");
            stage.setScene(new Scene(root, 1024, 768));
            stage.setResizable(true);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void navigateToSignup() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/view/signup.fxml"));
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
