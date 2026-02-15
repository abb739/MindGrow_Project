package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HomePage extends Application {

    public static final int WIDTH = 900;   // largeur commune
    public static final int HEIGHT = 800;  // hauteur commune

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/new.fxml"));

            Scene scene = new Scene(root, WIDTH, HEIGHT); // taille fixe ici
            primaryStage.setScene(scene);
            primaryStage.setTitle("MindGrow - Accueil");
            primaryStage.setResizable(false); // empêche l'utilisateur de redimensionner
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
