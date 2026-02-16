package models;

public class Specialite {
    private int id;
    private String nom;
    private String description;

    public Specialite() {}

    public Specialite(int id, String nom, String description) {
        this.id = id;
        this.nom = nom;
        this.description = description;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return nom; // Très important pour que la ComboBox affiche le nom
    }
}