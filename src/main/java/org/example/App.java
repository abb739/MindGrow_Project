package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Classe principale pour lancer l'application JavaFX MindGrow[cite: 54].
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Chargement du fichier FXML depuis le dossier resources [cite: 83]
            Parent root = FXMLLoader.load(getClass().getResource("/therapeute_view.fxml"));

            // Configuration de la fenêtre principale (Stage) [cite: 14, 16]
            primaryStage.setTitle("MindGrow - Gestion Thérapeutes");

            // Création de la scène avec le contenu du FXML [cite: 14]
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);

            // Affichage de la fenêtre [cite: 104]
            primaryStage.show();

        } catch (IOException e) {
            // Gestion des erreurs de chargement de l'interface
            System.err.println("Erreur critique : Impossible de charger le fichier FXML.");
            System.err.println("Message : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Lance le cycle de vie de l'application JavaFX
        launch(args);
    }
}