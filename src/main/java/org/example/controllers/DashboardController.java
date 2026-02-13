package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.models.User;
import org.example.services.UserService;
import org.example.utils.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controls the Admin Dashboard with card-based user display.
 * Supports Sidebar navigation, role filtering, and modal forms.
 */
public class DashboardController implements Initializable {

    // Center Content
    @FXML private BorderPane rootPane; // Need reference to root to change center
    @FXML private FlowPane cardsContainer;
    @FXML private TextField searchField;
    @FXML private Label pageTitle;
    @FXML private Label adminNameLabel;
    @FXML private Label adminInitials;
    @FXML private HBox statsContainer;
    @FXML private VBox contentArea; // The VBox in center
    
    // Sidebar Buttons
    @FXML private Button btnOverview;
    @FXML private Button btnPatients;
    // btnDoctors removed
    @FXML private Button btnAdmins;
    @FXML private VBox sidebar; // Reference to sidebar to hide it

    // Stats
    @FXML private Label totalUsersLabel;
    @FXML private Label activePatientsLabel;
    @FXML private Label totalDoctorsLabel;

    private final UserService userService = new UserService();
    private String currentRoleFilter = null; // null = all
    private boolean isAdmin = false;

    // SVG icon paths
    private static final String SVG_EDIT   = "M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z";
    private static final String SVG_DELETE = "M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup user info
        if (SessionManager.isLoggedIn()) {
            User currentUser = SessionManager.getCurrentUser();
            adminNameLabel.setText(currentUser.getNom()); 
            adminInitials.setText(currentUser.getInitials());
            
            // Determine Role
            // Logic: Admin if ROLE_ADMIN OR email ends with '@mindgrow.com'
            isAdmin = currentUser.isAdmin() || currentUser.getEmail().endsWith("@mindgrow.com");
            
            if (!isAdmin) {
                // Non-Admin View: Show Profile Form directly
                showUserProfile(currentUser);
            } else {
                 // Admin View: Default to Overview
                showOverview(new ActionEvent());
            }
        }
        
