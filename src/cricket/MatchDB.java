package cricket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatchDB {

    public static int saveCompletedMatch(Match m) {

        int matchId = -1;

        String insertMatch =
                "INSERT INTO matches (TeamAName, TeamBName, ScoreA, ScoreB, ResultText) VALUES (?,?,?,?,?)";

        String insertBat =
                "INSERT INTO match_batsman" +
                        "(MatchId, InningsNo, TeamName, PlayerName, Runs, Balls, Fours, Sixes, Dismissal)"+
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";


        String insertBowl =
                "INSERT INTO match_bowler " +
                        "(MatchId, InningsNo, TeamName, PlayerName, Overs, Runs, Wickets,Economy)"+
                        "VALUES (?, ?, ?, ?, ?, ?, ?,?)";

        try (Connection conn = DBConnection.getConnection()) {

            try (PreparedStatement ps = conn.prepareStatement(insertMatch, Statement.RETURN_GENERATED_KEYS)) {

                String scoreA = (m.totalA != null && !m.totalA.isEmpty()) ? m.totalA : m.scoreA;
                String scoreB = (m.totalB != null && !m.totalB.isEmpty()) ? m.totalB : m.scoreB;

                ps.setString(1, m.teamA);
                ps.setString(2, m.teamB);
                ps.setString(3, scoreA);
                ps.setString(4, scoreB);
                ps.setString(5, m.result);

                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    matchId = rs.getInt(1);
                }
            }

            if (matchId <= 0) {
                System.err.println("ERROR: Could not generate matchId.");
                return -1;
            }

            if (m.battingA != null) {
                try (PreparedStatement ps = conn.prepareStatement(insertBat)) {
                    for (Batsman b : m.battingA) {
                        ps.setInt(1, matchId);
                        ps.setInt(2, 1);                 // innings 1
                        ps.setString(3, m.teamA);
                        ps.setString(4, b.name);
                        ps.setInt(5, b.runs);
                        ps.setInt(6, b.balls);
                        ps.setInt(7, b.fours);
                        ps.setInt(8, b.sixes);
                        ps.setString(9, b.dismissal);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            if (m.battingB != null) {
                try (PreparedStatement ps = conn.prepareStatement(insertBat)) {
                    for (Batsman b : m.battingB) {
                        ps.setInt(1, matchId);
                        ps.setInt(2, 2);
                        ps.setString(3, m.teamB);
                        ps.setString(4, b.name);
                        ps.setInt(5, b.runs);
                        ps.setInt(6, b.balls);
                        ps.setInt(7, b.fours);
                        ps.setInt(8, b.sixes);
                        ps.setString(9, b.dismissal);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            if (m.bowlingB != null) {
                try (PreparedStatement ps = conn.prepareStatement(insertBowl)) {
                    for (Bowler bw : m.bowlingB) {
                        ps.setInt(1, matchId);
                        ps.setInt(2, 1);
                        ps.setString(3, m.teamB);  // bowling team
                        ps.setString(4, bw.name);
                        ps.setFloat(5, bw.overs);
                        ps.setInt(6, bw.runsGiven);
                        ps.setInt(7, bw.wickets);
                        float eco = (bw.overs == 0) ? 0 : (bw.runsGiven / bw.overs);
                        ps.setFloat(8, eco);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

            }

            if (m.bowlingA != null) {
                try (PreparedStatement ps = conn.prepareStatement(insertBowl)) {
                    for (Bowler bw : m.bowlingA) {
                        ps.setInt(1, matchId);
                        ps.setInt(2, 2);
                        ps.setString(3, m.teamA);
                        ps.setString(4, bw.name);
                        ps.setFloat(5, bw.overs);
                        ps.setInt(6, bw.runsGiven);
                        ps.setInt(7, bw.wickets);
                        float eco = (bw.overs == 0) ? 0 : (bw.runsGiven / bw.overs);
                        ps.setFloat(8, eco);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return matchId;
    }

    public static List<Match> getLastMatches(int limit) {

        List<Match> list = new ArrayList<>();
        String sql =
                "SELECT MatchId, TeamAName, TeamBName, ScoreA, ScoreB, ResultText " +
                        "FROM matches ORDER BY MatchId DESC LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                Match m = new Match(
                        String.valueOf(rs.getInt("MatchId")),
                        rs.getString("TeamAName"),
                        rs.getString("TeamBName"),
                        rs.getString("ScoreA"),
                        rs.getString("ScoreB"),
                        rs.getString("ResultText"),
                        false
                );

                m.totalA = m.scoreA;
                m.totalB = m.scoreB;

                list.add(m);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static void loadScorecardForMatch(Match m) {

        if (m == null || m.id == null) return;

        int matchId = Integer.parseInt(m.id);

        m.battingA = new ArrayList<Batsman>();
        m.battingB = new ArrayList<Batsman>();
        m.bowlingA = new ArrayList<Bowler>();
        m.bowlingB = new ArrayList<Bowler>();

        // ------------------ Load BATSMEN ------------------
        String batSql = "SELECT * FROM match_batsman WHERE MatchId = ? ORDER BY InningsNo, ID";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(batSql)) {

            ps.setInt(1, matchId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int innings = rs.getInt("InningsNo");
                String team = rs.getString("TeamName");
                String name = rs.getString("PlayerName");

                int runs   = rs.getInt("Runs");
                int balls  = rs.getInt("Balls");
                int fours  = rs.getInt("Fours");
                int sixes  = rs.getInt("Sixes");
                String dis = rs.getString("Dismissal");

                Batsman b = new Batsman(name, runs, balls, dis);
                b.fours = fours;
                b.sixes = sixes;

                if (innings == 1 && team.equals(m.teamA)) m.battingA.add(b);
                if (innings == 2 && team.equals(m.teamB)) m.battingB.add(b);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // ------------------ Load BOWLERS ------------------
        String bowlSql =
                "SELECT TeamName, PlayerName, Overs, Runs, Wickets,InningsNo " +
                        "FROM match_bowler WHERE MatchId = ?";


        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(bowlSql)) {

            ps.setInt(1, matchId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int innings = rs.getInt("InningsNo");
                String team = rs.getString("TeamName");
                String name = rs.getString("PlayerName");

                float overs = rs.getFloat("Overs");
                int runs    = rs.getInt("Runs");
                int wickets = rs.getInt("Wickets");

                Bowler bw = new Bowler(name, overs, runs, wickets);

                if (innings == 1 && team.equals(m.teamB)) m.bowlingB.add(bw);
                if (innings == 2 && team.equals(m.teamA)) m.bowlingA.add(bw);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
