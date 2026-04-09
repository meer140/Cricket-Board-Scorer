package cricket;

public class BatCardRow {
    String name;
    int runs = 0;
    int balls = 0;
    int fours = 0;
    int sixes = 0;
    boolean out = false;
    String dismissal = "";

    public BatCardRow(String name) {
        this.name = name;
    }
}
