package cricket;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDB {

    public static List<String[]> getTopScorers(String teamName, int limit) {
        List<String[]> out = new ArrayList<>();

        String sql =
                "SELECT PlayerName, SUM(Runs) AS TotalRuns " +
                        "FROM match_batsman " +
                        "WHERE LOWER(TRIM(TeamName)) = LOWER(TRIM(?)) " +
                        "GROUP BY PlayerName " +
                        "ORDER BY TotalRuns DESC " +
                        "LIMIT ?";


        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, teamName);
            ps.setInt(2, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new String[]{
                            rs.getString("PlayerName"),
                            String.valueOf(rs.getInt("TotalRuns"))
                    });
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return out;
    }
    public static List<String[]> getTopWicketTakers(String teamName, int limit) {
        List<String[]> out = new ArrayList<>();

        String sql =
                "SELECT PlayerName, SUM(Wickets) AS TotalWickets " +
                        "FROM match_bowler " +
                        "WHERE LOWER(TRIM(TeamName)) = LOWER(TRIM(?)) " +
                        "GROUP BY PlayerName " +
                        "ORDER BY TotalWickets DESC " +
                        "LIMIT ?";


        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, teamName);
            ps.setInt(2, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new String[]{
                            rs.getString("PlayerName"),
                            String.valueOf(rs.getInt("TotalWickets"))
                    });
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return out;
    }
}
