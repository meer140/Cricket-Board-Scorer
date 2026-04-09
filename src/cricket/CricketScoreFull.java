package cricket;

import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.net.URL;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.table.TableCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.table.JTableHeader;


public class CricketScoreFull extends JFrame {

    private JPanel mainPanel;
    private CardLayout cardLayout;

    private JPanel homePanel;
    private JPanel recentMatchesGrid;
    JPanel grid;
    private Map<String, JPanel> panelMap = new HashMap<>();
    private JTable rankingTable;
    private DefaultTableModel rankingModel;
    private JPanel rankingPanel;


    private java.util.List<Match> matches = new ArrayList<>();
    private java.util.List<Team> teams = new ArrayList<>();
    private java.util.List<RankingEntry> rankings = new ArrayList<>();
    private java.util.List<Schedule> scheduleMatches = new java.util.ArrayList<>();
    private Map<String, ImageIcon> flagCache = new HashMap<>();



    public CricketScoreFull() {
        setTitle("Cricket Scoreboard - OOP Project");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        prepareTeamsData();
        prepareSampleData();
        prepareRankingsData();
        prepareScheduleData();

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        homePanel = createHomePanel();
        panelMap.put("Home", homePanel);
        mainPanel.add(homePanel, "Home");

        mainPanel.add(createUpcomingPanel(), "Upcoming");
        mainPanel.add(createTeamsPanel(), "Teams");
        mainPanel.add(createRankingPanel(), "Ranking");

        setJMenuBar(createMenuBar());
        add(mainPanel);

        setVisible(true);
    }

    private void switchPanel(JPanel panel) {
        String key = null;
        for (Map.Entry<String, JPanel> e : panelMap.entrySet()) {
            if (e.getValue() == panel) {
                key = e.getKey();
                break;
            }
        }

        if (key == null) {
            key = "Screen" + panelMap.size();
            panelMap.put(key, panel);
            mainPanel.add(panel, key);
        }
        cardLayout.show(mainPanel, key);
    }

