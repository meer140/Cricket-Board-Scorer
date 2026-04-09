package cricket;

public interface MatchInterface {
    void setResult(String result);
    String detectWinner(String result);
    String getWinnerTeam();
    boolean hasWinner();
    String getLoserTeam();
}