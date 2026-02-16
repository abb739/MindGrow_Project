package entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entité représentant un utilisateur
 */
public class Utilisateur {

    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String telephone;
    private LocalDate dateNaissance;
    private LocalDateTime dateInscription;
    private String roles; // JSON format: ["ROLE_ADMIN"] ou ["ROLE_MEMBRE"]

    // Constructeur vide
    public Utilisateur() {}

    // Constructeur complet
    public Utilisateur(int id, String nom, String prenom, String email, String motDePasse,
                       String telephone, LocalDate dateNaissance, LocalDateTime dateInscription, String roles) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.telephone = telephone;
        this.dateNaissance = dateNaissance;
        this.dateInscription = dateInscription;
        this.roles = roles;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public LocalDateTime getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDateTime dateInscription) { this.dateInscription = dateInscription; }

    public String getRoles() { return roles; }
    public void setRoles(String roles) { this.roles = roles; }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", roles='" + roles + '\'' +
                '}';
    }
}