package cricket;
public class Player {

    private int playerId;
    private String fullName;
    private String role;

    public Player(int playerId, String fullName, String role) {
        this.playerId = playerId;
        this.fullName = fullName;
        this.role = role;
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }
}
