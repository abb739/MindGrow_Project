package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.example.models.User;
import org.example.services.UserService;
import org.example.utils.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controls the Admin Dashboard with card-based user display.
 * Supports search, filter by role, add/edit/delete user.
 */
public class DashboardController implements Initializable {

    @FXML private FlowPane cardsContainer;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> roleFilter;
    @FXML private Label totalUsersLabel;
    @FXML private Label adminNameLabel;

    private final UserService userService = new UserService();

    // SVG icon paths
    private static final String SVG_EDIT   = "M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z";
    private static final String SVG_DELETE = "M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16";
    private static final String SVG_LOGOUT = "M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1";

    // Supported input date formats
    private static final DateTimeFormatter[] INPUT_FORMATS = {
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("d/M/yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup admin name from session
        if (SessionManager.isLoggedIn()) {
            User admin = SessionManager.getCurrentUser();
            adminNameLabel.setText("Welcome, " + admin.getPrenom() + " " + admin.getNom());
        }

        // Setup role filter
        roleFilter.setItems(FXCollections.observableArrayList(
                "All Roles", "Admin", "Patient", "Doctor"
        ));
        roleFilter.setValue("All Roles");

        // Listeners for search & filter
        searchField.textProperty().addListener((obs, oldVal, newVal) -> refreshCards());
        roleFilter.valueProperty().addListener((obs, oldVal, newVal) -> refreshCards());

        // Initial load
        refreshCards();
    }

    /**
     * Reloads all user cards based on current search/filter.
     */
    private void refreshCards() {
        cardsContainer.getChildren().clear();

        try {
            List<User> users;
            String searchText = searchField.getText() != null ? searchField.getText().trim() : "";
            String selectedRole = roleFilter.getValue();

            // Get base data
            if (!searchText.isEmpty()) {
                users = userService.searchUsers(searchText);
            } else {
                users = userService.getAllUsers();
            }

            // Apply role filter
            if (selectedRole != null && !"All Roles".equals(selectedRole)) {
                String roleKey = switch (selectedRole) {
                    case "Admin"   -> "ROLE_ADMIN";
                    case "Patient" -> "ROLE_MEMBRE";
                    case "Doctor"  -> "ROLE_COACH";
                    default        -> "";
                };
                users = users.stream()
                        .filter(u -> u.getRoles() != null && u.getRoles().contains(roleKey))
                        .toList();
            }

            // Build cards
            for (User user : users) {
                cardsContainer.getChildren().add(createUserCard(user));
            }

            totalUsersLabel.setText(String.valueOf(users.size()));

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load users:\n" + e.getMessage());
        }
    }

    /**
     * Creates a single user card as a VBox.
     */
    private VBox createUserCard(User user) {
        VBox card = new VBox(12);
        card.getStyleClass().add("user-card");
        card.setPrefWidth(300);

        String role = user.getCleanRole();
        String roleLower = role.toLowerCase();

        // ----- Top row: Avatar + Info -----
        HBox topRow = new HBox(14);
        topRow.setAlignment(Pos.CENTER_LEFT);

        // Avatar circle
        StackPane avatar = new StackPane();
        avatar.getStyleClass().addAll("avatar-circle", "avatar-" + roleLower);
        Label initials = new Label(user.getInitials());
        initials.getStyleClass().addAll("avatar-text", "avatar-text-" + roleLower);
        initials.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        avatar.getChildren().add(initials);

        // Name + Email
        VBox info = new VBox(3);
        Label nameLabel = new Label(user.getFullName());
        nameLabel.getStyleClass().add("card-name");
        Label emailLabel = new Label(user.getEmail());
        emailLabel.getStyleClass().add("card-email");
        info.getChildren().addAll(nameLabel, emailLabel);

        topRow.getChildren().addAll(avatar, info);

        // ----- Middle row: Badges -----
        HBox badges = new HBox(8);
        badges.setAlignment(Pos.CENTER_LEFT);

        // Role badge
        Label roleBadge = new Label(role);
        roleBadge.getStyleClass().addAll("role-badge", "role-" + roleLower);

        // Status badge
        String status = user.getStatus() != null ? user.getStatus() : "Active";
        Label statusBadge = new Label(status);
        statusBadge.getStyleClass().addAll("status-badge",
                "Active".equalsIgnoreCase(status) ? "status-active" : "status-inactive");

        badges.getChildren().addAll(roleBadge, statusBadge);

        // ----- Separator -----
        Region separator = new Region();
        separator.getStyleClass().add("card-separator");
        separator.setPrefHeight(1);
        separator.setStyle("-fx-background-color: #E8EDEA;");

        // ----- Bottom row: Phone + Actions -----
        HBox bottomRow = new HBox(8);
        bottomRow.setAlignment(Pos.CENTER_LEFT);

        // Phone info
        Label phoneLabel = new Label(user.getTelephone() != null ? "📞 " + user.getTelephone() : "");
        phoneLabel.setStyle("-fx-text-fill: #636E72; -fx-font-size: 12;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Edit button with SVG icon
        Button editBtn = createIconButton(SVG_EDIT, "#7C3AED", "edit-button");
        editBtn.setOnAction(e -> handleEditUser(user));
        Tooltip.install(editBtn, new Tooltip("Edit user"));

        // Delete button with SVG icon
        Button deleteBtn = createIconButton(SVG_DELETE, "#DC2626", "delete-button");
        deleteBtn.setOnAction(e -> handleDeleteUser(user));
        Tooltip.install(deleteBtn, new Tooltip("Delete user"));

        bottomRow.getChildren().addAll(phoneLabel, spacer, editBtn, deleteBtn);

        card.getChildren().addAll(topRow, badges, separator, bottomRow);
        return card;
    }

    /**
     * Creates a button with an SVG icon inside.
     */
    private Button createIconButton(String svgPath, String color, String styleClass) {
        SVGPath icon = new SVGPath();
        icon.setContent(svgPath);
        icon.setFill(Color.web(color));
        icon.setScaleX(0.7);
        icon.setScaleY(0.7);

        Button btn = new Button();
        btn.setGraphic(icon);
        btn.getStyleClass().add(styleClass);
        return btn;
    }

    // ==================== User Actions ====================

    /**
     * Opens a dialog to add a new user.
     */
    @FXML
    void handleAddUser(ActionEvent event) {
        Dialog<User> dialog = createUserDialog("Add New User", null);
        Optional<User> result = dialog.showAndWait();
        result.ifPresent(user -> {
            try {
                userService.register(user);
                refreshCards();
                showAlert(Alert.AlertType.INFORMATION, "Success", "User created successfully!");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to create user:\n" + e.getMessage());
            }
        });
    }

    /**
     * Opens a pre-filled dialog to edit a user.
     */
    private void handleEditUser(User user) {
        Dialog<User> dialog = createUserDialog("Edit User", user);
        Optional<User> result = dialog.showAndWait();
        result.ifPresent(updated -> {
            try {
                updated.setId(user.getId());
                userService.update(updated);
                refreshCards();
                showAlert(Alert.AlertType.INFORMATION, "Success", "User updated successfully!");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update user:\n" + e.getMessage());
            }
        });
    }

    /**
     * Shows a confirmation dialog, then deletes the user.
     */
    private void handleDeleteUser(User user) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete User");
        confirm.setHeaderText("Are you sure?");
        confirm.setContentText("Delete " + user.getFullName() + " (" + user.getEmail() + ")?\nThis action cannot be undone.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                userService.delete(user.getId());
                refreshCards();
                showAlert(Alert.AlertType.INFORMATION, "Deleted", "User deleted successfully.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete user:\n" + e.getMessage());
            }
        }
    }

