package cricket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class TeamDB {

    public static List<Team> getAllTeams() {
        List<Team> teams = new ArrayList<>();

        String sql = "SELECT TeamId, Name, CountryCode FROM Team ORDER BY Name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Team t = new Team(
                        rs.getInt("TeamId"),
                        rs.getString("Name"),
                        rs.getString("CountryCode")
                );
                teams.add(t);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return teams;
    }

    public static int getTeamIdByName(String name) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT TeamId FROM Team WHERE Name = ?")) {

            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return rs.getInt("TeamId");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getTeamCode(String name) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT CountryCode FROM Team WHERE Name = ?")) {

            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return rs.getString("CountryCode");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int insertTeam(String name, String code) {
        String sql = "INSERT INTO Team(Name, CountryCode) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setString(2, code);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getCodeByTeamName(String teamName) {
        String sql = "SELECT CountryCode FROM Team WHERE Name = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, teamName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("CountryCode");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";   // not found
    }
    public static Map<String, Team> loadTeamsByName() {

        Map<String, Team> map = new HashMap<>();

        String sql = "SELECT TeamId, Name, CountryCode FROM Team";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id          = rs.getInt("TeamId");
                String name     = rs.getString("Name");
                String code     = rs.getString("CountryCode"); // "in", "pk", etc.

                Team t = new Team(id,name, code);
                // If your Team class has teamId field, set it here:
                // t.teamId = id;  // or t.setTeamId(id);

                map.put(name, t);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return map;
    }
    public static List<String> getAllTeamNames() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT Name FROM Team ORDER BY Name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString("Name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    // small DTO
    public static class PlayerRow {
        public int playerId;
        public int teamId;
        public String name;
        public String role;

        public PlayerRow(int playerId, int teamId, String name, String role) {
            this.playerId = playerId;
            this.teamId = teamId;
            this.name = name;
            this.role = role;
        }
    }

    public static List<PlayerRow> getPlayersByTeamId(int teamId) {
        List<PlayerRow> list = new ArrayList<>();
        String sql = "SELECT PlayerId, TeamId, FullName, Role FROM Player WHERE TeamId = ? ORDER BY PlayerId";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, teamId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new PlayerRow(
                            rs.getInt("PlayerId"),
                            rs.getInt("TeamId"),
                            rs.getString("FullName"),
                            rs.getString("Role")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void deletePlayersByTeamId(int teamId) {
        String sql = "DELETE FROM Player WHERE TeamId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teamId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertPlayer(int teamId, String name, String role) {
        String sql = "INSERT INTO Player(TeamId, Name, Role) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teamId);
            ps.setString(2, name);
            ps.setString(3, role);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static boolean deleteTeamById(int teamId) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // 1) delete players
            try (PreparedStatement ps1 = conn.prepareStatement("DELETE FROM Player WHERE TeamId = ?")) {
                ps1.setInt(1, teamId);
                ps1.executeUpdate();
            }

            // 2) delete team
            try (PreparedStatement ps2 = conn.prepareStatement("DELETE FROM Team WHERE TeamId = ?")) {
                ps2.setInt(1, teamId);
                int rows = ps2.executeUpdate();
                conn.commit();
                return rows > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean teamUsedInScheduleOrMatches(String teamName) {
        String sql = "SELECT COUNT(*) FROM Schedule WHERE TeamA = ? OR TeamB = ?"; // adjust
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, teamName);
            ps.setString(2, teamName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true; // be safe
    }

}
