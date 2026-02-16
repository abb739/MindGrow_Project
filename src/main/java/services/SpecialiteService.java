package services;

import models.Specialite;
import Utils.MyBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecialiteService implements InterfaceCRUD<Specialite> {

    private Connection conn = MyBD.getInstance().getConn();

    @Override
    public void ajouter(Specialite s) throws SQLException {
        String query = "INSERT INTO specialites (nom, description) VALUES (?, ?)";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, s.getNom());
        ps.setString(2, s.getDescription());
        ps.executeUpdate();
        System.out.println("Spécialité ajoutée avec succès !");
    }

    @Override
    public void modifier(Specialite s) throws SQLException {
        String query = "UPDATE specialites SET nom = ?, description = ? WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, s.getNom());
        ps.setString(2, s.getDescription());
        ps.setInt(3, s.getId());
        ps.executeUpdate();
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String query = "DELETE FROM specialites WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<Specialite> afficher() throws SQLException {
        List<Specialite> specialites = new ArrayList<>();
        String query = "SELECT * FROM specialites";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(query);

        while (rs.next()) {
            Specialite s = new Specialite(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("description")
            );
            specialites.add(s);
        }
        return specialites;
    }
}