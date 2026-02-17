package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import org.example.model.User;
import org.example.model.Video;

import java.io.File;
import java.io.IOException;

public class VideoDetailsController {

    @FXML
    private Label titleLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private MediaView mediaView;

    private User therapist;
    private Video video;
    private MediaPlayer mediaPlayer;

    @FXML
    private javafx.scene.control.Slider progressSlider;
    @FXML
    private javafx.scene.control.Slider volumeSlider;
    @FXML
    private Label currentTimeLabel;
    @FXML
    private Label totalTimeLabel;
    @FXML
    private javafx.scene.control.Button playPauseBtn;

    public void setVideoData(Video video, User therapist) {
        this.video = video;
        this.therapist = therapist;

        titleLabel.setText(video.getTitle());
        descriptionLabel.setText(video.getDescription());

        // Setup Media Player
        if (video.getUrl() != null && !video.getUrl().isEmpty()) {
            try {
                String mediaUrl = video.getUrl();
                if (!mediaUrl.startsWith("file:") && !mediaUrl.startsWith("http")) {
                    mediaUrl = new File(mediaUrl).toURI().toString();
                }

                Media media = new Media(mediaUrl);
                mediaPlayer = new MediaPlayer(media);
                mediaView.setMediaPlayer(mediaPlayer);
                mediaPlayer.setAutoPlay(false);

                // Media Player Listeners
                mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                    if (!progressSlider.isValueChanging()) {
                        progressSlider.setValue(newValue.toSeconds());
                    }
                    currentTimeLabel.setText(formatTime(newValue));
                });

                mediaPlayer.setOnReady(() -> {
                    double duration = media.getDuration().toSeconds();
                    progressSlider.setMax(duration);
                    totalTimeLabel.setText(formatTime(media.getDuration()));
                });

                // Slider Listeners
                progressSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                    if (progressSlider.isValueChanging()) {
                        mediaPlayer.seek(javafx.util.Duration.seconds(newVal.doubleValue()));
                    }
                });

                progressSlider.setOnMouseClicked(e -> {
                    mediaPlayer.seek(javafx.util.Duration.seconds(progressSlider.getValue()));
                });

                volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                    mediaPlayer.setVolume(newVal.doubleValue());
                });

            } catch (Exception e) {
                System.err.println("Error loading media: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private String formatTime(javafx.util.Duration duration) {
        int seconds = (int) duration.toSeconds();
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    @FXML
    private void handlePlayPause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                playPauseBtn.setText("▶");
            } else {
                mediaPlayer.play();
                playPauseBtn.setText("⏸");
            }
        }
    }

    @FXML
    private void handleStop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            playPauseBtn.setText("▶");
            mediaPlayer.seek(javafx.util.Duration.ZERO);
            progressSlider.setValue(0);
        }
    }

    @FXML
    private void handleBack() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        try {
            FXMLLoader loader;
            Parent view;

            // Check Role for Navigation
            if (org.example.model.Role.PATIENT == therapist.getRole()) { // we renamed field to therapist but it holds
                                                                         // current user
                loader = new FXMLLoader(getClass().getResource("/org/example/view/patient_course_details.fxml"));
                view = loader.load();

                PatientCourseDetailsController controller = loader.getController();
                // We need the Course object.
                org.example.service.PatientService patientService = new org.example.service.PatientService();
                org.example.model.Course course = patientService.getCourseById(video.getCourseId());

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
