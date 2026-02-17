package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import org.example.model.Course;
import org.example.model.Exercise;
import org.example.model.User;
import org.example.model.Video;
import org.example.service.TherapistService;
import org.example.service.PatientService;

import java.io.IOException;

public class PatientCourseDetailsController {

    @FXML
    private Label courseTitleLabel;
    @FXML
    private Label courseDescLabel;
    @FXML
    private ProgressBar courseProgressBar;
    @FXML
    private Label progressTextLabel;
    @FXML
    private VBox contentContainer;

    private Course course;
    private User patient;
    private final TherapistService therapistService = new TherapistService(); // To get content
    private final PatientService patientService = new PatientService(); // To track progress

    public void setCourseData(Course course, User patient) {
        this.course = course;
        this.patient = patient;

        courseTitleLabel.setText(course.getTitle());
        courseDescLabel.setText(course.getDescription());

        updateProgress();
        loadContent();
    }

    private void updateProgress() {
        double progress = patientService.getProgress(patient.getId(), course.getId());
        courseProgressBar.setProgress(progress);
        progressTextLabel.setText(String.format("%.0f%% Completed", progress * 100));
    }

    private void loadContent() {
        contentContainer.getChildren().clear();

        // Load Videos
        Label videoHeader = new Label("Videos");
        videoHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-padding: 10 0 5 0;");
        contentContainer.getChildren().add(videoHeader);

        for (Video video : therapistService.getVideosByCourse(course.getId())) {
            contentContainer.getChildren().add(createContentItem(video));
        }

        // Load Exercises
        Label exerciseHeader = new Label("Exercises");
        exerciseHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-padding: 20 0 5 0;");
        contentContainer.getChildren().add(exerciseHeader);

        for (Exercise exercise : therapistService.getExercisesByCourse(course.getId())) {
            contentContainer.getChildren().add(createContentItem(exercise));
        }
    }

    private VBox createContentItem(Object content) {
        VBox card = new VBox(5);
        card.setStyle(
                "-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 3, 0, 0, 2);");

        String titleText = "";
        String descText = "";
        String type = "";
        int id = 0;

        if (content instanceof Video) {
            Video v = (Video) content;
            titleText = "🎥 " + v.getTitle();
            descText = v.getDescription();
            type = "VIDEO";
            id = v.getId();
        } else if (content instanceof Exercise) {
            Exercise e = (Exercise) content;
            titleText = "🧘 " + e.getTitle();
            descText = e.getDurationMinutes() + " mins - " + e.getDescription();
            type = "EXERCISE";
            id = e.getId();
        }

        Label title = new Label(titleText);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label desc = new Label(descText);
        desc.setWrapText(true);
        desc.setStyle("-fx-text-fill: #666;");

        // Mark as Done Button
        Button doneBtn = new Button("Mark as Complete");
        doneBtn.getStyleClass().add("primary-button");
        doneBtn.setStyle("-fx-font-size: 12px; -fx-padding: 5 10;");

        final String finalType = type;
        final int finalId = id;

        // Check initial state
        boolean isCompleted = patientService.isContentCompleted(patient.getId(), course.getId(), finalType, finalId);
        updateButtonState(doneBtn, isCompleted);

        doneBtn.setOnAction(e -> {
            boolean currentStatus = "✔ Completed".equals(doneBtn.getText());
            if (currentStatus) {
                // Unmark
                if (patientService.removeContentCompletion(patient.getId(), course.getId(), finalType, finalId)) {
                    updateButtonState(doneBtn, false);
                    updateProgress();
                }
            } else {
                // Mark
                if (patientService.markContentComplete(patient.getId(), course.getId(), finalType, finalId)) {
                    updateButtonState(doneBtn, true);
                    updateProgress();
                }
            }
        });

        card.getChildren().addAll(title, desc, doneBtn);

        // Click to view details
        card.setOnMouseClicked(e -> {
            if (content instanceof Video) {
                openVideoPlayer((Video) content);
            } else if (content instanceof Exercise) {
                openExerciseTimer((Exercise) content);
            }
        });

        return card;
    }

    private void openVideoPlayer(Video video) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/view/video_details.fxml"));
            Parent view = loader.load();
            VideoDetailsController controller = loader.getController();
            controller.setVideoData(video, patient); // Using patient as user

            // We need a way to go back here. This is getting tricky with deep navigation.
            // For now, let's just push it. The back button in VideoDetailsController goes
            // separately.
            // Ideally we should update VideoDetailsController to have a flexible back
            // action.

            javafx.scene.layout.BorderPane mainLayout = (javafx.scene.layout.BorderPane) courseTitleLabel.getScene()
                    .lookup("#mainLayout");
            if (mainLayout != null) {
                mainLayout.setCenter(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openExerciseTimer(Exercise exercise) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/view/exercise_details.fxml"));
            Parent view = loader.load();
            ExerciseDetailsController controller = loader.getController();
            controller.setExerciseData(exercise, patient);

            javafx.scene.layout.BorderPane mainLayout = (javafx.scene.layout.BorderPane) courseTitleLabel.getScene()
                    .lookup("#mainLayout");
            if (mainLayout != null) {
                mainLayout.setCenter(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/view/patient_courses.fxml"));
            Parent view = loader.load();
            PatientCoursesController controller = loader.getController();
            controller.setPatientData(patient, true); // Go back to My Courses

            javafx.scene.layout.BorderPane mainLayout = (javafx.scene.layout.BorderPane) courseTitleLabel.getScene()
                    .lookup("#mainLayout");
            if (mainLayout != null) {
                mainLayout.setCenter(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateButtonState(Button btn, boolean isCompleted) {
        if (isCompleted) {
            btn.setText("✔ Completed");
            btn.setStyle(
                    "-fx-background-color: #A2D5AB; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10;");
        } else {
            btn.setText("Mark as Complete");
            btn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333; -fx-font-size: 12px; -fx-padding: 5 10;");
        }
    }
}
