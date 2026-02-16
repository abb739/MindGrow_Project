package models;

public class Therapeute {
    private int id;
    private String nom, prenom, email, specialiteNom, photoProfil;
    private int specialiteId;
    private boolean estVerifie;

    public Therapeute(int id, String nom, String prenom, String email, int specialiteId, String specialiteNom, String photoProfil, boolean estVerifie) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.specialiteId = specialiteId;
        this.specialiteNom = specialiteNom;
        this.photoProfil = photoProfil;
        this.estVerifie = estVerifie;
    }

    // Getters
    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getEmail() { return email; }
    public int getSpecialiteId() { return specialiteId; }
    public String getSpecialiteNom() { return specialiteNom; }
    public String getPhotoProfil() { return photoProfil; }
    public boolean isEstVerifie() { return estVerifie; }
}