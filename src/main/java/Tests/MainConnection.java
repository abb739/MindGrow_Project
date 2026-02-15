package Tests;

import Entities.Activity;
import Entities.Session;
import Services.ActivityCRUD;
import Services.SessionCRUD;
import Utils.MyBD;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class MainConnection {

    public static void main(String[] args) {

        // Test connexion BD
        MyBD myBD = MyBD.getInstance();

        // Création d'activités
        //Activity a1 = new Activity("Méditation", "Relaxation guidée", 45);
        //Activity a2 = new Activity("Yoga", "Souplesse et respiration", 60);

        // Création de sessions
        /*Session s1 = new Session(
                LocalDateTime.now().toString(),
                45,
                "Individuelle",
                "https://meet.google.com/aaa",
                "Planifiée",
                1, // idCoach
                2, // idMembre
                1  // idActivity
        );*/

        /*Session s2 = new Session(
                LocalDateTime.now().plusDays(1).toString(),
                60,
                "Groupe",
                "https://meet.google.com/bbb",
                "Planifiée",
                1,
                3,
                2
        );*/

        ActivityCRUD ac = new ActivityCRUD();
        SessionCRUD sc = new SessionCRUD();

        try {
            // Insertion test (décommente si nécessaire)
            // ac.ajouter(a1);
            // ac.ajouter(a2);
            // sc.ajouter(s1);
            // sc.ajouter(s2);

            // Affichage
            System.out.println("Liste des activités :");
            System.out.println(ac.afficher());

            System.out.println("Liste des sessions :");
            System.out.println(sc.afficher());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