    private JMenuBar createMenuBar() {
        JMenuBar bar = new JMenuBar();
        bar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        Color color1=Color.decode("#023E8A");
        bar.setPreferredSize(new Dimension(100, 50));
        bar.setBackground(color1);

        JMenu home = new JMenu("Home");
        JMenu match = new JMenu("Match");
        JMenu schedule = new JMenu("Schedule");
        JMenu teamsMenu = new JMenu("Teams");
        JMenu ranking = new JMenu("Ranking");

        //Admin Menu
        JMenu adminMenu = new JMenu("Admin");

        for (JMenu m : new JMenu[]{home, match, schedule, teamsMenu, ranking, adminMenu}) {
            m.setForeground(Color.WHITE);
            m.setBackground(Color.WHITE);
            m.setFont(new Font("Arial", Font.BOLD, 14));
            bar.add(Box.createRigidArea(new Dimension(10, 0)));
            bar.add(m);
        }

        // Home switch
        home.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                refreshHomeRecentMatchesUI();   // always refresh when opening Home
                switchPanel(homePanel);
            }
        });

        // Match submenu: Live / Recent
        match.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(CricketScoreFull.this,
                        "Please start a live match from the Schedule page.");
            }
        });

        // Schedule
        schedule.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                prepareScheduleData();
                switchPanel(createUpcomingPanel());
            }
        });

        // Teams
        teamsMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                switchPanel(createTeamsPanel());
            }
        });

        // Rankings
        ranking.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                switchPanel(createRankingPanel());
            }
        });

        // Admin
        adminMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new AdminLoginFrame();  // opens login window
            }
        });

        return bar;
    }
    private static class GradientPanel extends JPanel {
        private Color color1 = new Color(52, 152, 219);
        private Color color2 = new Color(41, 128, 185);

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, color1, width, height, color2);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, width, height);
        }
    }

    private JPanel createHomePanel() {
        JPanel home = new JPanel(new BorderLayout());
        home.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        home.setBackground(new Color(245, 248, 250));

        JLabel title = new JLabel("Cricket Dashboard", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));
        content.setOpaque(false);

        JPanel leftPanel = new JPanel(new BorderLayout(15, 15));
        leftPanel.setOpaque(false);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        leftPanel.setPreferredSize(new Dimension(340, 0));

        JPanel heroHeader = new JPanel(new BorderLayout());
        heroHeader.setOpaque(false);
        heroHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        heroHeader.setMaximumSize(new Dimension(340, 120));

        GradientPanel gradientBg = new GradientPanel();
        gradientBg.setLayout(new BorderLayout());
        gradientBg.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel cricketIcon = new JLabel("🏏");
        cricketIcon.setFont(new Font("Segoe UI Emoji", Font.BOLD, 48));
        cricketIcon.setHorizontalAlignment(SwingConstants.LEFT);

        JPanel titleStack = new JPanel();
        titleStack.setLayout(new BoxLayout(titleStack, BoxLayout.Y_AXIS));
        titleStack.setOpaque(false);

        JLabel mainTitle = new JLabel("Cricket Dashboard");
        mainTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        mainTitle.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Live Scores • Analytics");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(240, 248, 255));

        titleStack.add(mainTitle);
        titleStack.add(Box.createVerticalStrut(5));
        titleStack.add(subtitle);

        gradientBg.add(cricketIcon, BorderLayout.WEST);
        gradientBg.add(titleStack, BorderLayout.CENTER);
        heroHeader.add(gradientBg, BorderLayout.CENTER);

        JPanel featuresContainer = new JPanel();
        featuresContainer.setLayout(new BoxLayout(featuresContainer, BoxLayout.Y_AXIS));
        featuresContainer.setOpaque(false);

        String[][] features = {
                {"📊", "Live Scoring", "Real-time ball-by-ball updates"},
                {"📈", "Player Stats", "Detailed batting & bowling analysis"},
                {"🏆", "Match Archive", "Complete scorecard history"}
        };

        for (String[] feature : features) {
            JPanel featureCard = new JPanel(new BorderLayout(12, 12));
            featureCard.setOpaque(false);
            featureCard.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

            JPanel cardBg = new JPanel(new BorderLayout());
            cardBg.setBackground(Color.WHITE);
            cardBg.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 240, 250), 1),
                    BorderFactory.createEmptyBorder(15, 18, 15, 18)
            ));
            cardBg.setPreferredSize(new Dimension(300, 70));

            JLabel icon = new JLabel(feature[0]);
            icon.setFont(new Font("Segoe UI Emoji", Font.BOLD, 24));
            icon.setOpaque(false);
            icon.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

            JPanel textPanel = new JPanel(new BorderLayout());
            textPanel.setOpaque(false);

            JLabel titleLabel = new JLabel(feature[1]);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
            titleLabel.setForeground(new Color(52, 73, 94));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));

            JLabel descLabel = new JLabel(feature[2]);
            descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            descLabel.setForeground(new Color(127, 140, 141));

            textPanel.add(titleLabel, BorderLayout.NORTH);
            textPanel.add(descLabel, BorderLayout.SOUTH);

            cardBg.add(icon, BorderLayout.WEST);
            cardBg.add(textPanel, BorderLayout.CENTER);
            featureCard.add(cardBg);

            featuresContainer.add(featureCard);
        }

        // Description on Left Side
        featuresContainer.add(Box.createVerticalStrut(20));

        JLabel aboutTitle = new JLabel("About This Project");
        aboutTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        aboutTitle.setForeground(new Color(44, 62, 80));
        aboutTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        aboutTitle.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JTextArea aboutText = new JTextArea();
        aboutText.setEditable(false);
        aboutText.setLineWrap(true);
        aboutText.setWrapStyleWord(true);
        aboutText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        aboutText.setText(
                "A complete cricket scoring system built for university OOP project.\n\n" +
                        "Features:\n" +
                        "• Real-time live scoring with manual player selection\n" +
                        "• Detailed scorecards with batting/bowling stats\n" +
                        "• Match history with View buttons\n" +
                        "• Responsive card-based UI\n\n" +
                        "Click any match card on the right to view full scorecard!"
        );
        aboutText.setBackground(new Color(248, 249, 250));
        aboutText.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 240, 250), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        aboutText.setPreferredSize(new Dimension(300, 180));
        aboutText.setMaximumSize(new Dimension(300, 200));

        JPanel leftMainContent = new JPanel();
        leftMainContent.setLayout(new BoxLayout(leftMainContent, BoxLayout.Y_AXIS));
        leftMainContent.setOpaque(false);
        leftMainContent.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftMainContent.add(heroHeader);
        leftMainContent.add(featuresContainer);
        leftMainContent.add(aboutTitle);
        leftMainContent.add(aboutText);

        leftPanel.add(leftMainContent, BorderLayout.CENTER);

        // Recent Matches
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.setMinimumSize(new Dimension(600, 0));

        JLabel recentTitle = new JLabel("Recent Matches");
        recentTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        recentTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        grid = new JPanel(new GridLayout(4, 1, 16, 16));
        grid.setOpaque(false);

        int count = 0;
        for (Match m : matches) {
            if (count >= 4) break;
            grid.add(createMatchCard(m));
            count++;
        }
        while (count < 4) {
            JPanel empty = new JPanel();
            empty.setOpaque(false);
            grid.add(empty);
            count++;
        }

        rightPanel.add(recentTitle, BorderLayout.NORTH);
        rightPanel.add(grid, BorderLayout.CENTER);

        content.add(leftPanel);
        content.add(Box.createHorizontalStrut(20)); // gap between panels
        content.add(rightPanel);

        home.add(content, BorderLayout.CENTER);

        return home;
    }
    public void populateRecentMatchesGrid() {
        if (homePanel == null) return;
        homePanel.removeAll();
        homePanel.setLayout(new GridLayout(2, 3, 20, 20));
        homePanel.setBackground(new Color(240, 240, 240));

        int count = 0;

        for (Match m : matches) {

            JPanel card = new JPanel(new BorderLayout(0, 8)); // Added vertical gap
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createLineBorder(new Color(200,200,200), 1, true));

            JPanel top = new JPanel(new GridBagLayout());
            top.setOpaque(false);
            top.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5)); // Add padding
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 10, 5, 10);
            gbc.anchor = GridBagConstraints.CENTER;

            String[] aParts = m.scoreA.split(" ", 2);
            String scoreAVal = aParts[0];
            String oversAVal = (aParts.length > 1 ? aParts[1] : "");

            String[] bParts = m.scoreB.split(" ", 2);
            String scoreBVal = bParts[0];
            String oversBVal = (bParts.length > 1 ? bParts[1] : "");

            JPanel leftCol = new JPanel(new BorderLayout());
            leftCol.setOpaque(false);

            JLabel flagA = new JLabel(getFlagIcon(getCountryCode(m.teamA)));
            flagA.setHorizontalAlignment(SwingConstants.CENTER);

            JLabel teamA = new JLabel(m.teamA, SwingConstants.CENTER);
            teamA.setFont(new Font("Segoe UI", Font.BOLD, 14));
            teamA.setForeground(Color.GRAY);

            leftCol.add(flagA, BorderLayout.CENTER);
            leftCol.add(teamA, BorderLayout.SOUTH);

            gbc.gridx = 0;
            gbc.gridy = 0;
            top.add(leftCol, gbc);

            JPanel scoreACol = new JPanel();
            scoreACol.setOpaque(false);
            scoreACol.setLayout(new BoxLayout(scoreACol, BoxLayout.Y_AXIS));

            JLabel scoreALabel = new JLabel(scoreAVal, SwingConstants.CENTER);
            scoreALabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
            scoreALabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel oversALabel = new JLabel(oversAVal, SwingConstants.CENTER);
            oversALabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            oversALabel.setForeground(Color.GRAY);
            oversALabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            scoreACol.add(scoreALabel);
            scoreACol.add(oversALabel);

            gbc.gridx = 1;
            top.add(scoreACol, gbc);

            JPanel scoreBCol = new JPanel();
            scoreBCol.setOpaque(false);
            scoreBCol.setLayout(new BoxLayout(scoreBCol, BoxLayout.Y_AXIS));

            JLabel scoreBLabel = new JLabel(scoreBVal, SwingConstants.CENTER);
            scoreBLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
            scoreBLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel oversBLabel = new JLabel(oversBVal, SwingConstants.CENTER);
            oversBLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            oversBLabel.setForeground(Color.GRAY);
            oversBLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            scoreBCol.add(scoreBLabel);
            scoreBCol.add(oversBLabel);

            gbc.gridx = 2;
            top.add(scoreBCol, gbc);
            JPanel rightCol = new JPanel(new BorderLayout());
            rightCol.setOpaque(false);

            JLabel flagB = new JLabel(getFlagIcon(getCountryCode(m.teamB)));
            flagB.setHorizontalAlignment(SwingConstants.CENTER);

            JLabel teamB = new JLabel(m.teamB, SwingConstants.CENTER);
            teamB.setFont(new Font("Segoe UI", Font.BOLD, 14));
            teamB.setForeground(Color.GRAY);

            rightCol.add(flagB, BorderLayout.CENTER);
            rightCol.add(teamB, BorderLayout.SOUTH);

            gbc.gridx = 3;
            top.add(rightCol, gbc);

            card.add(top, BorderLayout.NORTH);

            JPanel resultPanel = new JPanel(new BorderLayout());
            resultPanel.setOpaque(false);
            resultPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

            JLabel winner = new JLabel(m.result, SwingConstants.CENTER);
            winner.setFont(new Font("Segoe UI", Font.BOLD, 12));
            winner.setForeground(new Color(0, 140, 0));

            winner.setVerticalAlignment(SwingConstants.TOP);

            resultPanel.add(winner, BorderLayout.CENTER);
            card.add(resultPanel, BorderLayout.CENTER);

            // View Button
            JButton view = new JButton("View");
            view.setFont(new Font("Segoe UI", Font.BOLD, 11));
            view.setBackground(new Color(16, 185, 129)); // Green
            view.setForeground(Color.WHITE);
            view.setFocusPainted(false);
            view.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
            view.setCursor(new Cursor(Cursor.HAND_CURSOR));

            view.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    view.setBackground(new Color(5, 150, 105));
                }
                public void mouseExited(MouseEvent e) {
                    view.setBackground(new Color(16, 185, 129));
                }
            });

            view.addActionListener(e -> new MatchSummaryFrame(m).setVisible(true));

            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
            bottom.setOpaque(false);
            bottom.add(view);

            card.add(bottom, BorderLayout.SOUTH);

            homePanel.add(card);

            count++;
            if (count == 4) break;
        }

        while (count < 4) {
            JPanel empty = new JPanel();
            empty.setBackground(new Color(245,245,245));
            empty.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));
            homePanel.add(empty);
            count++;
        }

        homePanel.revalidate();
        homePanel.repaint();
    }
    public JPanel createMatchCard(Match m) {

        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);

        JPanel top = new JPanel(new GridBagLayout());
        top.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 50, 5, 50); // <-- pushes flags farther outward

        String[] aParts = m.scoreA.split(" ", 2); // ["194/10", "(13.1 ov)"]
        String[] bParts = m.scoreB.split(" ", 2); // ["142/10", "(11.1 ov)"]

        JPanel leftCol = new JPanel(new BorderLayout());
        leftCol.setOpaque(false);

        JLabel flagA = new JLabel(getFlagIcon(getCountryCode(m.teamA)));
        flagA.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel teamA = new JLabel(m.teamA, SwingConstants.CENTER);
        teamA.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        teamA.setForeground(Color.GRAY);

        leftCol.add(flagA, BorderLayout.CENTER);
        leftCol.add(teamA, BorderLayout.SOUTH);

        gbc.gridx = 0;
        gbc.gridy = 0;
        top.add(leftCol, gbc);

        JPanel scoreACol = new JPanel();
        scoreACol.setOpaque(false);
        scoreACol.setLayout(new BoxLayout(scoreACol, BoxLayout.Y_AXIS));

        JLabel scoreA = new JLabel(aParts[0], SwingConstants.CENTER);
        scoreA.setFont(new Font("Segoe UI", Font.BOLD, 16));
        scoreA.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel oversA = new JLabel(aParts.length > 1 ? aParts[1] : "", SwingConstants.CENTER);
        oversA.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        oversA.setForeground(Color.GRAY);
        oversA.setAlignmentX(Component.CENTER_ALIGNMENT);

        scoreACol.add(scoreA);
        scoreACol.add(oversA);

        gbc.gridx = 1;
        top.add(scoreACol, gbc);

        // SCORE B COLUMN
        JPanel scoreBCol = new JPanel();
        scoreBCol.setOpaque(false);
        scoreBCol.setLayout(new BoxLayout(scoreBCol, BoxLayout.Y_AXIS));

        JLabel scoreB = new JLabel(bParts[0], SwingConstants.CENTER);
        scoreB.setFont(new Font("Segoe UI", Font.BOLD, 16));
        scoreB.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel oversB = new JLabel(bParts.length > 1 ? bParts[1] : "", SwingConstants.CENTER);
        oversB.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        oversB.setForeground(Color.GRAY);
        oversB.setAlignmentX(Component.CENTER_ALIGNMENT);

        scoreBCol.add(scoreB);
        scoreBCol.add(oversB);

        gbc.gridx = 2;
        top.add(scoreBCol, gbc);

        // RIGHT COLUMN (FLAG B + TEAM NAME)
        JPanel rightCol = new JPanel(new BorderLayout());
        rightCol.setOpaque(false);

        JLabel flagB = new JLabel(getFlagIcon(getCountryCode(m.teamB)));
        flagB.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel teamB = new JLabel(m.teamB, SwingConstants.CENTER);
        teamB.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        teamB.setForeground(Color.GRAY);

        rightCol.add(flagB, BorderLayout.CENTER);
        rightCol.add(teamB, BorderLayout.SOUTH);

        gbc.gridx = 3;
        top.add(rightCol, gbc);
        card.add(top, BorderLayout.NORTH);


        // MATCH RESULT
        JLabel resultLabel = new JLabel(m.result, SwingConstants.CENTER);
        resultLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        resultLabel.setForeground(new Color(0, 140, 0));
        card.add(resultLabel, BorderLayout.CENTER);
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel centerStack = new JPanel();
        centerStack.setLayout(new BoxLayout(centerStack, BoxLayout.Y_AXIS));
        centerStack.setOpaque(false);
        centerStack.add(top);
        centerStack.add(Box.createVerticalStrut(-8));
        centerStack.add(resultLabel);

        card.add(centerStack, BorderLayout.CENTER);

        // VIEW BUTTON
        JButton viewBtn = new JButton("View");
        viewBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        viewBtn.addActionListener(e -> new MatchSummaryFrame(m).setVisible(true));

        JPanel bottomRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottomRight.setOpaque(false);
        bottomRight.add(viewBtn);

        card.add(bottomRight, BorderLayout.SOUTH);

        return card;
    }
    private class MatchSummaryFrame extends JFrame {

        public MatchSummaryFrame(Match match) {

            // Database Call
            MatchDB.loadScorecardForMatch(match);

            setTitle("Match Summary: " + match.teamA + " vs " + match.teamB);
            setSize(850, 650);
            setLocationRelativeTo(null);

            JTabbedPane tabs = new JTabbedPane();
            tabs.add(match.teamA, createScorecardPanel(
                    match.teamA,
                    match.totalA,
                    match.battingA,
                    match.bowlingA,
                    match.result,
                    match.manOfTheMatch
            ));
            tabs.add(match.teamB, createScorecardPanel(
                    match.teamB,
                    match.totalB,
                    match.battingB,
                    match.bowlingB,
                    match.result,
                    match.manOfTheMatch
            ));

            add(tabs);
            setVisible(true);
        }

        private JPanel createScorecardPanel(String teamName, String totalScore,
                                            List<Batsman> batting,
                                            List<Bowler> bowling,
                                            String result, String mom) {

            JPanel panel = new JPanel(new BorderLayout(10,10));
            panel.setBackground(Color.WHITE);

            JPanel teamHeader = new JPanel();
            teamHeader.setLayout(new BoxLayout(teamHeader, BoxLayout.X_AXIS));
            teamHeader.setBackground(Color.WHITE);
            teamHeader.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            JLabel flag = new JLabel(getFlagIcon(getCountryCode(teamName)));
            JLabel teamLbl = new JLabel(teamName);
            teamLbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
            teamLbl.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

            JLabel scoreLbl = new JLabel(totalScore);
            scoreLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
            scoreLbl.setForeground(new Color(0,128,0));

            teamHeader.add(flag);
            teamHeader.add(teamLbl);
            teamHeader.add(Box.createHorizontalStrut(10));
            teamHeader.add(scoreLbl);

            panel.add(teamHeader, BorderLayout.NORTH);

            JPanel container = new JPanel();
            container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
            container.setBackground(Color.WHITE);

            java.util.function.Function<String, JPanel> makeHeader = (text) -> {
                JPanel header = new JPanel(new BorderLayout());
                header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
                header.setBackground(new Color(30, 144, 255));
                header.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                JLabel lbl = new JLabel(text);
                lbl.setForeground(Color.WHITE);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));

                header.add(lbl, BorderLayout.WEST);
                return header;
            };

            //  BATTING TABLE
            container.add(makeHeader.apply("Batting"));
            container.add(Box.createVerticalStrut(5));

            String[] batCols = {"Batsman", "R", "B", "4s", "6s", "SR", "Dismissal"};
            DefaultTableModel batModel = new DefaultTableModel(batCols, 0);

            for (Batsman b : batting) {
                double sr = (b.balls > 0) ? (b.runs * 100.0 / b.balls) : 0.0;

                batModel.addRow(new Object[]{
                        b.name,
                        b.runs,
                        b.balls,
                        b.fours,
                        b.sixes,
                        String.format("%.2f", sr),
                        b.dismissal
                });
            }

            JTable batTable = new JTable(batModel);
            batTable.setRowHeight(25);
            batTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            batTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
            batTable.setGridColor(new Color(220,220,220));

            container.add(new JScrollPane(batTable));
            container.add(Box.createVerticalStrut(15));

            //  BOWLING TABLE
            container.add(makeHeader.apply("Bowling"));
            container.add(Box.createVerticalStrut(5));

            String[] bowlCols = {"Bowler", "Overs", "Runs", "Wkts", "Economy"};
            DefaultTableModel bowlModel = new DefaultTableModel(bowlCols, 0);

            for (Bowler bw : bowling) {
                double eco = (bw.overs > 0) ? (bw.runsGiven / bw.overs) : 0.0;

                bowlModel.addRow(new Object[]{
                        bw.name,
                        bw.overs,
                        bw.runsGiven,
                        bw.wickets,
                        String.format("%.2f", eco)
                });
            }

            JTable bowlTable = new JTable(bowlModel);
            bowlTable.setRowHeight(25);
            bowlTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            bowlTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
            bowlTable.setGridColor(new Color(220,220,220));

            container.add(new JScrollPane(bowlTable));
            container.add(Box.createVerticalStrut(15));
            //  MATCH SUMMARY BOX
            container.add(makeHeader.apply("Match Summary"));

            JTextArea info = new JTextArea();
            info.setEditable(false);
            info.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            info.setBackground(new Color(250, 250, 250));
            info.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            info.setText(
                    "Total: " + totalScore + "\n" +
                            "Result: " + result + "\n"
            );

            container.add(info);

            panel.add(new JScrollPane(container), BorderLayout.CENTER);

            return panel;
        }
    }

    private String getCountryCode(String teamName) {
        for (Team t : teams) {
            if (t.name.equalsIgnoreCase(teamName)) {
                return t.code;
            }
        }
        return "";
    }

    public void reloadRankings() {
        List<RankingEntry> ranks = RankingDB.getRankings();

        rankings.clear();
        rankings.addAll(ranks);

        if (rankingModel == null) return;

        rankingModel.setRowCount(0);

        for (RankingEntry r : ranks) {
            rankingModel.addRow(new Object[]{
                    String.format("%02d", r.position),
                    getFlagIcon(r.countryCode),
                    r.team,
                    r.matches,
                    r.points,
                    r.rating
            });
        }
        rankingModel.fireTableDataChanged();

        if (rankingTable != null) {
            rankingTable.revalidate();
            rankingTable.repaint();
        }
    }

    private JPanel createLivePanel(Schedule sm) {

        Team teamA = null;
        Team teamB = null;

        for (Team t : teams) {
            if (t.name.trim().equalsIgnoreCase(sm.teamA.trim())) {
                teamA = t;
            }
            if (t.name.trim().equalsIgnoreCase(sm.teamB.trim())) {
                teamB = t;
            }
        }

        if (teamA == null || teamB == null) {
            JOptionPane.showMessageDialog(this,
                    "Team data not found for: " + sm.teamA + " or " + sm.teamB);
            return new JPanel(); // fail-safe
        }

        return new LiveScorePanel(sm, teamA, teamB,this);
    }


    private void prepareRankingsData() {
        rankings.clear();

        List<RankingEntry> dbRankings = RankingDB.getRankings();
        if (!dbRankings.isEmpty()) {
            rankings.addAll(dbRankings);
        }
    }

    private ImageIcon getFlagIcon(String countryCode) {
        if (countryCode == null) return null;
        countryCode = countryCode.toLowerCase();

        if (flagCache.containsKey(countryCode)) {
            return flagCache.get(countryCode);
        }

        try {
            String urlStr = "https://flagcdn.com/64x48/" + countryCode + ".png";
            URL url = new URL(urlStr);
            BufferedImage img = ImageIO.read(url);
            ImageIcon icon = new ImageIcon(img);
            flagCache.put(countryCode, icon);
            return icon;
        } catch (Exception e) {
            flagCache.put(countryCode, null);
            return null;
        }
    }

    private JPanel createRankingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(245, 248, 250));

        // Keep reference (optional but useful)
        rankingPanel = panel;

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("ICC ODI Team Rankings", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));

        header.add(title, BorderLayout.NORTH);

        panel.add(header, BorderLayout.NORTH);

        String[] cols = {"Rank", "", "Team", "Matches", "Points", "Rating"};

        rankingModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) return Icon.class; // flag column
                return Object.class;
            }
        };

        for (int i = 0; i < rankings.size(); i++) {
            RankingEntry r = rankings.get(i);
            rankingModel.addRow(new Object[]{
                    String.format("%02d", r.position),
                    getFlagIcon(r.countryCode),
                    r.team,
                    r.matches,
                    r.points,
                    r.rating
            });
        }

        rankingTable = new JTable(rankingModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                if (c instanceof JComponent) {
                    JComponent jc = (JComponent) c;

                    // Zebra striping + highlight top 3
                    if (isRowSelected(row)) {
                        jc.setBackground(new Color(0, 120, 215));
                        jc.setForeground(Color.WHITE);
                    } else {
                        if (row == 0) {           // Rank 1
                            jc.setBackground(new Color(255, 250, 210));
                        } else if (row == 1) {    // Rank 2
                            jc.setBackground(new Color(245, 245, 245));
                        } else if (row == 2) {    // Rank 3
                            jc.setBackground(new Color(240, 248, 255));
                        } else {
                            jc.setBackground(row % 2 == 0
                                    ? Color.WHITE
                                    : new Color(248, 248, 248));
                        }
                        jc.setForeground(Color.BLACK);
                    }

                    if (c instanceof JLabel label) {
                        if (column == 2) {
                            label.setHorizontalAlignment(SwingConstants.LEFT);
                        } else {
                            label.setHorizontalAlignment(SwingConstants.CENTER);
                        }
                    }
                }

                return c;
            }
        };

        rankingTable.setRowHeight(60);
        rankingTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rankingTable.setFillsViewportHeight(true);
        rankingTable.setShowHorizontalLines(true);
        rankingTable.setShowVerticalLines(false);
        rankingTable.setGridColor(new Color(230, 230, 230));
        rankingTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        rankingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader tableHeader = rankingTable.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableHeader.setBackground(new Color(20, 30, 40));
        tableHeader.setForeground(Color.WHITE);
        tableHeader.setOpaque(true);
        tableHeader.setReorderingAllowed(false);

        rankingTable.getColumnModel().getColumn(0).setPreferredWidth(60);  // Rank
        rankingTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Flag
        rankingTable.getColumnModel().getColumn(2).setPreferredWidth(180); // Team

        JScrollPane scroll = new JScrollPane(rankingTable);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210), 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }


    private JPanel createUpcomingPanel() {
        JPanel grid = new JPanel(new GridLayout(0, 3, 15, 15));
        grid.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        grid.setBackground(Color.WHITE);

        for (Schedule sm : scheduleMatches) {

            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBackground(new Color(245,245,245));
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200,200,200), 1, true),
                    BorderFactory.createEmptyBorder(10,10,10,10)
            ));

            JPanel teamRow = new JPanel(new GridLayout(1, 3));
            teamRow.setBackground(new Color(245,245,245));

            JPanel leftTeam = new JPanel();
            leftTeam.setLayout(new BoxLayout(leftTeam, BoxLayout.Y_AXIS));
            leftTeam.setBackground(new Color(245,245,245));

            JLabel flagA = new JLabel(getFlagIcon(sm.codeA));
            flagA.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel nameA = new JLabel(sm.teamA, SwingConstants.CENTER);
            nameA.setFont(new Font("Segoe UI", Font.BOLD, 14));
            nameA.setAlignmentX(Component.CENTER_ALIGNMENT);

            leftTeam.add(flagA);
            leftTeam.add(nameA);

            JLabel vsLabel = new JLabel("vs", SwingConstants.CENTER);
            vsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

            JPanel rightTeam = new JPanel();
            rightTeam.setLayout(new BoxLayout(rightTeam, BoxLayout.Y_AXIS));
            rightTeam.setBackground(new Color(245,245,245));

            JLabel flagB = new JLabel(getFlagIcon(sm.codeB));
            flagB.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel nameB = new JLabel(sm.teamB, SwingConstants.CENTER);
            nameB.setFont(new Font("Segoe UI", Font.BOLD, 14));
            nameB.setAlignmentX(Component.CENTER_ALIGNMENT);

            rightTeam.add(flagB);
            rightTeam.add(nameB);

            teamRow.add(leftTeam);
            teamRow.add(vsLabel);
            teamRow.add(rightTeam);

            JLabel teams = new JLabel(sm.teamA + " vs " + sm.teamB, SwingConstants.CENTER);
            teams.setFont(new Font("Segoe UI", Font.BOLD, 16));

            JLabel dateTime = new JLabel(sm.date + " | " + sm.time, SwingConstants.CENTER);
            dateTime.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            JLabel venue = new JLabel(sm.venue, SwingConstants.CENTER);
            venue.setFont(new Font("Segoe UI", Font.PLAIN, 13));

            JLabel format = new JLabel(sm.format, SwingConstants.CENTER);
            format.setFont(new Font("Segoe UI", Font.BOLD, 14));
            format.setForeground(new Color(0,120,210));

            card.add(teamRow);
            card.add(Box.createVerticalStrut(8));
            card.add(teams);
            card.add(dateTime);
            card.add(venue);
            card.add(Box.createVerticalStrut(5));
            card.add(format);
            // Start Match button
            JButton startBtn = new JButton();
            startBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            startBtn.setFocusPainted(false);

            if (sm.isPlayed()) {
                startBtn.setText("Match Played");
                startBtn.setEnabled(false);
                startBtn.setBackground(new Color(0, 0, 0));
                startBtn.setForeground(Color.WHITE);
            } else {
                startBtn.setText("Start Match");
                startBtn.setBackground(new Color(0, 150, 100));
                startBtn.setForeground(Color.WHITE);

                startBtn.addActionListener(e -> {
                    JPanel livePanel = createLivePanel(sm);
                    switchPanel(livePanel);
                });
            }

            card.add(Box.createVerticalStrut(5));
            card.add(startBtn);

            grid.add(card);
        }
        JScrollPane scrollPane = new JScrollPane(grid);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.add(scrollPane, BorderLayout.CENTER);

        return wrapper;
    }


    private void prepareSampleData() {

        matches.clear();

        ArrayList<Match> dbMatches = (ArrayList<Match>) MatchDB.getLastMatches(6);

        matches.addAll(dbMatches);

        int count = dbMatches.size();

        if (count < 6) {

            Match m1 = new Match("1", "Pakistan", "Zimbabwe",
                    "195/5 (20 ov)", "126 (19.0 ov)", "Pakistan won by 69 runs", false);

            if (matches.size() < 6) matches.add(m1);

            Match m2 = new Match("2", "Australia", "India",
                    "178/6 (20 ov)", "170/8 (20 ov)", "Australia won by 8 runs", false);
            if (matches.size() < 6) matches.add(m2);

            Match m3 = new Match("3", "South Africa", "New Zealand",
                    "178/7 (20 ov)", "180/3 (19.1 ov)", "New Zealand won by 7 wickets", false);
            if (matches.size() < 6) matches.add(m3);

            Match m4 = new Match("4", "Sri Lanka", "Bangladesh",
                    "143/9 (20 ov)", "145/6 (19.4 ov)", "Bangladesh won by 4 wickets", false);
            if (matches.size() < 6) matches.add(m4);

            Match m5 = new Match("5", "West Indies", "Zimbabwe",
                    "195/5 (20 ov)", "126 (19 ov)", "West Indies won by 69 runs", false);
            if (matches.size() < 6) matches.add(m5);

            Match m6 = new Match("6", "Afghanistan", "Ireland",
                    "167/5 (20 ov)", "155/7 (20 ov)", "Afghanistan won by 12 runs", false);
            if (matches.size() < 6) matches.add(m6);
        }
    }

    public void refreshHomeRecentMatchesUI() {
        prepareSampleData();

        SwingUtilities.invokeLater(() -> {
            if (grid == null) return;

            grid.removeAll();

            int count = 0;
            for (Match m : matches) {
                if (count >= 4) break;
                grid.add(createMatchCard(m));
                count++;
            }

            while (count < 4) {
                JPanel empty = new JPanel();
                empty.setOpaque(false);
                grid.add(empty);
                count++;
            }

            grid.revalidate();
            grid.repaint();
        });
    }

    private void prepareScheduleData() {
        scheduleMatches.clear();
        scheduleMatches.addAll(ScheduleDB.loadSchedule());
    }

    private JPanel createTeamsPanel() {

        JPanel panel = new JPanel(new GridLayout(0, 3, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(Color.WHITE);

        for (Team t : teams) {

            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBackground(new Color(245, 245, 245));
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));

            JLabel flag = new JLabel(getFlagIcon(t.code));
            flag.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel name = new JLabel(t.name, SwingConstants.CENTER);
            name.setFont(new Font("Segoe UI", Font.BOLD, 16));
            name.setAlignmentX(Component.CENTER_ALIGNMENT);

            card.add(flag);
            card.add(Box.createVerticalStrut(8));
            card.add(name);
            //Team Detail Players List
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    switchPanel(createTeamDetailsPanel(t));
                }
            });

            panel.add(card);
        }
        return panel;
    }
    private void prepareTeamsData() {

        teams.clear();

        Map<String, Team> dbTeams = TeamDB.loadTeamsByName();

        Map<String, Map<String, ArrayList<String>>> dbPlayers = PlayerDB.loadPlayersGrouped();

        for (String teamName : dbTeams.keySet()) {

            Team t = dbTeams.get(teamName);

            t.batsmen = new ArrayList<>();
            t.bowlers = new ArrayList<>();
            t.allRounders = new ArrayList<>();

            if (dbPlayers.containsKey(teamName)) {
                Map<String, ArrayList<String>> p = dbPlayers.get(teamName);

                t.batsmen      = p.getOrDefault("BAT",  new ArrayList<>());
                t.bowlers      = p.getOrDefault("BOWL", new ArrayList<>());
                t.allRounders  = p.getOrDefault("AR",   new ArrayList<>());
            }
            teams.add(t);
        }
    }


    private JPanel createTeamDetailsPanel(Team t) {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel(t.name + " - Player Squad", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        panel.add(title, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        java.util.function.BiConsumer<String, List<String>> addSection = (header, players) -> {

            JLabel lbl = new JLabel(header);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
            lbl.setBorder(BorderFactory.createEmptyBorder(12, 0, 6, 0));
            listPanel.add(lbl);

            JPanel box = new JPanel();
            box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
            box.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
            box.setBackground(Color.WHITE);

            for (String p : players) {
                JLabel pl = new JLabel("• " + p);
                pl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                pl.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                box.add(pl);
            }

            listPanel.add(box);
        };

        addSection.accept("Batsmen", t.batsmen);
        addSection.accept("Bowlers", t.bowlers);
        addSection.accept("All-Rounders", t.allRounders);
        panel.add(new JScrollPane(listPanel), BorderLayout.CENTER);
        return panel;
    }
}
