package services;

import models.Therapeute;
import Utils.MyBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TherapeuteService implements InterfaceCRUD<Therapeute> {

    private Connection conn = MyBD.getInstance().getConn();

    @Override
    public void ajouter(Therapeute t) throws SQLException {
        String query = "INSERT INTO therapeutes (nom, prenom, email, specialite_id, photo_profil, est_verifie) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, t.getNom());
        ps.setString(2, t.getPrenom());
        ps.setString(3, t.getEmail());
        ps.setInt(4, t.getSpecialiteId());
        ps.setString(5, t.getPhotoProfil()); // FIXED: Matches your model getter
        ps.setBoolean(6, t.isEstVerifie());
        ps.executeUpdate();
    }

    @Override
    public void modifier(Therapeute t) throws SQLException {
        String query = "UPDATE therapeutes SET nom=?, prenom=?, email=?, specialite_id=?, photo_profil=?, est_verifie=? WHERE id=?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, t.getNom());
        ps.setString(2, t.getPrenom());
        ps.setString(3, t.getEmail());
        ps.setInt(4, t.getSpecialiteId());
        ps.setString(5, t.getPhotoProfil()); // FIXED: Matches your model getter
        ps.setBoolean(6, t.isEstVerifie());
        ps.setInt(7, t.getId());
        ps.executeUpdate();
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String query = "DELETE FROM therapeutes WHERE id=?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<Therapeute> afficher() throws SQLException {
        List<Therapeute> list = new ArrayList<>();
        // JOINTURE: Retrieves data from therapeutes (t) and the name from specialites (s)
        String query = "SELECT t.*, s.nom as spec_name FROM therapeutes t " +
                "LEFT JOIN specialites s ON t.specialite_id = s.id";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(query);
        // Dans TherapeuteService.java -> méthode afficher()
        while (rs.next()) {
            list.add(new Therapeute(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getInt("specialite_id"),
                    rs.getString("spec_name"), // <--- Vérifie que c'est bien 'spec_name' ici
                    rs.getString("photo_profil"),
                    rs.getBoolean("est_verifie")
            ));
        }
        return list;
    }
}