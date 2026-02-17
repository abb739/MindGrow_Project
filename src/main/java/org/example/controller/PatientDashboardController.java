package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.model.User;

import java.io.IOException;

public class PatientDashboardController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private BorderPane mainLayout;

    private User patient;

    public void setPatient(User patient) {
        this.patient = patient;
        welcomeLabel.setText("Welcome, " + patient.getFullName());
        loadMyCoursesView(); // Load My Courses by default
    }

    @FXML
    private void loadMyCoursesView() {
        loadCoursesView(true);
    }

    @FXML
    private void loadAllCoursesView() {
        loadCoursesView(false);
    }

    private void loadCoursesView(boolean showMyCourses) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/view/patient_courses.fxml"));
            Parent view = loader.load();

            PatientCoursesController controller = loader.getController();
            controller.setPatientData(patient, showMyCourses);

            mainLayout.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/view/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
