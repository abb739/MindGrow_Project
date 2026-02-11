package org.example.entities;

public class Specialite {
    private int id;
    private String nomSpecialite;

    // Constructeur vide
    public Specialite() {}

    // Constructeur complet
    public Specialite(int id, String nomSpecialite) {
        this.id = id;
        this.nomSpecialite = nomSpecialite;
    }

    // Constructeur pour l'insertion
    public Specialite(String nomSpecialite) {
        this.nomSpecialite = nomSpecialite;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNomSpecialite() { return nomSpecialite; }
    public void setNomSpecialite(String nomSpecialite) { this.nomSpecialite = nomSpecialite; }

    // Indispensable pour l'affichage dans une ComboBox
    @Override
    public String toString() {
        return nomSpecialite;
    }
}