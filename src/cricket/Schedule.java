package cricket;

public class Schedule {

    private int scheduleId;

    public String teamA;
    public String codeA;
    public String teamB;
    public String codeB;
    public String date;
    public String time;
    public String venue;
    public String format;
    private boolean played;

    public Schedule() { }

    public Schedule(String teamA, String codeA,
                    String teamB, String codeB,
                    String date, String time,
                    String venue, String format) {
        this.teamA = teamA;
        this.codeA = codeA;
        this.teamB = teamB;
        this.codeB = codeB;
        this.date = date;
        this.time = time;
        this.venue = venue;
        this.format = format;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public boolean isPlayed() {
        return played;
    }

    public void setPlayed(boolean played) {
        this.played = played;
    }
}



