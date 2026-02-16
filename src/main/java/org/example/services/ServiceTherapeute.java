package org.example.services;

import org.example.entities.Therapeute;
import org.example.utils.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceTherapeute {
    private Connection conn;

    public ServiceTherapeute() {
        conn = DataSource.getInstance().getConn();
    }

    // CREATE : Ajouter un thérapeute [cite: 260, 284]
    public void ajouter(Therapeute t) throws SQLException {
        String req = "INSERT INTO therapeute (nom, prenom, specialite, diplome, image) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(req);
        ps.setString(1, t.getNom());
        ps.setString(2, t.getPrenom());
        ps.setString(3, t.getSpecialite());
        ps.setString(4, t.getDiplome());
        ps.setString(5, t.getImage());
        ps.executeUpdate();
        System.out.println("Thérapeute ajouté avec succès !");
    }

    // READ : Récupérer tous les thérapeutes [cite: 263, 286]
    public List<Therapeute> afficherAll() throws SQLException {
        List<Therapeute> liste = new ArrayList<>();
        String req = "SELECT * FROM therapeute";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(req);
        while (rs.next()) {
            liste.add(new Therapeute(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("specialite"),
                    rs.getString("diplome"),
                    rs.getString("image")
            ));
        }
        return liste;
    }

    // UPDATE : Modifier les informations d'un thérapeute [cite: 288]
    public void modifier(Therapeute t) throws SQLException {
        String req = "UPDATE therapeute SET nom=?, prenom=?, specialite=?, diplome=?, image=? WHERE id=?";
        PreparedStatement ps = conn.prepareStatement(req);
        ps.setString(1, t.getNom());
        ps.setString(2, t.getPrenom());
        ps.setString(3, t.getSpecialite());
        ps.setString(4, t.getDiplome());
        ps.setString(5, t.getImage());
        ps.setInt(6, t.getId());
        ps.executeUpdate();
        System.out.println("Thérapeute modifié !");
    }

    // DELETE : Supprimer un thérapeute [cite: 262, 290]
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM therapeute WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(req);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("Thérapeute supprimé !");
    }
}