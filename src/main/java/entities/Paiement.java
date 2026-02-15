package entities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Entité représentant un paiement
 */
public class Paiement {

    // Attributs principaux
    private int id;
    private int abonnementId;
    private double montant;
    private LocalDateTime datePaiement;
    private String modePaiement;
    private String transactionId;
    private String statut;
    private String factureUrl;

    // Attributs additionnels pour l'affichage
    private String typeAbonnement;
    private String nomUtilisateur;

    // Constructeur vide
    public Paiement() {}

    // Constructeur complet
    public Paiement(int id, int abonnementId, double montant, LocalDateTime datePaiement,
                    String modePaiement, String transactionId, String statut, String factureUrl) {
        this.id = id;
        this.abonnementId = abonnementId;
        this.montant = montant;
        this.datePaiement = datePaiement;
        this.modePaiement = modePaiement;
        this.transactionId = transactionId;
        this.statut = statut;
        this.factureUrl = factureUrl;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAbonnementId() {
        return abonnementId;
    }

    public void setAbonnementId(int abonnementId) {
        this.abonnementId = abonnementId;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public LocalDateTime getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDateTime datePaiement) {
        this.datePaiement = datePaiement;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getFactureUrl() {
        return factureUrl;
    }

    public void setFactureUrl(String factureUrl) {
        this.factureUrl = factureUrl;
    }

    public String getTypeAbonnement() {
        return typeAbonnement;
    }

    public void setTypeAbonnement(String typeAbonnement) {
        this.typeAbonnement = typeAbonnement;
    }

    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public void setNomUtilisateur(String nomUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
    }

    // Méthode utilitaire pour formater la date
    public String getDatePaiementFormatted() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return datePaiement != null ? datePaiement.format(formatter) : "";
    }

    @Override
    public String toString() {
        return "Paiement{" +
                "id=" + id +
                ", montant=" + montant +
                ", transactionId='" + transactionId + '\'' +
                ", statut='" + statut + '\'' +
                '}';
    }
}