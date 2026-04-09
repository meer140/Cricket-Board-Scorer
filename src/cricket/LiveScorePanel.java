package cricket;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class LiveScorePanel extends JPanel {

    private Schedule match;
    private Team teamA, teamB;

    private CricketScoreFull reference;
    private ArrayList<String> allBattingPlayers;
    private ArrayList<String> availableBatsmen;
    private ArrayList<BatCardRow> battingCard = new ArrayList<>();
    private ArrayList<BowlCardRow> bowlingCard = new ArrayList<>();

    private ArrayList<BatCardRow> firstInningsBattingCard = new ArrayList<>();
    private ArrayList<BowlCardRow> firstInningsBowlingCard = new ArrayList<>();
    private static final int MAX_OVERS_PER_BOWLER = 4;
    private static final int BALLS_PER_OVER = 6;
    private static final int MAX_BALLS_PER_BOWLER = MAX_OVERS_PER_BOWLER * BALLS_PER_OVER;

    private int strikerIndex = -1;
    private int nonStrikerIndex = -1;
    private int currentBowlerIndex = 0;

    private boolean resultShown = false;
    private boolean inningsBreakShown = false;

    private final int TOTAL_OVERS;
    private int ballsBowled = 0;
    private int runs = 0;
    private int wickets = 0;
    private int extras = 0;
    private int partnershipRuns = 0;
    private int partnershipBalls = 0;

    private int firstInningsRuns = 0;
    private int firstInningsWickets = 0;
    private int firstInningsBalls = 0;

    private int innings = 1;
    private int target = -1;

    private String strikerName = "Batsman 1";
    private String nonStrikerName = "Batsman 2";
    private String bowlerName = "Bowler";

    private JLabel inningsLabel;
    private JLabel lblScore;
    private JLabel lblOvers, lblExtras, lblCRR, lblProj, lblPartnership,lblRRR ,lblChase;

    private JButton startButton;

    private DefaultTableModel battingModel;
    private DefaultTableModel bowlingModel;

    private JPanel manualPanel;
    private JButton[] scoreButtons;

    private boolean noBallSelected = false;
    private boolean freeHitPending = false;
    private boolean freeHitActive = false;
    private JButton noBallButton;
    private Integer pendingNoBallBatRuns = null;
    private Team battingFirstTeam;
    private Team bowlingFirstTeam;
    private boolean processingDelivery = false;

    //  PLAYER SELECTION DIALOG
    private String selectPlayerDialog(String title, String message, ArrayList<String> players) {
        JDialog dialog = new JDialog(reference, title, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(350, 400);
        dialog.setLocationRelativeTo(this);

        JLabel prompt = new JLabel(message, SwingConstants.CENTER);
        prompt.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String player : players) listModel.addElement(player);

        JList<String> playerList = new JList<>(listModel);
        playerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        playerList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        if (players.size() > 0) playerList.setSelectedIndex(0);

        JButton selectButton = new JButton("Select Player");
        selectButton.setEnabled(players.size() > 0);

        final String[] selectedPlayer = {null};

        selectButton.addActionListener(e -> {
            selectedPlayer[0] = playerList.getSelectedValue();
            if (selectedPlayer[0] != null) dialog.dispose();
            else JOptionPane.showMessageDialog(dialog, "Please select a player.", "Error", JOptionPane.ERROR_MESSAGE);
        });

        playerList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) selectButton.doClick();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(selectButton);

        dialog.add(prompt, BorderLayout.NORTH);
        dialog.add(new JScrollPane(playerList), BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
        return selectedPlayer[0];
    }

    //  BATTING & BOWLING INITIALIZATION
    private void initBattingForCurrentInnings() {
        battingCard.clear();

        Team battingTeam = getBattingTeamForInnings();

        allBattingPlayers = new ArrayList<>();
        allBattingPlayers.addAll(battingTeam.batsmen);
        allBattingPlayers.addAll(battingTeam.allRounders);
        allBattingPlayers.addAll(battingTeam.bowlers);
        while (allBattingPlayers.size() < 11) {
            allBattingPlayers.add("Player " + (allBattingPlayers.size() + 1));
        }

        availableBatsmen = new java.util.ArrayList<>(allBattingPlayers);

        String opener1 = selectPlayerDialog("Select Opener 1", "Choose the first batsman to open the innings:", availableBatsmen);
        if (opener1 == null) opener1 = availableBatsmen.get(0);
        availableBatsmen.remove(opener1);

        String opener2 = selectPlayerDialog("Select Opener 2", "Choose the second batsman (non-striker):", availableBatsmen);
        if (opener2 == null) opener2 = availableBatsmen.get(0);
        availableBatsmen.remove(opener2);

        BatCardRow row1 = new BatCardRow(opener1);
        BatCardRow row2 = new BatCardRow(opener2);

        battingCard.add(row1);
        battingCard.add(row2);

        strikerIndex = 0;
        nonStrikerIndex = 1;

        strikerName = row1.name;
        nonStrikerName = row2.name;
    }

    private void initBowlingForCurrentInnings() {
        bowlingCard.clear();

        Team bowlingTeam = getBowlingTeamForInnings();
        ArrayList<String> bowlersList = bowlingTeam.bowlers;


        if (bowlersList == null || bowlersList.isEmpty()) {
            bowlersList = new java.util.ArrayList<>();
            bowlersList.add("Bowler 1");
            bowlersList.add("Bowler 2");
            bowlersList.add("Bowler 3");
        }

        String firstBowler = selectPlayerDialog("Select First Bowler", "Choose the bowler for the first over:", (ArrayList<String>) bowlersList);
        if (firstBowler == null) firstBowler = bowlersList.get(0);

        bowlingCard.add(new BowlCardRow(firstBowler));

        currentBowlerIndex = 0;
        bowlerName = firstBowler;
    }

    //  CONSTRUCTOR
    public LiveScorePanel(Schedule match, Team teamA, Team teamB,CricketScoreFull r) {
        this.match = match;
        this.teamA = teamA;
        this.teamB = teamB;
        this.reference = r;

        String fmt = match.format == null ? "" : match.format.toUpperCase();
        if (fmt.contains("T20")) TOTAL_OVERS = 20;
        else if (fmt.contains("ODI")) TOTAL_OVERS = 50;
        else TOTAL_OVERS = 2;

        innings = 1;

        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(15, 23, 42));

        JPanel scoreCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(59, 130, 246),
                        getWidth(), getHeight(), new Color(37, 99, 235)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        scoreCard.setLayout(new BoxLayout(scoreCard, BoxLayout.Y_AXIS));
        scoreCard.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));
        scoreCard.setOpaque(false);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        titlePanel.setOpaque(false);

        JLabel cricketIcon = new JLabel("🏏");
        cricketIcon.setFont(new Font("Segoe UI Emoji", Font.BOLD, 28));

        JLabel title = new JLabel(match.teamA + " vs " + match.teamB);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);

        titlePanel.add(cricketIcon);
        titlePanel.add(title);

        inningsLabel = new JLabel(match.teamA + " Innings");
        inningsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        inningsLabel.setForeground(new Color(191, 219, 254));
        inningsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblScore = new JLabel("0-0");
        lblScore.setFont(new Font("Segoe UI", Font.BOLD, 64));
        lblScore.setForeground(Color.WHITE);
        lblScore.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel liveIndicator = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        liveIndicator.setOpaque(false);

        JLabel liveDot = new JLabel("●");
        liveDot.setFont(new Font("Segoe UI", Font.BOLD, 20));
        liveDot.setForeground(new Color(239, 68, 68));

        JLabel liveText = new JLabel("LIVE");
        liveText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        liveText.setForeground(new Color(239, 68, 68));

        lblChase = new JLabel("", SwingConstants.CENTER);
        lblChase.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblChase.setForeground(new Color(224, 231, 255));   // light text
        lblChase.setAlignmentX(Component.CENTER_ALIGNMENT);


        liveIndicator.add(liveDot);
        liveIndicator.add(liveText);

        scoreCard.add(titlePanel);
        scoreCard.add(Box.createVerticalStrut(5));
        scoreCard.add(inningsLabel);
        scoreCard.add(Box.createVerticalStrut(12));
        scoreCard.add(lblScore);
        scoreCard.add(Box.createVerticalStrut(6));
        scoreCard.add(lblChase);
        scoreCard.add(Box.createVerticalStrut(8));
        scoreCard.add(liveIndicator);

        add(scoreCard, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(new Color(15, 23, 42));
        center.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel infoGrid = new JPanel(new GridLayout(1, 6, 15, 0));
        infoGrid.setOpaque(false);

        lblOvers       = new JLabel("Overs 0.0 / " + TOTAL_OVERS);
        lblExtras      = new JLabel("Extras");
        lblCRR         = new JLabel("CRR");
        lblRRR         = new JLabel("RRR");
        lblProj        = new JLabel("Projected");
        lblPartnership = new JLabel("Partnership");

        infoGrid.add(lblOvers);
        infoGrid.add(lblExtras);
        infoGrid.add(lblCRR);
        infoGrid.add(lblRRR);
        infoGrid.add(lblProj);
        infoGrid.add(lblPartnership);


        center.add(infoGrid);
        center.add(Box.createVerticalStrut(25));

        JPanel batSection = new JPanel(new BorderLayout());
        batSection.setOpaque(false);

        JLabel batHeader = new JLabel("  BATTING");
        batHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        batHeader.setForeground(new Color(148, 163, 184));
        batHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        String[] batCols = {"Batsman", "R", "B", "4s", "6s", "SR", "Dismissal"};
        battingModel = new DefaultTableModel(batCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable batTable = createModernTable(battingModel);
        batSection.add(batTable, BorderLayout.CENTER);

        center.add(batSection);
        center.add(Box.createVerticalStrut(20));

        JPanel bowlSection = new JPanel(new BorderLayout());
        bowlSection.setOpaque(false);

        JLabel bowlHeader = new JLabel("  BOWLING");
        bowlHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        bowlHeader.setForeground(new Color(148, 163, 184));
        bowlHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        String[] bowlCols = {"Bowler", "O", "M", "R", "W", "Eco"};
        bowlingModel = new DefaultTableModel(bowlCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable bowlTable = createModernTable(bowlingModel);

        bowlSection.add(bowlTable, BorderLayout.CENTER);
        center.add(bowlSection);

        startButton = new JButton("START MATCH");
        startButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        startButton.setFocusPainted(false);
        startButton.setBackground(new Color(34, 197, 94));
        startButton.setForeground(Color.WHITE);
        startButton.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40));
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        startButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { startButton.setBackground(new Color(22, 163, 74)); }
            public void mouseExited(MouseEvent e) { startButton.setBackground(new Color(34, 197, 94)); }
        });

        center.add(Box.createVerticalStrut(20));
        center.add(startButton);

        manualPanel = new JPanel(new GridLayout(3, 3, 12, 12));
        manualPanel.setBackground(new Color(15, 23, 42));
        manualPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        scoreButtons = new JButton[9];
        String[] buttonLabels = {"0", "1", "2", "3", "4", "6", "OUT", "WD", "NB"};
        Color[] buttonColors = {
                new Color(71, 85, 105),
                new Color(59, 130, 246),
                new Color(59, 130, 246),
                new Color(59, 130, 246),
                new Color(139, 92, 246),
                new Color(236, 72, 153),
                new Color(239, 68, 68),
                new Color(234, 179, 8),
                new Color(34, 197, 94)
        };

        for (int i = 0; i < 9; i++) {
            scoreButtons[i] = createScoreButton(buttonLabels[i], buttonColors[i]);
            manualPanel.add(scoreButtons[i]);
        }

        // Attach scoring actions (label-based)
        for (JButton b : scoreButtons) {
            String t = b.getText();
            if ("0".equals(t)) b.addActionListener(e -> processBall(0));
            else if ("1".equals(t)) b.addActionListener(e -> processBall(1));
            else if ("2".equals(t)) b.addActionListener(e -> processBall(2));
            else if ("3".equals(t)) b.addActionListener(e -> processBall(3));
            else if ("4".equals(t)) b.addActionListener(e -> processBall(4));
            else if ("6".equals(t)) b.addActionListener(e -> processBall(6));
            else if ("OUT".equals(t)) b.addActionListener(e -> processBall(-1));
            else if ("WD".equals(t)) b.addActionListener(e -> processBall(-2));
            else if ("NB".equals(t)) {
                noBallButton = b;
                b.addActionListener(e -> processNoBallDelivery());
            }

        }
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(15, 23, 42));
        leftPanel.add(manualPanel, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                leftPanel,
                new JScrollPane(center)
        );
        split.setDividerLocation(380);
        split.setResizeWeight(0.0);
        split.setDividerSize(2);
        split.setBackground(new Color(15, 23, 42));
        split.setBorder(null);

        add(split, BorderLayout.CENTER);

        setScoreButtonsEnabled(false);

        startButton.addActionListener(e -> {

            startButton.setVisible(false);

            Team tossWinner = askTossWinner();
            boolean choseBat = askBatOrBowl(tossWinner);

            if (choseBat) {
                battingFirstTeam = tossWinner;
                bowlingFirstTeam = (tossWinner == teamA) ? teamB : teamA;
            } else {
                bowlingFirstTeam = tossWinner;
                battingFirstTeam = (tossWinner == teamA) ? teamB : teamA;
            }

            innings = 1;
            ballsBowled = 0; runs = 0; wickets = 0; extras = 0; partnershipRuns = 0;
            target = -1;
            resultShown = false;
            freeHitPending = false;
            freeHitActive = false;
            noBallSelected = false;
            if (noBallButton != null) {
                noBallButton.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                noBallButton.setEnabled(true);
            }

            inningsLabel.setText(battingFirstTeam.name + " Innings");
            initBattingForCurrentInnings();
            initBowlingForCurrentInnings();
            refreshBattingTable();
            refreshBowlingTable();
            updateUIFields();

            setScoreButtonsEnabled(true);
        });
    }

    private JTable createModernTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setForeground(new Color(226, 232, 240));
        table.setBackground(new Color(30, 41, 59));
        table.setGridColor(new Color(51, 65, 85));
        table.setSelectionBackground(new Color(59, 130, 246));
        table.setSelectionForeground(Color.WHITE);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(15, 23, 42));
        header.setForeground(new Color(148, 163, 184));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(51, 65, 85)));

        return table;
    }

    private JButton createScoreButton(String label, Color color) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(color.brighter()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(color); }
        });

        return btn;
    }

    private String infoHtml(String title, String value) {
        return "<html><div style='text-align:center; padding:5px;'>" +
                "<div style='font-size:11px; color:#94A3B8; margin-bottom:3px;'>" + title + "</div>" +
                "<div style='font-size:16px; font-weight:bold; color:#FFFFFF;'>" + value + "</div>" +
                "</div></html>";
    }

    private void setScoreButtonsEnabled(boolean enabled) {
        for (JButton btn : scoreButtons) btn.setEnabled(enabled);

        if (noBallSelected && noBallButton != null) noBallButton.setEnabled(false);
    }

    //  Batting TABLE REFRESH
    private void refreshBattingTable() {
        battingModel.setRowCount(0);

        battingModel.addRow(new Object[]{
                "BATTERS", "Runs", "Balls", "4s", "6s", "SR", "Dismissal"
        });

        if (strikerIndex < 0 || nonStrikerIndex < 0 || battingCard.size() < 2) return;

        BatCardRow s = battingCard.get(strikerIndex);
        BatCardRow ns = battingCard.get(nonStrikerIndex);

        addBatsmanRow(s, true);
        addBatsmanRow(ns, false);
    }

    private void addBatsmanRow(BatCardRow row, boolean isStriker) {
        double sr = row.balls == 0 ? 0.0 : (row.runs * 100.0 / row.balls);
        String nameDisplay = row.name + (isStriker ? " *" : "");

        battingModel.addRow(new Object[]{
                nameDisplay,
                row.runs,
                row.balls,
                row.fours,
                row.sixes,
                String.format("%.1f", sr),
                row.out ? row.dismissal : ""
        });
    }

    private Team askTossWinner() {
        String[] options = { teamA.name, teamB.name };

        int choice = JOptionPane.showOptionDialog(
                this,
                "Who won the toss?",
                "Toss",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        // default teamA
        return (choice == 1) ? teamB : teamA;
    }

    private boolean askBatOrBowl(Team tossWinner) {
        String[] options = { "BAT", "BOWL" };

        int choice = JOptionPane.showOptionDialog(
                this,
                tossWinner.name + " won the toss.\nWhat did they choose?",
                "Toss Decision",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        return choice != 1; // true = BAT, false = BOWL
    }

    private boolean canBowlAnotherBall(BowlCardRow bw) {
        return bw.balls < (MAX_OVERS_PER_BOWLER * BALLS_PER_OVER);
    }

    private void refreshBowlingTable() {
        bowlingModel.setRowCount(0);

        bowlingModel.addRow(new Object[]{
                "BOWLER", "Overs", "Maiden", "Runs", "Wickets", "Economy"
        });

        if (bowlingCard.isEmpty()) return;

        BowlCardRow bowler = bowlingCard.get(currentBowlerIndex);

        String overs = (bowler.balls / 6) + "." + (bowler.balls % 6);
        double eco = bowler.balls == 0 ? 0.0 : (bowler.runs * 6.0 / bowler.balls);

        bowlingModel.addRow(new Object[]{
                bowler.name,
                overs,
                0,
                bowler.runs,
                bowler.wickets,
                String.format("%.1f", eco)
        });
    }

    private void swapStrikers() {
        int tmp = strikerIndex;
        strikerIndex = nonStrikerIndex;
        nonStrikerIndex = tmp;

        strikerName = battingCard.get(strikerIndex).name;
        nonStrikerName = battingCard.get(nonStrikerIndex).name;
    }
    private void handleWicket() {
        BatCardRow striker = battingCard.get(strikerIndex);
        striker.out = true;
        striker.dismissal = "Bowled by " + bowlerName;

        partnershipRuns = 0;
        partnershipBalls = 0;
        updateUIFields();


        if (wickets >= 10 || availableBatsmen.isEmpty()) return;

        setScoreButtonsEnabled(false);

        String next = selectPlayerDialog(
                "Select Next Batsman (" + (wickets + 1) + ")",
                "Select new batsman:",
                availableBatsmen
        );

        setScoreButtonsEnabled(true);

        if (next == null) next = availableBatsmen.get(0);
        availableBatsmen.remove(next);

        BatCardRow newRow = new BatCardRow(next);
        battingCard.add(newRow);

        strikerIndex = battingCard.size() - 1;
        strikerName = battingCard.get(strikerIndex).name;
    }

    private void handleBowlerRotation() {
        Team bowlingTeam = getBowlingTeamForInnings();
        ArrayList<String> allBowlers = new ArrayList<>();
        allBowlers.addAll(bowlingTeam.bowlers);
        allBowlers.addAll(bowlingTeam.allRounders);

        if (allBowlers.isEmpty()) allBowlers.add("Dummy Bowler");

        String currentBowlerName = bowlerName;

        ArrayList<String> availableBowlers = new ArrayList<>();
        for (String bName : allBowlers) {
            int balls = 0;
            for (BowlCardRow row : bowlingCard) {
                if (row.name.equals(bName)) {
                    balls = row.balls;
                    break;
                }
            }
            if (balls < MAX_BALLS_PER_BOWLER) {
                availableBowlers.add(bName);
            }
        }
        availableBowlers.remove(currentBowlerName);

        if (availableBowlers.isEmpty()) {
            int currentBalls = 0;
            for (BowlCardRow row : bowlingCard) {
                if (row.name.equals(currentBowlerName)) {
                    currentBalls = row.balls;
                    break;
                }
            }

            if (currentBalls < MAX_BALLS_PER_BOWLER) {
                availableBowlers.add(currentBowlerName);
                JOptionPane.showMessageDialog(this,
                        "Warning: No other bowlers available. Same bowler will continue.",
                        "Bowler Selection",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "No eligible bowlers available (everyone has completed " + MAX_OVERS_PER_BOWLER + " overs).",
                        "Bowler Selection",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        setScoreButtonsEnabled(false);

        String nextBowlerName = selectPlayerDialog(
                "Select Next Bowler",
                "Select the bowler for the next over (Current: " + currentBowlerName + "):",
                (ArrayList<String>) availableBowlers
        );

        setScoreButtonsEnabled(true);

        if (nextBowlerName == null) nextBowlerName = availableBowlers.get(0);

        if (nextBowlerName.equals(currentBowlerName) && availableBowlers.size() > 1) {
            JOptionPane.showMessageDialog(this,
                    "Same bowler cannot bowl consecutive overs! Please select a different bowler.",
                    "Invalid Selection",
                    JOptionPane.ERROR_MESSAGE);
            handleBowlerRotation();
            return;
        }

        int newBowlerIndex = -1;
        for (int i = 0; i < bowlingCard.size(); i++) {
            if (bowlingCard.get(i).name.equals(nextBowlerName)) {
                newBowlerIndex = i;
                break;
            }
        }

        if (newBowlerIndex == -1) {
            bowlingCard.add(new BowlCardRow(nextBowlerName));
            newBowlerIndex = bowlingCard.size() - 1;
        }

        currentBowlerIndex = newBowlerIndex;
        bowlerName = nextBowlerName;
        updateUIFields();
    }
    private int askBatRunsOnNoBall() {
        String[] options = {"0", "1", "2", "3", "4", "6", "OUT"};

        int choice = JOptionPane.showOptionDialog(
                this,
                "NO BALL!\nSelect runs off the bat or choose OUT:",
                "No Ball",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice < 0) return -2;
        if ("OUT".equals(options[choice])) return -1;

        return Integer.parseInt(options[choice]);
    }


    private void processNoBallDelivery() {
        if (resultShown) return;
        if (processingDelivery) return;

        processingDelivery = true;
        try {
            if (bowlingCard.isEmpty() || battingCard.isEmpty() || strikerIndex < 0 || nonStrikerIndex < 0) {
                JOptionPane.showMessageDialog(this, "Press START MATCH first.");
                return;
            }

            BowlCardRow bowler = bowlingCard.get(currentBowlerIndex);
            BatCardRow striker = battingCard.get(strikerIndex);

            int nbChoice = askBatRunsOnNoBall();

            boolean outClicked = (nbChoice == -1);
            int batRuns = (nbChoice < 0) ? 0 : nbChoice;

            extras += 1;
            int totalRuns = 1 + batRuns;
            runs += totalRuns;
            bowler.runs += totalRuns;

            striker.balls++;
            striker.runs += batRuns;
            partnershipRuns += batRuns;

            if (batRuns == 4) striker.fours++;
            if (batRuns == 6) striker.sixes++;

            if (!outClicked) {
                if (batRuns % 2 != 0) {
                    swapStrikers();
                }
            }
            else {
                boolean isRunOut = askRunOutOrNormal();

                if (!isRunOut) {
                    JOptionPane.showMessageDialog(this,
                            "NO BALL: Normal wicket is not allowed.\nOnly RUN OUT can happen on a no-ball.",
                            "No Ball",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    int outIndex = askWhoRunOut();
                    int completed = askRunsCompletedBeforeWicket();

                    runs += completed;
                    bowler.runs += completed;
                    striker.runs += completed;
                    partnershipRuns += completed;

                    wickets++;
                    BatCardRow outBatter = battingCard.get(outIndex);
                    outBatter.out = true;
                    outBatter.dismissal = "Run out";

                    partnershipRuns = 0;

                    updateUIFields();

                    if (wickets >= 10 || availableBatsmen.isEmpty()) {
                        endInningsOrMatch();
                        return;
                    }

                    bringNextBatsmanAtIndex(outIndex);

                    if (completed % 2 == 0 && completed!=0 ) {
                        swapStrikers();
                    }
                }
            }

            freeHitPending = true;
            updateUIFields();

            if (innings == 2 && runs >= target) {
                endMatch();
            }

        } finally {
            processingDelivery = false;
        }
    }

    //Main Method to Runs Score
    private void processBall(int outcome) {
        if (resultShown) return;
        if (processingDelivery) return;
        processingDelivery = true;

        try {
            if (bowlingCard.isEmpty() || battingCard.isEmpty() || strikerIndex < 0 || nonStrikerIndex < 0) {
                JOptionPane.showMessageDialog(this, "Press START MATCH first.");
                return;
            }

            boolean wicketBall = (outcome == -1);
            boolean wideBall = (outcome == -2);

            BowlCardRow bowler = bowlingCard.get(currentBowlerIndex);
            BatCardRow striker = battingCard.get(strikerIndex);

            freeHitActive = freeHitPending;

            // WIDE
            if (wideBall) {
                int extraOnWide = askExtraRunsOnWide();
                int total = 1 + extraOnWide;
                extras += total;
                partnershipRuns+=total;
                runs += total;
                bowler.runs += total;
                if(extraOnWide%2==1){
                    swapStrikers();
                }
                updateUIFields();
                return;
            }

            if (bowler.balls >= MAX_BALLS_PER_BOWLER) {
                JOptionPane.showMessageDialog(
                        this,
                        bowler.name + " has already completed " + MAX_OVERS_PER_BOWLER + " overs.\nSelect another bowler.",
                        "Over Limit",
                        JOptionPane.WARNING_MESSAGE
                );
                handleBowlerRotation();
                return;
            }

            ballsBowled++;
            bowler.balls++;
            partnershipBalls++;

            boolean thisBallWasFreeHit = freeHitActive;
            freeHitPending = false;
            freeHitActive = false;

            // WICKET ON LEGAL BALL
            if (wicketBall) {
                striker.balls++;
                boolean isRunOut = askRunOutOrNormal();

                if (thisBallWasFreeHit && !isRunOut) {
                    updateUIFields();
                    JOptionPane.showMessageDialog(this,
                            "FREE HIT: Normal wicket not allowed.\nOnly Run-out is allowed.\nOUT ignored.");

                    if (ballsBowled % 6 == 0) {
                        updateUIFields();
                        swapStrikers();
                        handleBowlerRotation();
                    }

                    if (ballsBowled >= TOTAL_OVERS * 6) {
                        updateUIFields();
                        endInningsOrMatch();
                    }
                    return;
                }

                if (isRunOut) {
                    int outIndex = askWhoRunOut();
                    int completed = askRunsCompletedBeforeWicket();

                    runs += completed;
                    bowler.runs += completed;
                    striker.runs += completed;
                    partnershipRuns += completed;

                    if (completed % 2 == 0 && completed !=0) swapStrikers();

                    wickets++;
                    BatCardRow outBatter = battingCard.get(outIndex);
                    outBatter.out = true;
                    outBatter.dismissal = "Run out";

                    updateUIFields();

                    if (wickets >= 10 || availableBatsmen.isEmpty()) {
                        endInningsOrMatch();
                        return;
                    }

                    bringNextBatsmanAtIndex(outIndex);
                    updateUIFields();
                    return;

                } else {
                    wickets++;
                    bowler.wickets++;
                    striker.out = true;
                    striker.dismissal = "Bowled by " + bowlerName;

                    updateUIFields();

                    if (wickets >= 10 || availableBatsmen.isEmpty()) {
                        endInningsOrMatch();
                        return;
                    }

                    handleWicket();
                    return;
                }
            }

            // RUNS ON LEGAL BALL
            runs += outcome;
            bowler.runs += outcome;
            striker.balls++;
            striker.runs += outcome;
            partnershipRuns += outcome;

            if (outcome == 4) striker.fours++;
            if (outcome == 6) striker.sixes++;


            if (outcome % 2 == 1) swapStrikers();
            updateUIFields();

            if (innings == 2 && runs >= target) {
                endMatch();
                return;
            }

            if (ballsBowled % 6 == 0) {
                updateUIFields();
                swapStrikers();
                handleBowlerRotation();
            }

            if (ballsBowled >= TOTAL_OVERS * 6) {
                updateUIFields();
                endInningsOrMatch();
            }

        } finally {
            processingDelivery = false;
        }
    }
    private void endInningsOrMatch() {
        setScoreButtonsEnabled(false);

        if (innings == 1) {
            JOptionPane.showMessageDialog(this,
                    "Innings Finished!\n" + getBattingTeamForInnings().name + ": " + runs + "/" + wickets,
                    "Innings Break",
                    JOptionPane.INFORMATION_MESSAGE);

            target = runs + 1;
            resetForSecondInnings();
            setScoreButtonsEnabled(true);
        } else {
            endMatch();
        }
    }

    private void endMatch() {
        Team innings1Team = battingFirstTeam;
        Team innings2Team = bowlingFirstTeam;

        String result;
        boolean tied;

        if (runs >= target) {
            int wicketsLeft = 10 - wickets;
            result = innings2Team.name + " won by " + wicketsLeft + " wickets";
            tied = false;
        } else if (runs == target - 1) {
            result = "Match Tied";
            tied = true;
        } else {
            int runDiff = (target - 1) - runs;
            result = innings1Team.name + " won by " + runDiff + " runs";
            tied = false;
        }
        saveMatchToDB();
        String winner = null;
        String loser  = null;

        if (!tied) {
            if (result.startsWith(innings1Team.name)) {
                winner = innings1Team.name;
                loser  = innings2Team.name;
            } else {
                winner = innings2Team.name;
                loser  = innings1Team.name;
            }
        }

        RankingDB.applyMatchResult(winner, loser, tied);
        ScheduleDB.markPlayed(match.getScheduleId());
        match.setPlayed(true);

            reference.refreshHomeRecentMatchesUI();
            reference.reloadRankings();

        JOptionPane.showMessageDialog(this, result, "Match Result", JOptionPane.INFORMATION_MESSAGE);

        setScoreButtonsEnabled(false);
    }

    //  UPDATE UI
    private void updateUIFields() {
        lblScore.setText(runs + "-" + wickets);

        int completed = ballsBowled / 6;
        int rem = ballsBowled % 6;
        String oversDone = completed + "." + rem;

        lblOvers.setText(infoHtml("Overs", oversDone + " / " + TOTAL_OVERS));

        double crr = ballsBowled == 0 ? 0.0 : (runs * 6.0 / ballsBowled);
        lblCRR.setText(infoHtml("CRR", String.format("%.1f", crr)));

        int proj = ballsBowled == 0 ? 0 : (int) Math.round(crr * TOTAL_OVERS);
        lblProj.setText(infoHtml("Projected", String.valueOf(proj)));

        lblExtras.setText(infoHtml("Extras", String.valueOf(extras)));

        lblPartnership.setText(infoHtml("Partnership", partnershipRuns + " (" + partnershipBalls + ")"));
        if (innings == 2) {
            int ballsRemaining = (TOTAL_OVERS * 6) - ballsBowled;
            int runsToWin = target - runs;
            if (runsToWin < 0) runsToWin = 0;

            double rrr = (ballsRemaining <= 0 || runsToWin <= 0) ? 0.0 : (runsToWin * 6.0 / ballsRemaining);
            lblRRR.setText(infoHtml("RRR", String.format("%.2f", rrr)));

            String chasingTeam = (bowlingFirstTeam != null) ? bowlingFirstTeam.name : match.teamB;
            if (runsToWin <= 0) {
                lblChase.setText("✅ " + chasingTeam + " has won the match!");
            } else if (ballsRemaining <= 0) {
                lblChase.setText("⛔ " + chasingTeam + " needed " + runsToWin + " more (no balls left)");
            } else {
                lblChase.setText("📌 " + chasingTeam + " needs " + runsToWin + " runs in " + ballsRemaining + " balls to win");
            }
        } else {
            lblRRR.setText(infoHtml("RRR", "-"));
            if (lblChase != null) lblChase.setText("");
        }

        refreshBattingTable();
        refreshBowlingTable();
    }

    private void resetForSecondInnings() {

        innings = 2;
        inningsLabel.setText(getBattingTeamForInnings().name + " Innings (Target: " + target + ")");


        firstInningsBattingCard = new ArrayList<>(battingCard);
        firstInningsBowlingCard = new ArrayList<>(bowlingCard);

        firstInningsRuns = runs;
        firstInningsWickets = wickets;
        firstInningsBalls = ballsBowled;

        ballsBowled = 0;
        runs = 0;
        wickets = 0;
        extras = 0;
        partnershipRuns = 0;
        partnershipBalls = 0;


        innings = 2;

        noBallSelected = false;
        freeHitPending = false;
        freeHitActive = false;
        if (noBallButton != null) {
            noBallButton.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            noBallButton.setEnabled(true);
        }

        lblScore.setText("0-0");
        lblOvers.setText(infoHtml("Overs", "0.0 / " + TOTAL_OVERS));
        lblCRR.setText(infoHtml("CRR", "0.0"));
        lblProj.setText(infoHtml("Projected", "0"));
        lblPartnership.setText(infoHtml("Partnership", "0(0)"));
        lblExtras.setText(infoHtml("Extras", "0"));

        initBattingForCurrentInnings();
        initBowlingForCurrentInnings();
        refreshBattingTable();
        refreshBowlingTable();
    }

    private int askExtraRunsOnWide() {
        String[] options = {"0", "1", "2", "3", "4"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Wide ball.\nAdditional runs (byes) taken?",
                "Wide + Runs",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
        return (choice < 0) ? 0 : Integer.parseInt(options[choice]);
    }
    private Team getBattingTeamForInnings() {
        return (innings == 1) ? battingFirstTeam : bowlingFirstTeam;
    }

    private Team getBowlingTeamForInnings() {
        return (innings == 1) ? bowlingFirstTeam : battingFirstTeam;
    }


    private boolean askRunOutOrNormal() {
        String[] options = {"Normal Wicket", "Run Out"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Wicket type?",
                "Wicket",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
        return choice == 1;
    }

    private int askRunsCompletedBeforeWicket() {
        String[] options = {"0", "1", "2", "3"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Runs completed before wicket?",
                "Run Out Runs",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
        return (choice < 0) ? 0 : Integer.parseInt(options[choice]);
    }

    private int askWhoRunOut() {
        String s = battingCard.get(strikerIndex).name;
        String ns = battingCard.get(nonStrikerIndex).name;

        String[] options = {"Striker: " + s, "Non-Striker: " + ns};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Who got run out?",
                "Run Out",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
        return (choice == 1) ? nonStrikerIndex : strikerIndex;
    }

    private void bringNextBatsmanAtIndex(int outIndex) {
        if (wickets >= 10 || availableBatsmen.isEmpty()) return;

        setScoreButtonsEnabled(false);

        String next = selectPlayerDialog(
                "Select Next Batsman (" + (wickets + 1) + ")",
                "Select new batsman:",
                availableBatsmen
        );

        setScoreButtonsEnabled(true);

        if (next == null) next = availableBatsmen.get(0);
        availableBatsmen.remove(next);

        BatCardRow newRow = new BatCardRow(next);
        battingCard.add(newRow);

        int newIndex = battingCard.size() - 1;

        if (outIndex == strikerIndex) {
            strikerIndex = newIndex;
            strikerName = battingCard.get(strikerIndex).name;
        } else if (outIndex == nonStrikerIndex) {
            nonStrikerIndex = newIndex;
            nonStrikerName = battingCard.get(nonStrikerIndex).name;
        }
    }

    private void saveMatchToDB() {
        Team innings1Team = battingFirstTeam;   // batted first
        Team innings2Team = bowlingFirstTeam;   // batted second

        Match m = new Match(
                null,
                innings1Team.name,
                innings2Team.name,
                firstInningsRuns + "/" + firstInningsWickets,
                runs + "/" + wickets,
                "",
                true
        );

        if (runs >= target) {
            m.result = innings2Team.name + " won by " + (10 - wickets) + " wickets";
        } else if (runs == target - 1) {
            m.result = "Match Tied";
        } else {
            m.result = innings1Team.name + " won by " + ((target - 1) - runs) + " runs";
        }

        m.battingA = new ArrayList<Batsman>();
        for (BatCardRow r : firstInningsBattingCard) {
            Batsman b = new Batsman(r.name, r.runs, r.balls, r.dismissal);
            b.fours = r.fours;
            b.sixes = r.sixes;
            m.battingA.add(b);
        }

        m.battingB = new ArrayList<Batsman>();
        for (BatCardRow r : battingCard) {
            Batsman b = new Batsman(r.name, r.runs, r.balls, r.dismissal);
            b.fours = r.fours;
            b.sixes = r.sixes;
            m.battingB.add(b);
        }

        m.bowlingA = new ArrayList<Bowler>();
        for (BowlCardRow bw : firstInningsBowlingCard) {
            float overs = bw.balls / 6f;
            m.bowlingA.add(new Bowler(bw.name, overs, bw.runs, bw.wickets));
        }

        m.bowlingB = new ArrayList<Bowler>();
        for (BowlCardRow bw : bowlingCard) {
            float overs = bw.balls / 6f;
            m.bowlingB.add(new Bowler(bw.name, overs, bw.runs, bw.wickets));
        }

        MatchDB.saveCompletedMatch(m);
        reference.refreshHomeRecentMatchesUI();
    }


}

