package services;

import entities.Paiement;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service pour gérer les opérations CRUD sur les paiements
 */
public class PaiementService {

    /**
     * CREATE - Ajouter un nouveau paiement
     */
    public boolean ajouterPaiement(Paiement paiement) {
        String query = "INSERT INTO paiement (abonnement_id, montant, date_paiement, mode_paiement, " +
                "transaction_id, statut, facture_url) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Générer un ID de transaction unique si non fourni
            if (paiement.getTransactionId() == null || paiement.getTransactionId().isEmpty()) {
                String txnId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                paiement.setTransactionId(txnId);
            }

            stmt.setInt(1, paiement.getAbonnementId());
            stmt.setDouble(2, paiement.getMontant());
            stmt.setTimestamp(3, Timestamp.valueOf(paiement.getDatePaiement()));
            stmt.setString(4, paiement.getModePaiement());
            stmt.setString(5, paiement.getTransactionId());
            stmt.setString(6, paiement.getStatut());
            stmt.setString(7, paiement.getFactureUrl());

            int result = stmt.executeUpdate();

            if (result > 0) {
                System.out.println("✅ Paiement enregistré! Transaction: " + paiement.getTransactionId());
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout du paiement!");
            e.printStackTrace();
        }

        return false;
    }

    /**
     * READ - Récupérer tous les paiements
     */
    public List<Paiement> getAllPaiements() {
        List<Paiement> paiements = new ArrayList<>();
        String query = "SELECT p.*, a.type as type_abonnement, u.nom, u.prenom " +
                "FROM paiement p " +
                "INNER JOIN abonnement a ON p.abonnement_id = a.id " +
                "INNER JOIN utilisateur u ON a.utilisateur_id = u.id " +
                "ORDER BY p.date_paiement DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Paiement p = new Paiement();
                p.setId(rs.getInt("id"));
                p.setAbonnementId(rs.getInt("abonnement_id"));
                p.setMontant(rs.getDouble("montant"));
                p.setDatePaiement(rs.getTimestamp("date_paiement").toLocalDateTime());
                p.setModePaiement(rs.getString("mode_paiement"));
                p.setTransactionId(rs.getString("transaction_id"));
                p.setStatut(rs.getString("statut"));
                p.setFactureUrl(rs.getString("facture_url"));
                p.setTypeAbonnement(rs.getString("type_abonnement"));
                p.setNomUtilisateur(rs.getString("nom") + " " + rs.getString("prenom"));

                paiements.add(p);
            }

            System.out.println("📖 " + paiements.size() + " paiement(s) récupéré(s).");

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des paiements!");
            e.printStackTrace();
        }

        return paiements;
    }

    /**
     * READ - Récupérer un paiement par ID
     */
    public Paiement getPaiementById(int id) {
        String query = "SELECT p.*, a.type as type_abonnement, u.nom, u.prenom " +
                "FROM paiement p " +
                "INNER JOIN abonnement a ON p.abonnement_id = a.id " +
                "INNER JOIN utilisateur u ON a.utilisateur_id = u.id " +
                "WHERE p.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Paiement p = new Paiement();
                p.setId(rs.getInt("id"));
                p.setAbonnementId(rs.getInt("abonnement_id"));
                p.setMontant(rs.getDouble("montant"));
                p.setDatePaiement(rs.getTimestamp("date_paiement").toLocalDateTime());
                p.setModePaiement(rs.getString("mode_paiement"));
                p.setTransactionId(rs.getString("transaction_id"));
                p.setStatut(rs.getString("statut"));
                p.setFactureUrl(rs.getString("facture_url"));
                p.setTypeAbonnement(rs.getString("type_abonnement"));
                p.setNomUtilisateur(rs.getString("nom") + " " + rs.getString("prenom"));

                return p;
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération du paiement #" + id);
            e.printStackTrace();
        }

        return null;
    }

    /**
     * UPDATE - Modifier un paiement existant
     */
    public boolean modifierPaiement(Paiement paiement) {
        String query = "UPDATE paiement SET abonnement_id=?, montant=?, date_paiement=?, " +
                "mode_paiement=?, statut=?, facture_url=? WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, paiement.getAbonnementId());
            stmt.setDouble(2, paiement.getMontant());
            stmt.setTimestamp(3, Timestamp.valueOf(paiement.getDatePaiement()));
            stmt.setString(4, paiement.getModePaiement());
            stmt.setString(5, paiement.getStatut());
            stmt.setString(6, paiement.getFactureUrl());
            stmt.setInt(7, paiement.getId());

            int result = stmt.executeUpdate();

            if (result > 0) {
                System.out.println("✅ Paiement #" + paiement.getId() + " modifié avec succès!");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification du paiement!");
            e.printStackTrace();
        }

        return false;
    }

    /**
     * DELETE - Supprimer un paiement
     */
    public boolean supprimerPaiement(int id) {
        String query = "DELETE FROM paiement WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            int result = stmt.executeUpdate();

            if (result > 0) {
                System.out.println("✅ Paiement #" + id + " supprimé avec succès!");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression du paiement!");
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Récupérer les paiements par abonnement
     */
    public List<Paiement> getPaiementsByAbonnement(int abonnementId) {
        List<Paiement> paiements = new ArrayList<>();
        String query = "SELECT * FROM paiement WHERE abonnement_id = ? ORDER BY date_paiement DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, abonnementId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Paiement p = new Paiement();
                p.setId(rs.getInt("id"));
                p.setAbonnementId(rs.getInt("abonnement_id"));
                p.setMontant(rs.getDouble("montant"));
                p.setDatePaiement(rs.getTimestamp("date_paiement").toLocalDateTime());
                p.setModePaiement(rs.getString("mode_paiement"));
                p.setTransactionId(rs.getString("transaction_id"));
                p.setStatut(rs.getString("statut"));
                p.setFactureUrl(rs.getString("facture_url"));

                paiements.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return paiements;
    }

    /**
     * STATISTIQUES - Total encaissé
     */
    public double getTotalEncaisse() {
        String query = "SELECT SUM(montant) as total FROM paiement WHERE statut = 'VALIDE'";

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
     * STATISTIQUES - Compter par statut
     */
    public int countByStatut(String statut) {
        String query = "SELECT COUNT(*) as total FROM paiement WHERE statut = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, statut);
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