        // Search listener
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> refreshCards());
        }
    }
    
    /**
     * Loads the User Form directly into the center for non-admins.
     */
    private void showUserProfile(User currentUser) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/views/user_form.fxml"));
            Parent formView = loader.load();
            UserFormController controller = loader.getController();
            
            // Customize form for Profile Mode
            controller.setUser(currentUser);
            controller.setProfileMode();
            
            // Handle Save Action (Update Profile)
            controller.setSaveHandler(updatedUser -> {
                 try {
                        userService.update(updatedUser);
                        // Update session
                        SessionManager.setCurrentUser(updatedUser);
                        
                        // Handle password update separately if changed
                        if (!"KEEP_EXISTING".equals(updatedUser.getMotDePasse())) {
                             userService.updatePassword(updatedUser.getId(), updatedUser.getMotDePasse());
                        }
                        
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully!");
                        
                        // Refresh to show new data (optional, since fields specific to sidebar/header might need update)
                        adminNameLabel.setText(updatedUser.getNom());
                        adminInitials.setText(updatedUser.getInitials());
                        
                    } catch (SQLException e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Update Error", e.getMessage());
                    }
            });
            
            // Hide Sidebar navigation, keep Logout
            if (sidebar != null) {
                // Hide nav buttons
                if (btnOverview != null) { btnOverview.setVisible(false); btnOverview.setManaged(false); }
                if (btnPatients != null) { btnPatients.setVisible(false); btnPatients.setManaged(false); }
                if (btnAdmins != null) { btnAdmins.setVisible(false); btnAdmins.setManaged(false); }
                // Keep sidebar visible for Logout button
            }
            
            // Replace center content
            if (rootPane != null) {
                rootPane.setCenter(formView);
            } else if (contentArea != null) {
                // Fallback if rootPane not injected (it needs fx:id in FXML)
                // Assuming contentArea is the VBox in center
                contentArea.getChildren().clear();
                contentArea.getChildren().add(formView);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load profile view.");
        }
    }

    // ==================== Sidebar Actions ====================

    @FXML
    void showOverview(ActionEvent event) {
        if (!isAdmin) return;
        setActiveButton(btnOverview);
        pageTitle.setText("Overview");
        statsContainer.setVisible(true);
        statsContainer.setManaged(true);
        currentRoleFilter = null;
        refreshCards();
    }

    @FXML
    void showPatients(ActionEvent event) {
        if (!isAdmin) return;
        setActiveButton(btnPatients);
        pageTitle.setText("Patient Management");
        currentRoleFilter = "[\"ROLE_MEMBRE\"]";
        statsContainer.setVisible(false);
        statsContainer.setManaged(false);
        refreshCards();
    }

    // Doctors removed

    @FXML
    void showAdmins(ActionEvent event) {
        if (!isAdmin) return;
        setActiveButton(btnAdmins);
        pageTitle.setText("Administrators");
        currentRoleFilter = "[\"ROLE_ADMIN\"]";
        statsContainer.setVisible(false);
        statsContainer.setManaged(false);
        refreshCards();
    }

    private void setActiveButton(Button active) {
        if (btnOverview != null) btnOverview.getStyleClass().remove("nav-active");
        if (btnPatients != null) btnPatients.getStyleClass().remove("nav-active");
        if (btnAdmins != null) btnAdmins.getStyleClass().remove("nav-active");
        
        if (active != null) active.getStyleClass().add("nav-active");
    }

    // ==================== Data Loading ====================

    private void refreshCards() {
        if (cardsContainer == null) return; // Might be null in Profile View
        
        cardsContainer.getChildren().clear();

        try {
            List<User> users;
            String searchText = searchField.getText() != null ? searchField.getText().trim() : "";

            if (!searchText.isEmpty()) {
                users = userService.searchUsers(searchText);
            } else {
                users = userService.getAllUsers();
            }

            // Filtering
            if (currentRoleFilter != null) {
                String filter = currentRoleFilter;
                users = users.stream()
                        .filter(u -> u.getRoles().contains(filter))
                        .toList();
            }

            // Update stats (only for admin)
            if (isAdmin && totalUsersLabel != null) {
                totalUsersLabel.setText(String.valueOf(users.size()));
                long patients = users.stream().filter(u -> u.getRoles().contains("ROLE_MEMBRE")).count();
                long doctors = users.stream().filter(u -> u.getRoles().contains("ROLE_COACH")).count();
                activePatientsLabel.setText(String.valueOf(patients));
                totalDoctorsLabel.setText(String.valueOf(doctors));
            }

            // Build cards
            for (User user : users) {
                cardsContainer.getChildren().add(createUserCard(user));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load users:\n" + e.getMessage());
        }
    }

    // ==================== Card Generation ====================

    private VBox createUserCard(User user) {
        VBox card = new VBox(12);
        card.getStyleClass().add("user-card");
        card.setPrefWidth(300);

        String role = user.getCleanRole();
        String roleLower = role.toLowerCase();

        // Top Row
        HBox topRow = new HBox(14);
        topRow.setAlignment(Pos.CENTER_LEFT);

        StackPane avatar = new StackPane();
        avatar.getStyleClass().addAll("avatar-circle", "avatar-" + roleLower);
        Label initials = new Label(user.getInitials());
        initials.getStyleClass().addAll("avatar-text", "avatar-text-" + roleLower);
        initials.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        avatar.getChildren().add(initials);

        VBox info = new VBox(3);
        Label nameLabel = new Label(user.getFullName());
        nameLabel.getStyleClass().add("card-name");
        Label emailLabel = new Label(user.getEmail());
        emailLabel.getStyleClass().add("card-email");
        info.getChildren().addAll(nameLabel, emailLabel);

        topRow.getChildren().addAll(avatar, info);

        // Badges
        HBox badges = new HBox(8);
        badges.setAlignment(Pos.CENTER_LEFT);
        Label roleBadge = new Label(role);
        roleBadge.getStyleClass().addAll("role-badge", "role-" + roleLower);
        
        Label statusBadge = new Label("Active");
        statusBadge.getStyleClass().addAll("status-badge", "status-active");
        badges.getChildren().addAll(roleBadge, statusBadge);

        // Separator
        Region separator = new Region();
        separator.getStyleClass().add("card-separator");
        separator.setPrefHeight(1);
        separator.setStyle("-fx-background-color: #E8EDEA;");

        // Actions
        HBox bottomRow = new HBox(8);
        bottomRow.setAlignment(Pos.CENTER_LEFT);
        Label phoneLabel = new Label(user.getTelephone() != null ? "📞 " + user.getTelephone() : "");
        phoneLabel.setStyle("-fx-text-fill: #636E72; -fx-font-size: 12;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button editBtn = createIconButton(SVG_EDIT, "#7C3AED", "edit-button");
        editBtn.setOnAction(e -> handleEditUser(user));
        
        Button deleteBtn = createIconButton(SVG_DELETE, "#DC2626", "delete-button");
        deleteBtn.setOnAction(e -> handleDeleteUser(user));
        
        // HIDE DELETE FOR NON-ADMINS
        if (!isAdmin) {
            deleteBtn.setVisible(false);
            deleteBtn.setManaged(false);
        }

        bottomRow.getChildren().addAll(phoneLabel, spacer, editBtn, deleteBtn);
        card.getChildren().addAll(topRow, badges, separator, bottomRow);
        
        return card;
    }

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

    @FXML
    void handleAddUser(ActionEvent event) {
        // Feature removed as per request (Users register themselves)
        // openUserForm(null);
    }

    private void handleEditUser(User user) {
        openUserForm(user);
    }
    
    /**
     * Opens the Custom FXML User Form.
     */
    private void openUserForm(User userToEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/views/user_form.fxml"));
            Parent root = loader.load();
            UserFormController controller = loader.getController();
            
            // Set data
            controller.setUser(userToEdit);
            controller.setPreselectedRole(currentRoleFilter); // If in "Patients" view, default to Patient role

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setTitle(userToEdit == null ? "Add User" : "Edit User");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            User result = controller.getResult();
            if (result != null) {
                try {
                    if (userToEdit == null) {
                        userService.register(result);
                        showAlert(Alert.AlertType.INFORMATION, "Success", "User created successfully!");
                    } else {
                        result.setId(userToEdit.getId());
                        userService.update(result);
                        if (!"KEEP_EXISTING".equals(result.getMotDePasse())) {
                             userService.updatePassword(result.getId(), result.getMotDePasse());
                        }
                        showAlert(Alert.AlertType.INFORMATION, "Success", "User updated successfully!");
                    }
                    refreshCards();
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "System Error", "Could not load user form.");
        }
    }

    private void handleDeleteUser(User user) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete User");
        confirm.setHeaderText("Delete " + user.getFullName() + "?");
        confirm.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                userService.delete(user.getId());
                refreshCards();
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
            }
        }
    }

    @FXML
    void handleLogout(ActionEvent event) {
        SessionManager.clear();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/views/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 900, 650));
            stage.setMaximized(false); 
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
