package cricket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class PlayerDB {

    public static ArrayList<Player> getPlayersByTeamId(int teamId) {
        ArrayList<Player> list = new ArrayList<>();

        String sql = "CALL team_info(?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, teamId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Player(
                        rs.getInt("PlayerId"),
                        rs.getString("FullName"),
                        rs.getString("Role")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static void insertPlayer(int teamId, String fullName, String role) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO Player(TeamId, FullName, Role) VALUES(?,?,?)")) {

            ps.setInt(1, teamId);
            ps.setString(2, fullName);
            ps.setString(3, role);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Map<String, Map<String, ArrayList<String>>> loadPlayersGrouped() {

        Map<String, Map<String, ArrayList<String>>> result = new HashMap<>();

        String sql =
                "SELECT t.Name AS TeamName, p.FullName, p.Role " +
                        "FROM Player p " +
                        "JOIN Team t ON p.TeamId = t.TeamId " +
                        "ORDER BY t.Name, p.PlayerId";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String teamName   = rs.getString("TeamName");
                String fullName   = rs.getString("FullName");
                String role       = rs.getString("Role");   // e.g. "BAT", "BOWL", "AR"

                Map<String, ArrayList<String>> roleMap =
                        result.computeIfAbsent(teamName, k -> new HashMap<>());

                String key;
                if ("BOWL".equalsIgnoreCase(role)) key = "BOWL";
                else if ("BAT".equalsIgnoreCase(role)) key = "BAT";
                else if ("WK".equalsIgnoreCase(role)) key = "WK";
                else key = "AR";

                ArrayList<String> list =
                        roleMap.computeIfAbsent(key, k -> new ArrayList<>());

                list.add(fullName);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }
}
