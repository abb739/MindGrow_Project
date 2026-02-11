package org.example;

import org.example.entities.Therapeute;
import org.example.services.ServiceTherapeute;
import org.example.utils.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- Démarrage du test MindGrow ---");

        // 1. Test de la connexion [cite: 192, 214]
        Connection connection = DataSource.getInstance().getConn();

        if (connection != null) {
            System.out.println("SUCCÈS : Connexion à MySQL opérationnelle !");
            System.out.println("-------------------------------------------");

            // 2. Initialisation du service [cite: 259]
            ServiceTherapeute st = new ServiceTherapeute();

            try {
                // 3. Test de l'insertion (CREATE) [cite: 260, 282]
                // On crée un objet thérapeute sans ID (car auto-incrémenté) [cite: 283]
                Therapeute nouveauCoach = new Therapeute(
                        "Dubois",
                        "Alice",
                        "TCC",
                        "Doctorat en Psychologie",
                        "alice_photo.jpg"
                );

                System.out.println("Tentative d'ajout du thérapeute : " + nouveauCoach.getNom());
                st.ajouter(nouveauCoach); // [cite: 260, 285]
                System.out.println("Ajout réussi !");

                // 4. Test de l'affichage (READ) [cite: 263, 286]
                System.out.println("\n--- Liste des thérapeutes en base ---");
                List<Therapeute> liste = st.afficherAll(); // [cite: 263, 287]
                for (Therapeute t : liste) {
                    System.out.println(t); // Utilise le toString() de l'entité
                }

                System.out.println("-------------------------------------------");
                System.out.println("Tests CRUD terminés avec succès !");

            } catch (SQLException e) {
                System.err.println("Erreur lors des opérations CRUD : " + e.getMessage()); //
            }

        } else {
            System.err.println("ERREUR : La connexion a échoué. Vérifiez XAMPP.");
        }
    }
}