    /**
     * Creates a user form dialog (for both Add and Edit).
     */
    private Dialog<User> createUserDialog(String title, User existing) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(title);

        // Buttons
        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        // Form fields
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(24));

        TextField nomField     = new TextField();
        nomField.setPromptText("Last Name");
        TextField prenomField  = new TextField();
        prenomField.setPromptText("First Name");
        TextField emailField   = new TextField();
        emailField.setPromptText("Email");
        TextField phoneField   = new TextField();
        phoneField.setPromptText("Phone");
        TextField dobField     = new TextField();
        dobField.setPromptText("DD/MM/YYYY or YYYY-MM-DD");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        ComboBox<String> roleCombo = new ComboBox<>(FXCollections.observableArrayList(
                "[\"ROLE_ADMIN\"]", "[\"ROLE_MEMBRE\"]", "[\"ROLE_COACH\"]"
        ));
        roleCombo.setPromptText("Select Role");

        // Pre-fill for edit
        if (existing != null) {
            nomField.setText(existing.getNom());
            prenomField.setText(existing.getPrenom());
            emailField.setText(existing.getEmail());
            phoneField.setText(existing.getTelephone());
            dobField.setText(existing.getDateNaissance());
            roleCombo.setValue(existing.getRoles());
            passField.setPromptText("Leave blank to keep current");
        }

        grid.add(new Label("Last Name:"),  0, 0);  grid.add(nomField,    1, 0);
        grid.add(new Label("First Name:"), 0, 1);  grid.add(prenomField, 1, 1);
        grid.add(new Label("Email:"),      0, 2);  grid.add(emailField,  1, 2);
        grid.add(new Label("Phone:"),      0, 3);  grid.add(phoneField,  1, 3);
        grid.add(new Label("Birth Date:"), 0, 4);  grid.add(dobField,    1, 4);
        grid.add(new Label("Role:"),       0, 5);  grid.add(roleCombo,   1, 5);

        if (existing == null) {
            grid.add(new Label("Password:"), 0, 6);
            grid.add(passField, 1, 6);
        }

        dialog.getDialogPane().setContent(grid);

        // Style the dialog
        dialog.getDialogPane().setStyle(
                "-fx-background-color: white; -fx-font-family: 'Segoe UI';"
        );

        // Convert result
        dialog.setResultConverter(btn -> {
            if (btn == saveButton) {
                String nom    = nomField.getText().trim();
                String prenom = prenomField.getText().trim();
                String email  = emailField.getText().trim();
                String phone  = phoneField.getText().trim();
                String dob    = dobField.getText().trim();
                String role   = roleCombo.getValue();
                String pass   = passField.getText();

                if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Validation", "Name and email are required.");
                    return null;
                }

                if (existing == null && (pass == null || pass.isEmpty())) {
                    showAlert(Alert.AlertType.WARNING, "Validation", "Password is required for new users.");
                    return null;
                }

                // Convert date
                String mysqlDate = convertToMySQLDate(dob);
                if (mysqlDate == null) {
                    showAlert(Alert.AlertType.WARNING, "Validation", "Invalid date. Use DD/MM/YYYY.");
                    return null;
                }

                User u = new User(nom, prenom, email,
                        pass != null && !pass.isEmpty() ? pass : "KEEP_EXISTING",
                        role != null ? role : "[\"ROLE_MEMBRE\"]",
                        phone, mysqlDate);
                return u;
            }
            return null;
        });

        return dialog;
    }

    /**
     * Converts various date formats to MySQL YYYY-MM-DD.
     */
    private String convertToMySQLDate(String input) {
        for (DateTimeFormatter fmt : INPUT_FORMATS) {
            try {
                LocalDate date = LocalDate.parse(input, fmt);
                return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException ignored) {
                // Try next
            }
        }
        return null;
    }

    /**
     * Handles logout — clears session and returns to login.
     */
    @FXML
    void handleLogout(ActionEvent event) {
        SessionManager.clear();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/views/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setMaximized(false);
            stage.setScene(new Scene(root, 900, 650));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows a styled alert dialog.
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
