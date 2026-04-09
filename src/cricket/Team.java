package cricket;
import java.util.ArrayList;

public class Team {

    public int teamId;
    public String name;
    public String code;

    public ArrayList<String> batsmen = new ArrayList<>();
    public ArrayList<String> bowlers = new ArrayList<>();
    public ArrayList<String> allRounders = new ArrayList<>();

    public Team() {
    }

    public Team(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public Team(int teamId, String name, String code) {
        this.teamId = teamId;
        this.name = name;
        this.code = code;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
