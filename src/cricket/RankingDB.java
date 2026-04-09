package cricket;
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RankingDB {

    public static List<RankingEntry> getRankings() {
        List<RankingEntry> list = new ArrayList<>();

        String sql =
                "SELECT TeamName, Matches, Points, Rating, CountryCode " +
                        "FROM TeamRanking " +
                        "ORDER BY Points DESC, Rating DESC";


        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            int pos = 1;
            while (rs.next()) {
                list.add(new RankingEntry(
                        pos++,
                        rs.getString("TeamName"),
                        rs.getInt("Matches"),
                        rs.getInt("Points"),
                        rs.getInt("Rating"),
                        rs.getString("CountryCode")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    public static void applyMatchResult(String winnerTeam, String loserTeam, boolean tied) {

        int winPts = 10;
        int tiePts = 5;

        String updateSql =
                "UPDATE TeamRanking " +
                        "SET Matches = Matches + 1, " +
                        "    Points  = Points + ?, " +
                        "    Wins    = Wins + ?, " +
                        "    Losses  = Losses + ?, " +
                        "    Ties    = Ties + ? " +
                        "WHERE TeamName = ?";

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            if (!teamExists(conn, winnerTeam) || !teamExists(conn, loserTeam)) {
                JOptionPane.showMessageDialog(
                        null,
                        "Ranking row missing for one of the teams.\n" +
                                "Please add both teams in TeamRanking first:\n" +
                                winnerTeam + " , " + loserTeam,
                        "Ranking Update Failed",
                        JOptionPane.ERROR_MESSAGE
                );
                conn.rollback();
                return;
            }

            if (tied) {
                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setInt(1, tiePts);
                    ps.setInt(2, 0);
                    ps.setInt(3, 0);
                    ps.setInt(4, 1);
                    ps.setString(5, winnerTeam);
                    ps.executeUpdate();

                    ps.setInt(1, tiePts);
                    ps.setInt(2, 0);
                    ps.setInt(3, 0);
                    ps.setInt(4, 1);
                    ps.setString(5, loserTeam);
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    // winner
                    ps.setInt(1, winPts);
                    ps.setInt(2, 1);
                    ps.setInt(3, 0);
                    ps.setInt(4, 0);
                    ps.setString(5, winnerTeam);
                    ps.executeUpdate();

                    // loser
                    ps.setInt(1, 0);
                    ps.setInt(2, 0);
                    ps.setInt(3, 1);
                    ps.setInt(4, 0);
                    ps.setString(5, loserTeam);
                    ps.executeUpdate();
                }
            }

            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean teamExists(Connection conn, String teamName) throws SQLException {
        String sql = "SELECT 1 FROM TeamRanking WHERE TeamName = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, teamName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

}
