package cricket;

public class RankingEntry {
    int position;
    String team;
    int matches;
    int points;
    int rating;
    String countryCode;

    public RankingEntry(int position, String team,
                 int matches, int points, int rating,
                 String countryCode) {
        this.position = position;
        this.team = team;
        this.matches = matches;
        this.points = points;
        this.rating = rating;
        this.countryCode = countryCode;
    }
}
