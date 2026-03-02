package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

public class MainApp extends Application {

    private BorderPane root;
    private Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #f8fafc;");

        // Afficher le dashboard au démarrage
        showDashboard();

        // Créer la scène
        scene = new Scene(root, 1400, 800);

        primaryStage.setTitle("MindGrow - Bienvenue");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private void showDashboard() {
        VBox dashboard = new VBox(30);
        dashboard.setAlignment(Pos.TOP_CENTER);
        dashboard.setPadding(new Insets(60));
        dashboard.setStyle("-fx-background-color: #f8fafc;");

        // En-tête avec logo colors
        VBox headerBox = new VBox(15);
        headerBox.setAlignment(Pos.CENTER);

        Label welcomeLabel = new Label("🌱 Bienvenue sur MindGrow");
        welcomeLabel.setStyle("-fx-font-size: 42px; -fx-font-weight: bold; -fx-text-fill: #0B7A8F;");

        Label subtitleLabel = new Label("STAYS • EXPERIENCES • EVENTS");
        subtitleLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #D2691E; -fx-font-weight: bold;");

        headerBox.getChildren().addAll(welcomeLabel, subtitleLabel);

        // Cartes statistiques
        HBox statsBox = new HBox(30);
        statsBox.setAlignment(Pos.CENTER);

        VBox cardClients = createStatCard("👤", "Clients", "Gérer vos utilisateurs");

        statsBox.getChildren().addAll(cardClients);

        // Actions rapides - DEUX BOUTONS
        VBox actionsBox = new VBox(20);
        actionsBox.setAlignment(Pos.CENTER);
        actionsBox.setMaxWidth(600);

        Label actionsTitle = new Label("Choisissez votre interface");
        actionsTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0B7A8F;");

        // Bouton BackOffice
        Button btnBackOffice = new Button("🔧 BackOffice (Administration)");
        btnBackOffice.setStyle(
                "-fx-background-color: #0B7A8F; -fx-text-fill: white; " +
                        "-fx-font-size: 16px; -fx-font-weight: bold; " +
                        "-fx-padding: 20px 40px; -fx-background-radius: 12px; -fx-cursor: hand;");
        btnBackOffice.setPrefWidth(350);
        btnBackOffice.setPrefHeight(60);
        btnBackOffice.setOnMouseEntered(e -> btnBackOffice.setStyle(
                "-fx-background-color: #085968; -fx-text-fill: white; " +
                        "-fx-font-size: 16px; -fx-font-weight: bold; " +
                        "-fx-padding: 20px 40px; -fx-background-radius: 12px; -fx-cursor: hand;"));
        btnBackOffice.setOnMouseExited(e -> btnBackOffice.setStyle(
                "-fx-background-color: #0B7A8F; -fx-text-fill: white; " +
                        "-fx-font-size: 16px; -fx-font-weight: bold; " +
                        "-fx-padding: 20px 40px; -fx-background-radius: 12px; -fx-cursor: hand;"));
        btnBackOffice.setOnAction(e -> {
            System.out.println("Bouton BackOffice cliqué");
            showBackOffice();
        });

        // Bouton FrontOffice
        Button btnFrontOffice = new Button("🏖️ FrontOffice (Réservations Utilisateurs)");
        btnFrontOffice.setStyle(
                "-fx-background-color: #D2691E; -fx-text-fill: white; " +
                        "-fx-font-size: 16px; -fx-font-weight: bold; " +
                        "-fx-padding: 20px 40px; -fx-background-radius: 12px; -fx-cursor: hand;");
        btnFrontOffice.setPrefWidth(350);
        btnFrontOffice.setPrefHeight(60);
        btnFrontOffice.setOnMouseEntered(e -> btnFrontOffice.setStyle(
                "-fx-background-color: #C55A11; -fx-text-fill: white; " +
                        "-fx-font-size: 16px; -fx-font-weight: bold; " +
                        "-fx-padding: 20px 40px; -fx-background-radius: 12px; -fx-cursor: hand;"));
        btnFrontOffice.setOnMouseExited(e -> btnFrontOffice.setStyle(
                "-fx-background-color: #D2691E; -fx-text-fill: white; " +
                        "-fx-font-size: 16px; -fx-font-weight: bold; " +
                        "-fx-padding: 20px 40px; -fx-background-radius: 12px; -fx-cursor: hand;"));
        btnFrontOffice.setOnAction(e -> {
            System.out.println("Bouton FrontOffice cliqué");
            showFrontOffice();
        });

        actionsBox.getChildren().addAll(actionsTitle, btnBackOffice, btnFrontOffice);

        dashboard.getChildren().addAll(headerBox, statsBox, actionsBox);

        root.setCenter(dashboard);
    }

