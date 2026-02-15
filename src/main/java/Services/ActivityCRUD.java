package Services;

import Entities.Activity;
import Utils.MyBD;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ActivityCRUD implements IntrefaceCRUD<Activity> {

    private Connection conn;

    public ActivityCRUD() {
        conn = MyBD.getInstance().getConn();
    }

    @Override
    public void ajouter(Activity activity) throws SQLException {
        String req = "INSERT INTO activity (type, description, duree, start_time, session_id) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pst = conn.prepareStatement(req);
        pst.setString(1, activity.getType().toString()); // <-- enum converti en String
        pst.setString(2, activity.getDescription());
        pst.setInt(3, activity.getDuree());
        pst.setTime(4, Time.valueOf(activity.getStartTime()));
        pst.setInt(5, activity.getSessionId());
        pst.executeUpdate();
        System.out.println("Activity ajoutée !");
    }

    @Override
    public void modifier(Activity activity) throws SQLException {
        String req = "UPDATE activity SET type=?, description=?, duree=?, start_time=?, session_id=? WHERE id=?";
        PreparedStatement pst = conn.prepareStatement(req);
        pst.setString(1, activity.getType().toString()); // <-- enum converti en String
        pst.setString(2, activity.getDescription());
        pst.setInt(3, activity.getDuree());
        pst.setTime(4, Time.valueOf(activity.getStartTime()));
        pst.setInt(5, activity.getSessionId());
        pst.setInt(6, activity.getIdActivity());
        pst.executeUpdate();
        System.out.println("Activity modifiée !");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM activity WHERE id=?";
        PreparedStatement pst = conn.prepareStatement(req);
        pst.setInt(1, id);
        pst.executeUpdate();
        System.out.println("Activity supprimée !");
    }

    @Override
    public List<Activity> afficher() throws SQLException {
        String req = "SELECT * FROM activity";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(req);

        List<Activity> listeActivities = new ArrayList<>();
        while (rs.next()) {
            Activity a = new Activity();
            a.setIdActivity(rs.getInt("id"));
            a.setType(Entities.ActivityType.valueOf(rs.getString("type"))); // <-- convertir String en enum
            a.setDescription(rs.getString("description"));
            a.setDuree(rs.getInt("duree"));
            a.setStartTime(rs.getTime("start_time").toLocalTime());
            a.setSessionId(rs.getInt("session_id"));
            listeActivities.add(a);
        }
        return listeActivities;
    }


    public boolean existsSameTime(int sessionId, LocalTime startTime, Integer excludeId) throws SQLException {
        String req = "SELECT COUNT(*) FROM activity WHERE session_id=? AND start_time=?";
        if (excludeId != null) req += " AND id<>?";
        PreparedStatement pst = conn.prepareStatement(req);
        pst.setInt(1, sessionId);
        pst.setTime(2, Time.valueOf(startTime));
        if (excludeId != null) pst.setInt(3, excludeId);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) return rs.getInt(1) > 0;
        return false;
    }
}
