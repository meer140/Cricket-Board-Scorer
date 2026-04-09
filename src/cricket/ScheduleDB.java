package cricket;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScheduleDB {

    public static List<Schedule> loadSchedule() {
        List<Schedule> list = new ArrayList<>();

        String sql =
                "SELECT ScheduleId, TeamA, TeamACode, TeamB, TeamBCode, " +
                        "MatchDate, MatchTime, Venue, Format, IsPlayed " +
                        "FROM Schedule " +
                        "ORDER BY MatchDate, MatchTime";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Schedule m = new Schedule(
                        rs.getString("TeamA"),
                        rs.getString("TeamACode"),
                        rs.getString("TeamB"),
                        rs.getString("TeamBCode"),
                        rs.getString("MatchDate"),
                        rs.getString("MatchTime"),
                        rs.getString("Venue"),
                        rs.getString("Format")
                );

                m.setScheduleId(rs.getInt("ScheduleId"));
                try {
                    m.setPlayed(rs.getInt("IsPlayed") == 1);
                } catch (SQLException ignored) {
                    m.setPlayed(false);
                }

                list.add(m);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    public static int addMatch(Schedule s) {
        String sql =
                "INSERT INTO Schedule " +
                        "(TeamA, TeamACode, TeamB, TeamBCode, MatchDate, MatchTime, Venue, Format, IsPlayed) " +
                        "VALUES (?,?,?,?,?,?,?,?,0)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, s.teamA);
            ps.setString(2, s.codeA);
            ps.setString(3, s.teamB);
            ps.setString(4, s.codeB);
            ps.setString(5, s.date);
            ps.setString(6, s.time);
            ps.setString(7, s.venue);
            ps.setString(8, s.format);

            int affected = ps.executeUpdate();
            if (affected == 0) return -1;

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    s.setScheduleId(id);
                    s.setPlayed(false);
                    return id;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public static void markPlayed(int scheduleId) {
        String sql = "UPDATE Schedule SET IsPlayed = 1 WHERE ScheduleId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, scheduleId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void markUnplayed(int scheduleId) {
        String sql = "UPDATE Schedule SET IsPlayed = 0 WHERE ScheduleId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, scheduleId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
