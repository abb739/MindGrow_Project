package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.example.model.User;

import java.io.IOException;

public class TherapistDashboardController {

    @FXML
    private BorderPane mainLayout;
    @FXML
    private Label welcomeLabel;

    private User therapist;

    public void setTherapist(User therapist) {
        this.therapist = therapist;
        welcomeLabel.setText("Welcome, " + therapist.getFullName());
        loadProgramsView(); // Load Programs by default
    }

    @FXML
    private void loadProgramsView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/view/programs_view.fxml"));
            Parent view = loader.load();

            ProgramsController controller = loader.getController();
            controller.setTherapist(therapist);

            mainLayout.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/view/login.fxml"));
            mainLayout.getScene().getWindow().sizeToScene(); // Reset size? Or keeps current
            mainLayout.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