    private void showBackOffice() {
        try {
            System.out.println("Chargement du BackOffice...");
            BorderPane backOfficePane = new BorderPane();

            // Header avec bouton retour
            HBox header = new HBox(20);
            header.setAlignment(Pos.CENTER_LEFT);
            header.setPadding(new Insets(20));
            header.setStyle("-fx-background-color: linear-gradient(to right, #0B7A8F 0%, #0d97ad 100%);");

            Button btnBack = new Button("⬅ Retour au Dashboard");
            btnBack.setStyle(
                    "-fx-background-color: #D2691E; -fx-text-fill: white; " +
                            "-fx-font-size: 14px; -fx-font-weight: 600; " +
                            "-fx-background-radius: 8px; -fx-padding: 10 20; -fx-cursor: hand;");
            btnBack.setOnAction(e -> showDashboard());

            Label headerTitle = new Label("BackOffice - Administration");
            headerTitle.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            header.getChildren().addAll(btnBack, spacer, headerTitle);
            backOfficePane.setTop(header);

            // TabPane avec les CRUDs
            TabPane tabPane = new TabPane();
            tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

            // Hébergement and Reservation tabs removed

            backOfficePane.setCenter(tabPane);
            root.setCenter(backOfficePane);

            System.out.println("BackOffice chargé avec succès!");

        } catch (Exception e) {
            System.err.println("ERREUR lors du chargement du BackOffice:");
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible de charger le BackOffice");
            alert.setContentText("Erreur: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void showFrontOffice() {
        try {
            System.out.println("Chargement du FrontOffice...");
            BorderPane frontOfficePane = new BorderPane();

            // Header avec bouton retour
            HBox header = new HBox(20);
            header.setAlignment(Pos.CENTER_LEFT);
            header.setPadding(new Insets(20));
            header.setStyle("-fx-background-color: linear-gradient(135deg, #0B7A8F 0%, #14b8a6 100%);");

            Button btnBack = new Button("⬅ Retour au Dashboard");
            btnBack.setStyle(
                    "-fx-background-color: #D2691E; -fx-text-fill: white; " +
                            "-fx-font-size: 14px; -fx-font-weight: 600; " +
                            "-fx-background-radius: 8px; -fx-padding: 10 20; -fx-cursor: hand;");
            btnBack.setOnAction(e -> showDashboard());

            Label headerTitle = new Label("FrontOffice - Réservations");
            headerTitle.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            header.getChildren().addAll(btnBack, spacer, headerTitle);
            frontOfficePane.setTop(header);

            // TabPane pour Front
            TabPane tabPane = new TabPane();
            tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

            // Hébergement and Reservation tabs removed

            frontOfficePane.setCenter(tabPane);
            root.setCenter(frontOfficePane);

            System.out.println("FrontOffice chargé avec succès!");

        } catch (Exception e) {
            System.err.println("ERREUR lors du chargement du FrontOffice:");
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible de charger le FrontOffice");
            alert.setContentText("Erreur: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private VBox createStatCard(String icon, String title, String description) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(250);
        card.setPrefHeight(180);
        card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 12px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 15, 0, 0, 5); " +
                        "-fx-padding: 25px; -fx-cursor: hand;");

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 56px;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0B7A8F;");

        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");

        card.getChildren().addAll(iconLabel, titleLabel, descLabel);

        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 12px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(11, 122, 143, 0.3), 25, 0, 0, 10); " +
                        "-fx-padding: 25px; -fx-cursor: hand; -fx-translate-y: -5;"));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 12px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 15, 0, 0, 5); " +
                        "-fx-padding: 25px; -fx-cursor: hand;"));

        return card;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
