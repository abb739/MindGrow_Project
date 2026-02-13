package org.example.models;

/**
 * User model representing a row in the 'utilisateur' table.
 * Includes fields for password reset functionality.
 */
public class User {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String roles;
    private String telephone;
    private String dateNaissance;
    private String status;          // "Active" or "Inactive"
    private String resetToken;
    private String resetTokenExpiry;

    public User() {
        this.status = "Active";
    }

    public User(String nom, String prenom, String email, String motDePasse, String roles, String telephone, String dateNaissance) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.roles = roles;
        this.telephone = telephone;
        this.dateNaissance = dateNaissance;
        this.status = "Active";
    }

    public User(int id, String nom, String prenom, String email, String motDePasse, String roles, String telephone, String dateNaissance) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.roles = roles;
        this.telephone = telephone;
        this.dateNaissance = dateNaissance;
        this.status = "Active";
    }

    // ===== Getters & Setters =====

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

    public String getRoles() { return roles; }
    public void setRoles(String roles) { this.roles = roles; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(String dateNaissance) { this.dateNaissance = dateNaissance; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }

    public String getResetTokenExpiry() { return resetTokenExpiry; }
    public void setResetTokenExpiry(String resetTokenExpiry) { this.resetTokenExpiry = resetTokenExpiry; }

    /**
     * Returns the user's initials (first letter of nom + first letter of prenom).
     */
    public String getInitials() {
        String initial1 = (prenom != null && !prenom.isEmpty()) ? prenom.substring(0, 1).toUpperCase() : "";
        String initial2 = (nom != null && !nom.isEmpty()) ? nom.substring(0, 1).toUpperCase() : "";
        return initial1 + initial2;
    }

    /**
     * Returns the full name: prenom + nom.
     */
    public String getFullName() {
        return (prenom != null ? prenom : "") + " " + (nom != null ? nom : "");
    }

    /**
     * Returns a clean role name without JSON brackets.
     */
    public String getCleanRole() {
        if (roles == null) return "Unknown";
        String clean = roles.replaceAll("[\\[\\]\"']", "").trim();
        return switch (clean) {
            case "ROLE_ADMIN" -> "Admin";
            case "ROLE_MEMBRE" -> "Patient";
            case "ROLE_COACH" -> "Doctor";
            default -> clean;
        };
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", roles='" + roles + '\'' +
                ", status='" + status + '\'' +
                ", telephone='" + telephone + '\'' +
                ", dateNaissance='" + dateNaissance + '\'' +
                '}';
    }
}
