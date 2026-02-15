package services;

import entities.Abonnement;
import utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service pour gérer les opérations CRUD sur les abonnements
 * MODIFIÉ : Ajout de la récupération du statut de paiement
 */
public class AbonnementService {

    /**
     * Générer un ID unique pour un nouvel abonnement
     * Cette méthode s'assure que l'ID généré n'existe pas déjà dans la base de données
     */
    private int genererIdUnique() {
        String query = "SELECT MAX(id) as maxId FROM abonnement";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                int maxId = rs.getInt("maxId");
                return maxId + 1; // Retourner le prochain ID disponible
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la génération de l'ID unique!");
            e.printStackTrace();
        }

        return 1; // Si aucun abonnement n'existe, commencer à 1
    }

    /**
     * Calculer la date d'expiration en fonction du type d'abonnement
     * @param dateDebut Date de début de l'abonnement
     * @param type Type d'abonnement (MENSUEL, TRIMESTRIEL, ANNUEL)
     * @return Date d'expiration calculée
     */
    private LocalDateTime calculerDateExpiration(LocalDateTime dateDebut, String type) {
        switch (type.toUpperCase()) {
            case "MENSUEL":
                return dateDebut.plusMonths(1);
            case "TRIMESTRIEL":
                return dateDebut.plusMonths(3);
            case "ANNUEL":
                return dateDebut.plusYears(1);
            default:
                throw new IllegalArgumentException("Type d'abonnement invalide: " + type +
                        ". Les types valides sont: MENSUEL, TRIMESTRIEL, ANNUEL");
        }
    }

    /**
     * CREATE - Ajouter un nouvel abonnement
     * L'ID est généré automatiquement
     * La date d'expiration est calculée automatiquement selon le type
     */
    public boolean ajouterAbonnement(Abonnement abonnement) {
        // Validation: pas de type GRATUIT
        if (abonnement.getType() != null && abonnement.getType().equalsIgnoreCase("GRATUIT")) {
            System.err.println("❌ Erreur: Le type d'abonnement GRATUIT n'est pas autorisé!");
            throw new IllegalArgumentException("Le type d'abonnement GRATUIT n'est pas autorisé. " +
                    "Les types valides sont: MENSUEL, TRIMESTRIEL, ANNUEL");
        }

        // Générer un ID unique
        int idUnique = genererIdUnique();
        abonnement.setId(idUnique);

        // Si la date de début n'est pas définie, utiliser la date actuelle
        if (abonnement.getDateDebut() == null) {
            abonnement.setDateDebut(LocalDateTime.now());
        }

        // Calculer automatiquement la date d'expiration selon le type
        LocalDateTime dateExpiration = calculerDateExpiration(
                abonnement.getDateDebut(),
                abonnement.getType()
        );
        abonnement.setDateExpiration(dateExpiration);

        String query = "INSERT INTO abonnement (id, utilisateur_id, type, montant, date_debut, date_expiration, statut) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, abonnement.getId());
            stmt.setInt(2, abonnement.getUtilisateurId());
            stmt.setString(3, abonnement.getType());
            stmt.setDouble(4, abonnement.getMontant());
            stmt.setTimestamp(5, Timestamp.valueOf(abonnement.getDateDebut()));
            stmt.setTimestamp(6, Timestamp.valueOf(abonnement.getDateExpiration()));
            stmt.setString(7, abonnement.getStatut());

            int result = stmt.executeUpdate();

            if (result > 0) {
                System.out.println("✅ Abonnement ajouté avec succès!");
                System.out.println("   ID généré: " + idUnique);
                System.out.println("   Type: " + abonnement.getType());
                System.out.println("   Date début: " + abonnement.getDateDebut());
                System.out.println("   Date expiration: " + abonnement.getDateExpiration());
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout de l'abonnement!");
            e.printStackTrace();
        }

        return false;
    }

    /**
     * READ - Récupérer tous les abonnements AVEC leur statut de paiement
     * MODIFIÉ : Ajout d'une jointure avec la table paiement pour récupérer le statut
     */
    public List<Abonnement> getAllAbonnements() {
        List<Abonnement> abonnements = new ArrayList<>();

        // Requête modifiée pour récupérer le statut de paiement
        // On prend le dernier paiement (le plus récent) pour chaque abonnement
        String query = "SELECT a.*, u.nom, u.prenom, u.email, " +
                "COALESCE(p.statut, 'AUCUN') as statut_paiement " +
                "FROM abonnement a " +
                "INNER JOIN utilisateur u ON a.utilisateur_id = u.id " +
                "LEFT JOIN (" +
                "    SELECT abonnement_id, statut, " +
                "    ROW_NUMBER() OVER (PARTITION BY abonnement_id ORDER BY date_paiement DESC) as rn " +
                "    FROM paiement" +
                ") p ON a.id = p.abonnement_id AND p.rn = 1 " +
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

                // NOUVEAU : Récupérer le statut de paiement
                ab.setStatutPaiement(rs.getString("statut_paiement"));

                abonnements.add(ab);
            }

            System.out.println("📖 " + abonnements.size() + " abonnement(s) récupéré(s) avec statut de paiement.");

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des abonnements!");
            e.printStackTrace();
        }

        return abonnements;
    }

    /**
     * READ - Récupérer un abonnement par ID AVEC son statut de paiement
     * MODIFIÉ : Ajout de la récupération du statut de paiement
     */
    public Abonnement getAbonnementById(int id) {
        String query = "SELECT a.*, u.nom, u.prenom, u.email, " +
                "COALESCE(p.statut, 'AUCUN') as statut_paiement " +
                "FROM abonnement a " +
                "INNER JOIN utilisateur u ON a.utilisateur_id = u.id " +
                "LEFT JOIN (" +
                "    SELECT abonnement_id, statut, " +
                "    ROW_NUMBER() OVER (PARTITION BY abonnement_id ORDER BY date_paiement DESC) as rn " +
                "    FROM paiement" +
                ") p ON a.id = p.abonnement_id AND p.rn = 1 " +
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

                // NOUVEAU : Récupérer le statut de paiement
                ab.setStatutPaiement(rs.getString("statut_paiement"));

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
     * Si le type d'abonnement est modifié, la date d'expiration est recalculée automatiquement
     */
    public boolean modifierAbonnement(Abonnement abonnement) {
        // Validation: pas de type GRATUIT
        if (abonnement.getType() != null && abonnement.getType().equalsIgnoreCase("GRATUIT")) {
            System.err.println("❌ Erreur: Le type d'abonnement GRATUIT n'est pas autorisé!");
            throw new IllegalArgumentException("Le type d'abonnement GRATUIT n'est pas autorisé. " +
                    "Les types valides sont: MENSUEL, TRIMESTRIEL, ANNUEL");
        }

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
        String query = "SELECT a.*, u.nom, u.prenom, u.email, " +
                "COALESCE(p.statut, 'AUCUN') as statut_paiement " +
                "FROM abonnement a " +
                "INNER JOIN utilisateur u ON a.utilisateur_id = u.id " +
                "LEFT JOIN (" +
                "    SELECT abonnement_id, statut, " +
                "    ROW_NUMBER() OVER (PARTITION BY abonnement_id ORDER BY date_paiement DESC) as rn " +
                "    FROM paiement" +
                ") p ON a.id = p.abonnement_id AND p.rn = 1 " +
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
                ab.setStatutPaiement(rs.getString("statut_paiement"));

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

    /**
     * Récupérer l'abonnement d'un utilisateur spécifique
     */
    public Abonnement getAbonnementByUserId(int utilisateurId) {
        String query = "SELECT a.*, u.nom, u.prenom, u.email, " +
                "COALESCE(p.statut, 'AUCUN') as statut_paiement " +
                "FROM abonnement a " +
                "INNER JOIN utilisateur u ON a.utilisateur_id = u.id " +
                "LEFT JOIN (" +
                "    SELECT abonnement_id, statut, " +
                "    ROW_NUMBER() OVER (PARTITION BY abonnement_id ORDER BY date_paiement DESC) as rn " +
                "    FROM paiement" +
                ") p ON a.id = p.abonnement_id AND p.rn = 1 " +
                "WHERE a.utilisateur_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, utilisateurId);
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
                ab.setStatutPaiement(rs.getString("statut_paiement"));

                return ab;
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération de l'abonnement utilisateur!");
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Créer ou mettre à jour l'abonnement d'un utilisateur
     * La durée est automatiquement calculée selon le type d'abonnement
     */
    public boolean upgraderAbonnement(int utilisateurId, String type, double montant) {
        // Validation: pas de type GRATUIT
        if (type != null && type.equalsIgnoreCase("GRATUIT")) {
            System.err.println("❌ Erreur: Le type d'abonnement GRATUIT n'est pas autorisé!");
            throw new IllegalArgumentException("Le type d'abonnement GRATUIT n'est pas autorisé. " +
                    "Les types valides sont: MENSUEL, TRIMESTRIEL, ANNUEL");
        }

        // Vérifier si l'utilisateur a déjà un abonnement
        Abonnement existant = getAbonnementByUserId(utilisateurId);

        LocalDateTime maintenant = LocalDateTime.now();
        LocalDateTime expiration = calculerDateExpiration(maintenant, type);

        if (existant != null) {
            // Mettre à jour l'abonnement existant
            existant.setType(type);
            existant.setMontant(montant);
            existant.setDateDebut(maintenant);
            existant.setDateExpiration(expiration);
            existant.setStatut("ACTIF");

            return modifierAbonnement(existant);
        } else {
            // Créer un nouvel abonnement
            Abonnement nouveau = new Abonnement();
            nouveau.setUtilisateurId(utilisateurId);
            nouveau.setType(type);
            nouveau.setMontant(montant);
            nouveau.setDateDebut(maintenant);
            nouveau.setDateExpiration(expiration);
            nouveau.setStatut("ACTIF");

            return ajouterAbonnement(nouveau);
        }
    }

    /**
     * Annuler l'abonnement d'un utilisateur
     */
    public boolean annulerAbonnement(int utilisateurId) {
        String query = "UPDATE abonnement SET statut = 'ANNULE' WHERE utilisateur_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, utilisateurId);
            int result = stmt.executeUpdate();

            if (result > 0) {
                System.out.println("✅ Abonnement annulé pour l'utilisateur #" + utilisateurId);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'annulation!");
            e.printStackTrace();
        }

        return false;
    }
}