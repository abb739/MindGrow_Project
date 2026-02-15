package services;

import entities.Utilisateur;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service pour gérer les utilisateurs
 */
public class UtilisateurService {

    /**
     * Authentifier un utilisateur
     */
    public Utilisateur login(String email, String motDePasse) {
        String query = "SELECT * FROM utilisateur WHERE email = ? AND mot_de_passe = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, motDePasse);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Utilisateur user = new Utilisateur();
                user.setId(rs.getInt("id"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setEmail(rs.getString("email"));
                user.setMotDePasse(rs.getString("mot_de_passe"));
                user.setTelephone(rs.getString("telephone"));

                Date dateNaissance = rs.getDate("date_naissance");
                if (dateNaissance != null) {
                    user.setDateNaissance(dateNaissance.toLocalDate());
                }

                Timestamp dateInscription = rs.getTimestamp("date_inscription");
                if (dateInscription != null) {
                    user.setDateInscription(dateInscription.toLocalDateTime());
                }

                user.setRoles(rs.getString("roles"));

                return user;
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'authentification!");
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Récupérer un utilisateur par ID
     */
    public Utilisateur getUtilisateurById(int id) {
        String query = "SELECT * FROM utilisateur WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Utilisateur user = new Utilisateur();
                user.setId(rs.getInt("id"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setEmail(rs.getString("email"));
                user.setTelephone(rs.getString("telephone"));
                user.setRoles(rs.getString("roles"));

                return user;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Récupérer tous les utilisateurs
     */
    public List<Utilisateur> getAllUtilisateurs() {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String query = "SELECT * FROM utilisateur ORDER BY date_inscription DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Utilisateur user = new Utilisateur();
                user.setId(rs.getInt("id"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setEmail(rs.getString("email"));
                user.setTelephone(rs.getString("telephone"));
                user.setRoles(rs.getString("roles"));

                utilisateurs.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return utilisateurs;
    }
}