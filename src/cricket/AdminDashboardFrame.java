package cricket;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdminDashboardFrame extends JFrame {

    public AdminDashboardFrame() {

        setTitle("Admin Dashboard");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);


        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(15, 23, 42)); // dark bg
        root.setBorder(new EmptyBorder(18, 18, 18, 18));

        // ===== Header (Gradient Card) =====
        JPanel header = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(59, 130, 246),
                        getWidth(), getHeight(), new Color(37, 99, 235)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel title = new JLabel("👑 Admin Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Manage teams, schedule matches, and generate reports");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(219, 234, 254));

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(6));
        titleBox.add(subtitle);

        header.add(titleBox, BorderLayout.WEST);
        root.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(18, 0, 0, 0));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(22, 22, 22, 22));

        JPanel grid = new JPanel(new GridLayout(3, 2, 16, 16));
        grid.setOpaque(false);

        JButton addTeamBtn = modernActionButton("➕ Add Team (With Players)", "Create a team + squad", new Color(255, 130, 246));
        JButton addMatchBtn = modernActionButton("🎯 Add Match", "Create schedule match", new Color(139, 92, 246));
        JButton updateTeamBtn = modernActionButton("✏️ Update Team Players", "Edit squad members", new Color(34, 197, 94));
        JButton deleteTeamBtn = modernActionButton("🗑️ Delete Team", "Remove team from system", new Color(239, 68, 68));
        JButton reportBtn = modernActionButton("📈 Team Report", "Top scorers & wicket takers", new Color(234, 179, 8));

        // Actions
        addTeamBtn.addActionListener(e -> new AddTeamFrame());
        addMatchBtn.addActionListener(e -> new AddMatchFrame());
        updateTeamBtn.addActionListener(e -> new EditTeamFrame().setVisible(true));
        deleteTeamBtn.addActionListener(e -> new DeleteTeamFrame().setVisible(true));
        reportBtn.addActionListener(e -> new TeamReportFrame().setVisible(true));

        grid.add(addTeamBtn);
        grid.add(addMatchBtn);
        grid.add(updateTeamBtn);
        grid.add(deleteTeamBtn);
        grid.add(reportBtn);
        grid.add(makeInfoPanel());

        card.add(grid, BorderLayout.CENTER);
        center.add(card, BorderLayout.CENTER);

        root.add(center, BorderLayout.CENTER);

        setContentPane(root);
        setVisible(true);
    }

    private JButton modernActionButton(String title, String desc, Color accent) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout());
        btn.setBackground(new Color(248, 250, 252));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 15));
        t.setForeground(new Color(15, 23, 42));

        JLabel d = new JLabel(desc);
        d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        d.setForeground(new Color(100, 116, 139));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setBorder(new EmptyBorder(14, 14, 14, 14));
        text.add(t);
        text.add(Box.createVerticalStrut(6));
        text.add(d);

        JPanel bar = new JPanel();
        bar.setPreferredSize(new Dimension(8, 10));
        bar.setBackground(accent);

        btn.add(bar, BorderLayout.WEST);
        btn.add(text, BorderLayout.CENTER);

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(241, 245, 249));
                btn.setBorder(BorderFactory.createLineBorder(accent, 2));
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(248, 250, 252));
                btn.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
            }
        });

        return btn;
    }

    private JComponent makeInfoPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(15, 23, 42));
        p.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel h = new JLabel("Tips");
        h.setFont(new Font("Segoe UI", Font.BOLD, 14));
        h.setForeground(Color.WHITE);

        JLabel t = new JLabel("<html>• Add team first<br>• Then schedule matches<br>• Generate reports after matches</html>");
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.setForeground(new Color(203, 213, 225));

        p.add(h, BorderLayout.NORTH);
        p.add(t, BorderLayout.CENTER);

        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 41, 59), 1),
                new EmptyBorder(14, 14, 14, 14)
        ));

        return p;
    }
}
