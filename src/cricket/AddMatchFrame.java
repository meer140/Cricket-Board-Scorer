package cricket;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AddMatchFrame extends JFrame {

    private JComboBox<String> cbTeamA, cbTeamB;
    private JTextField tfCodeA, tfCodeB, tfDate, tfTime, tfVenue, tfFormat;

    public AddMatchFrame() {

        setTitle("Add New Match");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 245));  // soft background

        JLabel lblTitle = new JLabel("Add New Match", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(70, 130, 180));   // steel blue
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(lblTitle, BorderLayout.NORTH);

        // MAIN PANEL
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(new Color(255, 255, 255));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,200,200)),
                BorderFactory.createEmptyBorder(20,20,20,20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        List<String> teams = TeamDB.getAllTeamNames();

        cbTeamA = new JComboBox<>(teams.toArray(new String[0]));
        cbTeamB = new JComboBox<>(teams.toArray(new String[0]));

        tfCodeA = new JTextField();
        tfCodeB = new JTextField();
        tfDate = new JTextField();
        tfTime = new JTextField();
        tfVenue = new JTextField();
        tfFormat = new JTextField();

        addField(formPanel, gbc, "Team A:", cbTeamA, 0);
        addField(formPanel, gbc, "Code A:", tfCodeA, 1);
        addField(formPanel, gbc, "Team B:", cbTeamB, 2);
        addField(formPanel, gbc, "Code B:", tfCodeB, 3);
        addField(formPanel, gbc, "Date (YYYY-MM-DD):", tfDate, 4);
        addField(formPanel, gbc, "Time (XX : XX PM/AM):", tfTime, 5);
        addField(formPanel, gbc, "Venue:", tfVenue, 6);
        addField(formPanel, gbc, "Format (ODI/T20/Test):", tfFormat, 7);

        add(formPanel, BorderLayout.CENTER);

        // BUTTON PANEL
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(245, 245, 245));

        JButton btnAdd = new JButton("Save Match");
        JButton btnCancel = new JButton("Cancel");

        styleButton(btnAdd, new Color(46, 204, 113));   // green
        styleButton(btnCancel, new Color(231, 76, 60)); // red

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnCancel);

        add(buttonPanel, BorderLayout.SOUTH);

        // AUTO-FILL CODES
        cbTeamA.addActionListener(e -> {
            String t = (String) cbTeamA.getSelectedItem();
            tfCodeA.setText(TeamDB.getCodeByTeamName(t));
        });

        cbTeamB.addActionListener(e -> {
            String t = (String) cbTeamB.getSelectedItem();
            tfCodeB.setText(TeamDB.getCodeByTeamName(t));
        });

        // BUTTON ACTIONS LISTENER
        btnAdd.addActionListener(e -> saveMatch());
        btnCancel.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, String label, JComponent comp, int y) {
        gbc.gridx = 0; gbc.gridy = y;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 15));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        comp.setFont(new Font("SansSerif", Font.PLAIN, 15));
        panel.add(comp, gbc);
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    }

    private void saveMatch() {
        String teamA = (String) cbTeamA.getSelectedItem();
        String teamB = (String) cbTeamB.getSelectedItem();

        if (teamA.equals(teamB)) {
            JOptionPane.showMessageDialog(this,
                    "Team A and Team B must be different.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String codeA = tfCodeA.getText().trim();
        String codeB = tfCodeB.getText().trim();
        String date = tfDate.getText().trim();
        String time = tfTime.getText().trim();
        String venue = tfVenue.getText().trim();
        String format = tfFormat.getText().trim();

        if (date.isEmpty() || time.isEmpty() || venue.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Date, Time, and Venue are required.",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Schedule s = new Schedule(teamA, codeA, teamB, codeB, date, time, venue, format);
        int newId = ScheduleDB.addMatch(s);

        if (newId > 0) {
            JOptionPane.showMessageDialog(this,
                    "Match saved (ID: " + newId + ").",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to save match.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
