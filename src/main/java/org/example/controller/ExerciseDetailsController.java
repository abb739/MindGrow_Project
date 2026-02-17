package org.example.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import org.example.model.Exercise;
import org.example.model.User;

import java.io.IOException;

public class ExerciseDetailsController {

    @FXML
    private Label titleLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label timerLabel;
    @FXML
    private Button playPauseBtn;
    @FXML
    private ProgressBar progressBar;

    private User therapist;
    private Exercise exercise;
    private Timeline timeline;
    private int totalSeconds;
    private int remainingSeconds;
    private boolean isRunning = false;

    public void setExerciseData(Exercise exercise, User therapist) {
        this.exercise = exercise;
        this.therapist = therapist;

        titleLabel.setText(exercise.getTitle());
        descriptionLabel.setText(exercise.getDescription());

        // Initialize Timer
        totalSeconds = exercise.getDurationMinutes() * 60;
        remainingSeconds = totalSeconds;
        updateTimerDisplay();

        progressBar.setProgress(0.0);
    }

    private void updateTimerDisplay() {
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));

        double progress = 1.0 - ((double) remainingSeconds / totalSeconds);
        progressBar.setProgress(progress);
    }

    @FXML
    private void handlePlayPause() {
        if (timeline == null) {
            timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                if (remainingSeconds > 0) {
                    remainingSeconds--;
                    updateTimerDisplay();
                } else {
                    timeline.stop();
                    isRunning = false;
                    playPauseBtn.setText("▶ Start Exercise");
                    // Optional: Play a sound or show "Done!"
                }
            }));
            timeline.setCycleCount(Timeline.INDEFINITE);
        }

        if (isRunning) {
            timeline.pause();
            playPauseBtn.setText("▶ Resume Exercise");
            isRunning = false;
        } else {
            timeline.play();
            playPauseBtn.setText("⏸ Pause Exercise");
            isRunning = true;
        }
    }

    @FXML
    private void handleReset() {
        if (timeline != null) {
            timeline.stop();
        }
        isRunning = false;
        remainingSeconds = totalSeconds;
        updateTimerDisplay();
        playPauseBtn.setText("▶ Start Exercise");
    }

    @FXML
    private void handleBack() {
        handleReset(); // Stop timer if running

        try {
            FXMLLoader loader;
            Parent view;

            // Check Role for Navigation
            if (org.example.model.Role.PATIENT == therapist.getRole()) {
                loader = new FXMLLoader(getClass().getResource("/org/example/view/patient_course_details.fxml"));
                view = loader.load();

                PatientCourseDetailsController controller = loader.getController();
                // We need the Course object.
                org.example.service.PatientService patientService = new org.example.service.PatientService();
                org.example.model.Course course = patientService.getCourseById(exercise.getCourseId());

                controller.setCourseData(course, therapist);
            } else {
                loader = new FXMLLoader(getClass().getResource("/org/example/view/programs_view.fxml"));
                view = loader.load();

                ProgramsController controller = loader.getController();
                controller.setTherapist(therapist);
            }

            BorderPane mainLayout = (BorderPane) titleLabel.getScene().lookup("#mainLayout");
            if (mainLayout != null) {
                mainLayout.setCenter(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
