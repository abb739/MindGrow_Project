package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.example.model.Course;
import org.example.model.Exercise;
import org.example.model.User;
import org.example.model.Video;
import org.example.service.TherapistService;
import org.example.util.ValidationUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

public class CourseDetailsController {

    @FXML
    private Label courseTitleLabel;
    @FXML
    private Label courseDescLabel;
    @FXML
    private FlowPane videosContainer;
    @FXML
    private FlowPane exercisesContainer;

    private Course course;
    private User therapist;
    private final TherapistService therapistService = new TherapistService();

    public void setCourseData(Course course, User therapist) {
        this.course = course;
        this.therapist = therapist;

        courseTitleLabel.setText(course.getTitle());
        courseDescLabel.setText(course.getDescription());

        loadVideos();
        loadExercises();
    }

    private void loadVideos() {
        videosContainer.getChildren().clear();
        for (Video video : therapistService.getVideosByCourse(course.getId())) {
            videosContainer.getChildren().add(createVideoCard(video));
        }
    }

    private void loadExercises() {
        exercisesContainer.getChildren().clear();
        for (Exercise exercise : therapistService.getExercisesByCourse(course.getId())) {
            exercisesContainer.getChildren().add(createExerciseCard(exercise));
        }
    }

    private VBox createVideoCard(Video video) {
        VBox card = new VBox(10);
        card.getStyleClass().add("program-card");

        Label title = new Label(video.getTitle());
        title.getStyleClass().add("card-title");

        Label desc = new Label(video.getDescription());
        desc.getStyleClass().add("card-desc");

        // Spacer to push delete button to bottom
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        // Buttons Container
        javafx.scene.layout.HBox buttons = new javafx.scene.layout.HBox(10);

        Button editBtn = new Button("Edit");
        editBtn.getStyleClass().add("secondary-button");
        editBtn.setMaxWidth(Double.MAX_VALUE);
        javafx.scene.layout.HBox.setHgrow(editBtn, javafx.scene.layout.Priority.ALWAYS);

        editBtn.setOnAction(e -> {
            e.consume();
            handleEditVideo(video);
        });

        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("danger-button");
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        javafx.scene.layout.HBox.setHgrow(deleteBtn, javafx.scene.layout.Priority.ALWAYS);

        deleteBtn.setOnAction(e -> {
            e.consume();
            if (therapistService.deleteVideo(video.getId())) {
                loadVideos();
            }
        });

        buttons.getChildren().addAll(editBtn, deleteBtn);

        card.getChildren().addAll(title, desc, spacer, buttons);

        // Add click listener to navigate to details
        card.setOnMouseClicked(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/view/video_details.fxml"));
                Parent view = loader.load();

                VideoDetailsController controller = loader.getController();
                controller.setVideoData(video, therapist); // We pass therapist, maybe need course context too?

                javafx.scene.layout.BorderPane mainLayout = (javafx.scene.layout.BorderPane) card.getScene()
                        .lookup("#mainLayout");
                if (mainLayout != null) {
                    mainLayout.setCenter(view);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        return card;
    }

    private VBox createExerciseCard(Exercise exercise) {
        VBox card = new VBox(10);
        card.getStyleClass().add("program-card");

        Label title = new Label(exercise.getTitle());
        title.getStyleClass().add("card-title");

        Label desc = new Label(exercise.getDescription());
        desc.getStyleClass().add("card-desc");

        Label duration = new Label(exercise.getDurationMinutes() + " mins");
        duration.setStyle("-fx-text-fill: #A2D5AB; -fx-font-weight: bold;");

        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        // Buttons Container
        javafx.scene.layout.HBox buttons = new javafx.scene.layout.HBox(10);

        Button editBtn = new Button("Edit");
        editBtn.getStyleClass().add("secondary-button");
        editBtn.setMaxWidth(Double.MAX_VALUE);
        javafx.scene.layout.HBox.setHgrow(editBtn, javafx.scene.layout.Priority.ALWAYS);

        editBtn.setOnAction(e -> {
            e.consume();
            handleEditExercise(exercise);
        });

        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("danger-button");
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        javafx.scene.layout.HBox.setHgrow(deleteBtn, javafx.scene.layout.Priority.ALWAYS);

        deleteBtn.setOnAction(e -> {
            e.consume();
            if (therapistService.deleteExercise(exercise.getId())) {
                loadExercises();
            }
        });

        buttons.getChildren().addAll(editBtn, deleteBtn);

        card.getChildren().addAll(title, desc, duration, spacer, buttons);

        card.setOnMouseClicked(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/view/exercise_details.fxml"));
                Parent view = loader.load();

                ExerciseDetailsController controller = loader.getController();
                controller.setExerciseData(exercise, therapist);

                javafx.scene.layout.BorderPane mainLayout = (javafx.scene.layout.BorderPane) card.getScene()
                        .lookup("#mainLayout");
                if (mainLayout != null) {
                    mainLayout.setCenter(view);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        return card;
    }

    @FXML
    private void handleAddVideo() {
        Dialog<Video> dialog = new Dialog<>();
        dialog.setTitle("Add Video to Course");
        dialog.setHeaderText("Enter Video Details");

        ButtonType loginButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        VBox content = new VBox(10);
        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        TextField descField = new TextField();
        descField.setPromptText("Description");

        Button fileBtn = new Button("Choose Video File");
        Label fileLabel = new Label("No file selected");
        final StringBuilder selectedFilePath = new StringBuilder();

        fileBtn.setOnAction(e -> {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Select Video File");
            fileChooser.getExtensionFilters().addAll(
                    new javafx.stage.FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.avi", "*.mkv"));
            File selectedFile = fileChooser.showOpenDialog(fileBtn.getScene().getWindow());
            if (selectedFile != null) {
                fileLabel.setText(selectedFile.getName());
                try {
                    String userHome = System.getProperty("user.home");
                    Path uploadDir = Paths.get(userHome, ".mindgrow", "videos");
                    if (!Files.exists(uploadDir))
                        Files.createDirectories(uploadDir);

                    String newFileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                    Path targetPath = uploadDir.resolve(newFileName);
                    Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                    selectedFilePath.setLength(0);
                    selectedFilePath.append(targetPath.toUri().toString());
                } catch (IOException ex) {
                    ex.printStackTrace();
                    fileLabel.setText("Error saving file");
                }
            }
        });

        content.getChildren().addAll(new Label("Title:"), titleField, new Label("Description:"), descField,
                new Label("Video File:"), fileBtn, fileLabel);
        dialog.getDialogPane().setContent(content);

        // Validation for Add Video
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(loginButtonType);
        btOk.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String videoUrl = selectedFilePath.toString();
            if (ValidationUtils.isEmpty(titleField.getText()) || ValidationUtils.isEmpty(descField.getText())
                    || ValidationUtils.isEmpty(videoUrl)) {
                ValidationUtils.showError("Validation Error", "Title, Description and Video File are required.");
                event.consume();
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                String videoUrl = selectedFilePath.toString();
                return new Video(titleField.getText(), descField.getText(), videoUrl, course.getId());
            }
            return null;
        });

        Optional<Video> result = dialog.showAndWait();
        result.ifPresent(video -> {
            therapistService.addVideo(video.getTitle(), video.getDescription(), video.getUrl(), video.getCourseId());
            loadVideos();
        });
    }

    @FXML
    private void handleAddExercise() {
        Dialog<Exercise> dialog = new Dialog<>();
        dialog.setTitle("Add Exercise to Course");
        dialog.setHeaderText("Enter Exercise Details");

        ButtonType loginButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        VBox content = new VBox(10);
        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        TextField descField = new TextField();
        descField.setPromptText("Description");
        TextField durationField = new TextField();
        durationField.setPromptText("Duration (mins)");
        content.getChildren().addAll(new Label("Title:"), titleField, new Label("Description:"), descField,
                new Label("Duration:"), durationField);

        dialog.getDialogPane().setContent(content);

        // Validation for Add Exercise
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(loginButtonType);
        btOk.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (ValidationUtils.isEmpty(titleField.getText()) || ValidationUtils.isEmpty(descField.getText())) {
                ValidationUtils.showError("Validation Error", "Title and Description are required.");
                event.consume();
                return;
            }
            if (!ValidationUtils.isPositiveInteger(durationField.getText())) {
                ValidationUtils.showError("Validation Error", "Duration must be a positive number.");
                event.consume();
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                try {
                    int duration = Integer.parseInt(durationField.getText());
                    return new Exercise(titleField.getText(), descField.getText(), duration, course.getId());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        Optional<Exercise> result = dialog.showAndWait();
        result.ifPresent(exercise -> {
            therapistService.addExercise(exercise.getTitle(), exercise.getDescription(), exercise.getDurationMinutes(),
                    exercise.getCourseId());
            loadExercises();
        });
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/view/programs_view.fxml"));
            Parent view = loader.load();
            ProgramsController controller = loader.getController();
            controller.setTherapist(therapist);

            javafx.scene.layout.BorderPane mainLayout = (javafx.scene.layout.BorderPane) courseTitleLabel.getScene()
                    .lookup("#mainLayout");
            if (mainLayout != null) {
                mainLayout.setCenter(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleEditVideo(Video video) {
        Dialog<Video> dialog = new Dialog<>();
        dialog.setTitle("Edit Video");
        dialog.setHeaderText("Update Video Details");

        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        VBox content = new VBox(10);
        TextField titleField = new TextField(video.getTitle());
        titleField.setPromptText("Title");
        TextField descField = new TextField(video.getDescription());
        descField.setPromptText("Description");

        TextField urlField = new TextField(video.getUrl());
        urlField.setPromptText("URL or File Path");

        content.getChildren().addAll(new Label("Title:"), titleField,
                new Label("Description:"), descField,
                new Label("URL/Path:"), urlField);

        dialog.getDialogPane().setContent(content);

        // Validation for Edit Video
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(updateButtonType);
        btOk.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (ValidationUtils.isEmpty(titleField.getText()) || ValidationUtils.isEmpty(descField.getText())
                    || ValidationUtils.isEmpty(urlField.getText())) {
                ValidationUtils.showError("Validation Error", "All fields are required.");
                event.consume();
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                video.setTitle(titleField.getText());
                video.setDescription(descField.getText());
                video.setUrl(urlField.getText());
                return video;
            }
            return null;
        });

        Optional<Video> result = dialog.showAndWait();
        result.ifPresent(updatedVideo -> {
            therapistService.updateVideo(updatedVideo);
            loadVideos();
        });
    }

    private void handleEditExercise(Exercise exercise) {
        Dialog<Exercise> dialog = new Dialog<>();
        dialog.setTitle("Edit Exercise");
        dialog.setHeaderText("Update Exercise Details");

        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        VBox content = new VBox(10);
        TextField titleField = new TextField(exercise.getTitle());
        titleField.setPromptText("Title");
        TextField descField = new TextField(exercise.getDescription());
        descField.setPromptText("Description");
        TextField durationField = new TextField(String.valueOf(exercise.getDurationMinutes()));
        durationField.setPromptText("Duration (mins)");

        content.getChildren().addAll(new Label("Title:"), titleField,
                new Label("Description:"), descField,
                new Label("Duration (mins):"), durationField);

        dialog.getDialogPane().setContent(content);

        // Validation for Edit Exercise
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(updateButtonType);
        btOk.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (ValidationUtils.isEmpty(titleField.getText()) || ValidationUtils.isEmpty(descField.getText())) {
                ValidationUtils.showError("Validation Error", "Title and Description are required.");
                event.consume();
                return;
            }
            if (!ValidationUtils.isPositiveInteger(durationField.getText())) {
                ValidationUtils.showError("Validation Error", "Duration must be a positive number.");
                event.consume();
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                try {
                    int duration = Integer.parseInt(durationField.getText());
                    exercise.setTitle(titleField.getText());
                    exercise.setDescription(descField.getText());
                    exercise.setDurationMinutes(duration);
                    return exercise;
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        Optional<Exercise> result = dialog.showAndWait();
        result.ifPresent(updatedExercise -> {
            therapistService.updateExercise(updatedExercise);
            loadExercises();
        });
    }
}
