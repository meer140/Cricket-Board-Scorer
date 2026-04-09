package cricket;
import java.util.ArrayList;

public class Match implements MatchInterface {
    String id;
    String teamA;
    String teamB;
    String scoreA;
    String scoreB;
    String result;
    String winnerTeam;
    boolean live;
    ArrayList<Batsman> battingA = new ArrayList<Batsman>();
    ArrayList<Batsman> battingB = new ArrayList<Batsman>();
    ArrayList<Bowler> bowlingA = new ArrayList<Bowler>();
    ArrayList<Bowler> bowlingB = new ArrayList<Bowler>();
    String manOfTheMatch;
    String totalA = "";
    String totalB = "";

    Match(String id, String teamA, String teamB,
          String scoreA, String scoreB, String result, boolean live) {
        this.id = id;
        this.teamA = teamA;
        this.teamB = teamB;
        this.scoreA = scoreA;
        this.scoreB = scoreB;
        this.setResult(result);
        this.live = live;
    }

    public void setResult(String result) {
        this.result = result;
        this.winnerTeam = detectWinner(result);
    }

    public String detectWinner(String result) {
        if (result == null) return null;

        String lowerResult = result.toLowerCase();

        if (lowerResult.contains("won") || lowerResult.contains("win")) {
            if (lowerResult.contains(teamA.toLowerCase())) {
                return teamA;
            } else if (lowerResult.contains(teamB.toLowerCase())) {
                return teamB;
            }
        }

        if (lowerResult.contains("tie") || lowerResult.contains("no result")) {
            return null;
        }

        return null;
    }

    public String getWinnerTeam() {
        return winnerTeam;
    }

    public boolean hasWinner() {
        return winnerTeam != null && !winnerTeam.isEmpty();
    }

    public String getLoserTeam() {
        if (winnerTeam == null) return null;
        return winnerTeam.equals(teamA) ? teamB : teamA;
    }
}
