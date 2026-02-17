package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.model.Course;
import org.example.model.User;
import org.example.service.PatientService;

import java.io.IOException;

public class PatientCoursesController {

    @FXML
    private Label pageTitle;
    @FXML
    private FlowPane coursesContainer;

    private User patient;
    private boolean showMyCourses;
    private final PatientService patientService = new PatientService();

    public void setPatientData(User patient, boolean showMyCourses) {
        this.patient = patient;
        this.showMyCourses = showMyCourses;

        if (showMyCourses) {
            pageTitle.setText("My Courses");
        } else {
            pageTitle.setText("All Courses");
        }

        loadCourses();
    }

    private void loadCourses() {
        coursesContainer.getChildren().clear();

        java.util.List<Course> courses;
        if (showMyCourses) {
            courses = patientService.getEnrolledCourses(patient.getId());
        } else {
            courses = patientService.getAllCourses();
        }

        for (Course course : courses) {
            coursesContainer.getChildren().add(createCourseCard(course));
        }
    }

    private VBox createCourseCard(Course course) {
        VBox card = new VBox(10);
        card.getStyleClass().add("program-card");
        card.setPrefWidth(200);
        card.setPrefHeight(180);

        Label title = new Label(course.getTitle());
        title.getStyleClass().add("card-title");
        title.setWrapText(true);

        Label desc = new Label(course.getDescription());
        desc.getStyleClass().add("card-desc");
        desc.setWrapText(true);

        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Button actionBtn = new Button();
        actionBtn.setMaxWidth(Double.MAX_VALUE);

        // check if already enrolled
        boolean isEnrolled = false;
        if (!showMyCourses) {
            long count = patientService.getEnrolledCourses(patient.getId()).stream()
                    .filter(c -> c.getId() == course.getId()).count();
            isEnrolled = count > 0;
        } else {
            isEnrolled = true;
        }

        if (isEnrolled) {
            actionBtn.setText("Continue");
            actionBtn.getStyleClass().add("primary-button");
            actionBtn.setOnAction(e -> openCourseDetails(course));

            // Add progress bar if enrolled
            double progress = patientService.getProgress(patient.getId(), course.getId());
            ProgressBar pb = new ProgressBar(progress);
            pb.setMaxWidth(Double.MAX_VALUE);
            pb.setStyle("-fx-accent: #A2D5AB;");
            card.getChildren().addAll(title, desc, spacer, pb, actionBtn);
        } else {
            actionBtn.setText("Enroll");
            actionBtn.setStyle("-fx-background-color: #444; -fx-text-fill: white;");
            actionBtn.setOnAction(e -> {
                if (patientService.enrollInCourse(patient.getId(), course.getId())) {
                    // Switch to My Courses or just refresh
                    setPatientData(patient, true);
                }
            });
            card.getChildren().addAll(title, desc, spacer, actionBtn);
        }

        return card;
    }

    private void openCourseDetails(Course course) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/view/patient_course_details.fxml"));
            Parent view = loader.load();

            PatientCourseDetailsController controller = loader.getController();
            controller.setCourseData(course, patient);

            javafx.scene.layout.BorderPane mainLayout = (javafx.scene.layout.BorderPane) coursesContainer.getScene()
                    .lookup("#mainLayout");
            if (mainLayout != null) {
                mainLayout.setCenter(view);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
