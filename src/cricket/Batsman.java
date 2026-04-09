package cricket;
public  class Batsman {
    String name;
    int runs;
    int balls;
    String dismissal;
    int fours;
    int sixes;


    public Batsman(String name, int runs, int balls, int fours,int sixes, String dismissal) {
        this.name = name;
        this.runs = runs;
        this.balls = balls;
        this.fours = fours;
        this.sixes = sixes;
        this.dismissal = dismissal;

    }
    public Batsman(String name, int runs, int balls, String dismissal) {
        this.name = name;
        this.runs = runs;
        this.balls = balls;
        this.dismissal = dismissal;

    }
}