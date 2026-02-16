package controllers;

import models.Therapeute;
import services.TherapeuteService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TherapeuteController implements Initializable {

    @FXML private GridPane cardContainer;
    private TherapeuteService service = new TherapeuteService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshGrid();
    }


    public void refreshGrid() {
        cardContainer.getChildren().clear();
        try {
            List<Therapeute> list = service.afficher();
            System.out.println("DEBUG: Nombre de thérapeutes trouvés en DB : " + list.size()); // Témoin 1

            int column = 0;
            int row = 1;
            for (Therapeute t : list) {
                System.out.println("DEBUG: Chargement de la carte pour : " + t.getNom()); // Témoin 2

                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/views/CardTherapeute.fxml"));
                VBox cardBox = fxmlLoader.load();

                CardController cardController = fxmlLoader.getController();

                if (cardController == null) {
                    System.out.println("ERREUR: Le contrôleur de la carte est NULL !"); // Témoin 3
                } else {
                    cardController.setData(t, this);
                }

                if (column == 3) { column = 0; row++; }
                cardContainer.add(cardBox, column++, row);
                GridPane.setMargin(cardBox, new Insets(10));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleAddExpertClick() {
        try {
            // On récupère le controller principal via la scène
            MainLayoutController main = (MainLayoutController) cardContainer.getScene().getUserData();
            if (main != null) {
                main.handleAddTherapeutePage();
            } else {
                System.err.println("Erreur : MainLayoutController introuvable dans UserData");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}