package entities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Entité représentant un abonnement utilisateur
 */
public class Abonnement {

    // Attributs principaux
    private int id;
    private int utilisateurId;
    private String type;
    private double montant;
    private LocalDateTime dateDebut;
    private LocalDateTime dateExpiration;
    private String statut;

    // Attributs additionnels pour l'affichage
    private String nomUtilisateur;
    private String prenomUtilisateur;
    private String emailUtilisateur;

    // NOUVEAU : Statut de paiement
    private String statutPaiement; // VALIDE, EN_ATTENTE, ECHOUE, REMBOURSE

    // Constructeur vide
    public Abonnement() {}

    // Constructeur complet
    public Abonnement(int id, int utilisateurId, String type, double montant,
                      LocalDateTime dateDebut, LocalDateTime dateExpiration, String statut) {
        this.id = id;
        this.utilisateurId = utilisateurId;
        this.type = type;
        this.montant = montant;
        this.dateDebut = dateDebut;
        this.dateExpiration = dateExpiration;
        this.statut = statut;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(int utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(LocalDateTime dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public void setNomUtilisateur(String nomUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
    }

    public String getPrenomUtilisateur() {
        return prenomUtilisateur;
    }

    public void setPrenomUtilisateur(String prenomUtilisateur) {
        this.prenomUtilisateur = prenomUtilisateur;
    }

    public String getEmailUtilisateur() {
        return emailUtilisateur;
    }

    public void setEmailUtilisateur(String emailUtilisateur) {
        this.emailUtilisateur = emailUtilisateur;
    }

    // NOUVEAU : Getter et Setter pour le statut de paiement
    public String getStatutPaiement() {
        return statutPaiement;
    }

    public void setStatutPaiement(String statutPaiement) {
        this.statutPaiement = statutPaiement;
    }

    // Méthode utilitaire pour formater les dates
    public String getDateDebutFormatted() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dateDebut != null ? dateDebut.format(formatter) : "";
    }

    public String getDateExpirationFormatted() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dateExpiration != null ? dateExpiration.format(formatter) : "";
    }

    @Override
    public String toString() {
        return "Abonnement{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", montant=" + montant +
                ", statut='" + statut + '\'' +
                ", statutPaiement='" + statutPaiement + '\'' +
                '}';
    }
}