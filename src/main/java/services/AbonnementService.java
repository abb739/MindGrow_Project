package services;

import entities.Abonnement;
import utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service pour gérer les opérations CRUD sur les abonnements
 */
public class AbonnementService {

    /**
     * CREATE - Ajouter un nouvel abonnement
     */
    public boolean ajouterAbonnement(Abonnement abonnement) {
        String query = "INSERT INTO abonnement (utilisateur_id, type, montant, date_debut, date_expiration, statut) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, abonnement.getUtilisateurId());
            stmt.setString(2, abonnement.getType());
            stmt.setDouble(3, abonnement.getMontant());
            stmt.setTimestamp(4, Timestamp.valueOf(abonnement.getDateDebut()));
            stmt.setTimestamp(5, Timestamp.valueOf(abonnement.getDateExpiration()));
            stmt.setString(6, abonnement.getStatut());

            int result = stmt.executeUpdate();

            if (result > 0) {
                System.out.println("✅ Abonnement ajouté avec succès!");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout de l'abonnement!");
            e.printStackTrace();
        }

        return false;
    }

    /**
     * READ - Récupérer tous les abonnements
     */
    public List<Abonnement> getAllAbonnements() {
        List<Abonnement> abonnements = new ArrayList<>();
        String query = "SELECT a.*, u.nom, u.prenom, u.email " +
                "FROM abonnement a " +
                "INNER JOIN utilisateur u ON a.utilisateur_id = u.id " +
                "ORDER BY a.date_debut DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Abonnement ab = new Abonnement();
                ab.setId(rs.getInt("id"));
                ab.setUtilisateurId(rs.getInt("utilisateur_id"));
                ab.setType(rs.getString("type"));
                ab.setMontant(rs.getDouble("montant"));
                ab.setDateDebut(rs.getTimestamp("date_debut").toLocalDateTime());
                ab.setDateExpiration(rs.getTimestamp("date_expiration").toLocalDateTime());
                ab.setStatut(rs.getString("statut"));
                ab.setNomUtilisateur(rs.getString("nom"));
                ab.setPrenomUtilisateur(rs.getString("prenom"));
                ab.setEmailUtilisateur(rs.getString("email"));

                abonnements.add(ab);
            }

            System.out.println("📖 " + abonnements.size() + " abonnement(s) récupéré(s).");

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des abonnements!");
            e.printStackTrace();
        }

        return abonnements;
    }

    /**
     * READ - Récupérer un abonnement par ID
     */
    public Abonnement getAbonnementById(int id) {
        String query = "SELECT a.*, u.nom, u.prenom, u.email " +
                "FROM abonnement a " +
                "INNER JOIN utilisateur u ON a.utilisateur_id = u.id " +
                "WHERE a.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Abonnement ab = new Abonnement();
                ab.setId(rs.getInt("id"));
                ab.setUtilisateurId(rs.getInt("utilisateur_id"));
                ab.setType(rs.getString("type"));
                ab.setMontant(rs.getDouble("montant"));
                ab.setDateDebut(rs.getTimestamp("date_debut").toLocalDateTime());
                ab.setDateExpiration(rs.getTimestamp("date_expiration").toLocalDateTime());
                ab.setStatut(rs.getString("statut"));
                ab.setNomUtilisateur(rs.getString("nom"));
                ab.setPrenomUtilisateur(rs.getString("prenom"));
                ab.setEmailUtilisateur(rs.getString("email"));

                return ab;
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération de l'abonnement #" + id);
            e.printStackTrace();
        }

        return null;
    }

    /**
     * UPDATE - Modifier un abonnement existant
     */
    public boolean modifierAbonnement(Abonnement abonnement) {
        String query = "UPDATE abonnement SET utilisateur_id=?, type=?, montant=?, " +
                "date_debut=?, date_expiration=?, statut=? WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, abonnement.getUtilisateurId());
            stmt.setString(2, abonnement.getType());
            stmt.setDouble(3, abonnement.getMontant());
            stmt.setTimestamp(4, Timestamp.valueOf(abonnement.getDateDebut()));
            stmt.setTimestamp(5, Timestamp.valueOf(abonnement.getDateExpiration()));
            stmt.setString(6, abonnement.getStatut());
            stmt.setInt(7, abonnement.getId());

            int result = stmt.executeUpdate();

            if (result > 0) {
                System.out.println("✅ Abonnement #" + abonnement.getId() + " modifié avec succès!");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification de l'abonnement!");
            e.printStackTrace();
        }

        return false;
    }

    /**
     * DELETE - Supprimer un abonnement
     */
    public boolean supprimerAbonnement(int id) {
        String query = "DELETE FROM abonnement WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            int result = stmt.executeUpdate();

            if (result > 0) {
                System.out.println("✅ Abonnement #" + id + " supprimé avec succès!");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de l'abonnement!");
            e.printStackTrace();
        }

        return false;
    }

    /**
     * STATISTIQUES - Récupérer les abonnements actifs
     */
    public List<Abonnement> getAbonnementsActifs() {
        List<Abonnement> abonnements = new ArrayList<>();
        String query = "SELECT a.*, u.nom, u.prenom, u.email " +
                "FROM abonnement a " +
                "INNER JOIN utilisateur u ON a.utilisateur_id = u.id " +
                "WHERE a.statut = 'ACTIF' AND a.date_expiration > NOW() " +
                "ORDER BY a.date_expiration ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Abonnement ab = new Abonnement();
                ab.setId(rs.getInt("id"));
                ab.setUtilisateurId(rs.getInt("utilisateur_id"));
                ab.setType(rs.getString("type"));
                ab.setMontant(rs.getDouble("montant"));
                ab.setDateDebut(rs.getTimestamp("date_debut").toLocalDateTime());
                ab.setDateExpiration(rs.getTimestamp("date_expiration").toLocalDateTime());
                ab.setStatut(rs.getString("statut"));
                ab.setNomUtilisateur(rs.getString("nom"));
                ab.setPrenomUtilisateur(rs.getString("prenom"));
                ab.setEmailUtilisateur(rs.getString("email"));

                abonnements.add(ab);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return abonnements;
    }

    /**
     * STATISTIQUES - Calculer le revenu total
     */
    public double getRevenuTotal() {
        String query = "SELECT SUM(montant) as total FROM abonnement WHERE statut = 'ACTIF'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getDouble("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    /**
     * STATISTIQUES - Compter par type
     */
    public int countByType(String type) {
        String query = "SELECT COUNT(*) as total FROM abonnement WHERE type = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, type);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
