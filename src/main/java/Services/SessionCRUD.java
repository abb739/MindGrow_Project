package Services;

import Entities.Session;
import Utils.MyBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SessionCRUD implements IntrefaceCRUD<Session> {

    Connection conn;

    public SessionCRUD() {
        conn = MyBD.getInstance().getConn();
    }

    @Override
    public void ajouter(Session session) throws SQLException {

        String req = "INSERT INTO seance (coach_id, titre, description, start_date, start_time, duree, type, lien_visio, statut) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement pst = conn.prepareStatement(req);

        pst.setInt(1, session.getIdCoach());
        pst.setString(2, session.getTitle());
        pst.setString(3, session.getDescription());
        pst.setDate(4, Date.valueOf(session.getStart_Session()));
        pst.setTime(5, Time.valueOf(session.getStart_Time()));
        pst.setInt(6, session.getDureeSession());
        pst.setString(7, session.getTypeSession().name());
        pst.setString(8, session.getLien());
        pst.setString(9, session.getStatus().name());

        pst.executeUpdate();
        System.out.println("Séance ajoutée !");
    }

    @Override
    public void modifier(Session session) throws SQLException {

        String req = "UPDATE seance SET coach_id=?, titre=?, description=?, start_date=?, start_time=?, duree=?, type=?, lien_visio=?, statut=? WHERE id=?";

        PreparedStatement pst = conn.prepareStatement(req);

        pst.setInt(1, session.getIdCoach());
        pst.setString(2, session.getTitle());
        pst.setString(3, session.getDescription());
        pst.setDate(4, Date.valueOf(session.getStart_Session()));
        pst.setTime(5, Time.valueOf(session.getStart_Time()));
        pst.setInt(6, session.getDureeSession());
        pst.setString(7, session.getTypeSession().name());
        pst.setString(8, session.getLien());
        pst.setString(9, session.getStatus().name());
        pst.setInt(10, session.getIdSession());

        pst.executeUpdate();
        System.out.println("Séance modifiée !");
    }

    @Override
    public void supprimer(int id) throws SQLException {

        String req = "DELETE FROM seance WHERE id=?";

        PreparedStatement pst = conn.prepareStatement(req);
        pst.setInt(1, id);
        pst.executeUpdate();

        System.out.println("Séance supprimée !");
    }

    @Override
    public List<Session> afficher() throws SQLException {

        String req = "SELECT * FROM seance";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(req);

        List<Session> listeSessions = new ArrayList<>();

        while (rs.next()) {

            Session s = new Session();

            s.setIdSession(rs.getInt("id"));
            s.setIdCoach(rs.getInt("coach_id"));
            s.setTitle(rs.getString("titre"));
            s.setDescription(rs.getString("description"));
            s.setStart_Session(rs.getDate("start_date").toLocalDate());
            s.setStart_Time(rs.getTime("start_time").toLocalTime());
            s.setDureeSession(rs.getInt("duree"));
            s.setTypeSession(Enum.valueOf(Entities.SessionType.class, rs.getString("type")));
            s.setLien(rs.getString("lien_visio"));
            s.setStatus(Enum.valueOf(Entities.SessionStatus.class, rs.getString("statut")));

            listeSessions.add(s);
        }

        return listeSessions;
    }
}
