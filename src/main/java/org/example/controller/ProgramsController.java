package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.example.model.Course;
import org.example.model.User;
import org.example.service.TherapistService;
import org.example.util.ValidationUtils;

import java.io.IOException;
import java.util.Optional;

public class ProgramsController {

    @FXML
    private FlowPane coursesContainer;

    private User therapist;
    private final TherapistService therapistService = new TherapistService();

    public void setTherapist(User therapist) {
        this.therapist = therapist;
        loadCourses();
    }

    private void loadCourses() {
        if (coursesContainer == null)
            return;
        coursesContainer.getChildren().clear();
        for (Course course : therapistService.getCoursesByTherapist(therapist.getId())) {
            coursesContainer.getChildren().add(createCourseCard(course));
        }
    }

    private VBox createCourseCard(Course course) {
        VBox card = new VBox(10);
        card.getStyleClass().add("program-card");

        // Ensure card has a fixed or preferred size to look consistent
        card.setPrefWidth(200);
        card.setPrefHeight(150);

        Label title = new Label(course.getTitle());
        title.getStyleClass().add("card-title");
        title.setWrapText(true);

        Label desc = new Label(course.getDescription());
        desc.getStyleClass().add("card-desc");
        desc.setWrapText(true);

        // Spacer to push delete button to bottom
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        // Buttons Container
        javafx.scene.layout.HBox buttons = new javafx.scene.layout.HBox(10);

        Button editBtn = new Button("Edit");
        editBtn.getStyleClass().add("secondary-button"); // Assuming we have this, or use distinct style
        editBtn.setMaxWidth(Double.MAX_VALUE);
        javafx.scene.layout.HBox.setHgrow(editBtn, javafx.scene.layout.Priority.ALWAYS);

        editBtn.setOnAction(e -> {
            // Prevent card click
            e.consume();
            handleEditCourse(course);
        });

        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("danger-button");
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        javafx.scene.layout.HBox.setHgrow(deleteBtn, javafx.scene.layout.Priority.ALWAYS);

        deleteBtn.setOnAction(e -> {
            e.consume();
            if (therapistService.deleteCourse(course.getId())) {
                loadCourses();
            }
        });

        buttons.getChildren().addAll(editBtn, deleteBtn);

        card.getChildren().addAll(title, desc, spacer, buttons);

        // Add click listener to navigate to Course Details
        card.setOnMouseClicked(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/view/course_details.fxml"));
                Parent view = loader.load();

                CourseDetailsController controller = loader.getController();
                controller.setCourseData(course, therapist);

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
    private void handleAddCourse() {
        Dialog<Course> dialog = new Dialog<>();
        dialog.setTitle("Add New Course");
        dialog.setHeaderText("Enter Course Details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        VBox content = new VBox(10);
        TextField titleField = new TextField();
        titleField.setPromptText("Course Title");
        TextField descField = new TextField();
        descField.setPromptText("Course Description");

        content.getChildren().addAll(new Label("Title:"), titleField, new Label("Description:"), descField);

        dialog.getDialogPane().setContent(content);

        // Validation for Add
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(addButtonType);
        btOk.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (ValidationUtils.isEmpty(titleField.getText()) || ValidationUtils.isEmpty(descField.getText())) {
                ValidationUtils.showError("Validation Error", "Title and Description cannot be empty.");
                event.consume();
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new Course(titleField.getText(), descField.getText(), therapist.getId());
            }
            return null;
        });

        Optional<Course> result = dialog.showAndWait();
        result.ifPresent(course -> {
            therapistService.addCourse(course.getTitle(), course.getDescription(), course.getTherapistId());
            loadCourses();
        });
    }

    private void handleEditCourse(Course course) {
        Dialog<Course> dialog = new Dialog<>();
        dialog.setTitle("Edit Course");
        dialog.setHeaderText("Update Course Details");

        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        VBox content = new VBox(10);
        TextField titleField = new TextField(course.getTitle());
        titleField.setPromptText("Course Title");
        TextField descField = new TextField(course.getDescription());
        descField.setPromptText("Course Description");

        content.getChildren().addAll(new Label("Title:"), titleField, new Label("Description:"), descField);

        dialog.getDialogPane().setContent(content);

        // Validation for Edit
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(updateButtonType);
        btOk.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (ValidationUtils.isEmpty(titleField.getText()) || ValidationUtils.isEmpty(descField.getText())) {
                ValidationUtils.showError("Validation Error", "Title and Description cannot be empty.");
                event.consume();
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                course.setTitle(titleField.getText());
                course.setDescription(descField.getText());
                return course;
            }
            return null;
        });

        Optional<Course> result = dialog.showAndWait();
        result.ifPresent(updatedCourse -> {
            therapistService.updateCourse(updatedCourse);
            loadCourses();
        });
    }
}
