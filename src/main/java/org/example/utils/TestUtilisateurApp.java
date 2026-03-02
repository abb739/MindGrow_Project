package org.example.utils;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class TestUtilisateurApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Chargement de l'interface d'inscription (Sign Up) en premier (ou SignIn si
        // vous préférez)
        // System.out.println(getClass().getResource("/Frontoffice/SignIn.fxml"));
        URL resource = getClass().getResource("/Frontoffice/SignIn.fxml");
        if (resource == null) {
            System.err.println(
                    "Fichier fxml introuvable. Vérifiez que SignIn.fxml est bien dans src/main/resources/Frontoffice/");
            return;
        }
        Parent root = FXMLLoader.load(resource);

        primaryStage.setTitle("Plateforme MindGrow - Connexion / Inscription");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
