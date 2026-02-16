package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class HomePage extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Charge ton interface principale
            Parent root = FXMLLoader.load(getClass().getResource("/views/MainLayout.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setTitle("MindGrow Admin - DB: ayy");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            System.out.println("Erreur FXML : " + e.getMessage());
        }
    }
}