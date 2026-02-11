package org.example.entities;

public class Therapeute {
    private int id;
    private String nom, prenom, specialite, diplome, image;

    public Therapeute() {}

    public Therapeute(int id, String nom, String prenom, String specialite, String diplome, String image) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.specialite = specialite;
        this.diplome = diplome;
        this.image = image;
    }

    public Therapeute(String nom, String prenom, String specialite, String diplome, String image) {
        this.nom = nom;
        this.prenom = prenom;
        this.specialite = specialite;
        this.diplome = diplome;
        this.image = image;
    }

    // Getters et Setters nécessaires pour le TableView
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }
    public String getDiplome() { return diplome; }
    public void setDiplome(String diplome) { this.diplome = diplome; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}