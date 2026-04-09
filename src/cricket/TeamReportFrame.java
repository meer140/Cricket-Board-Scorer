package cricket;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TeamReportFrame extends JFrame {

    private JComboBox<String> teamDrop;
    private DefaultTableModel scorerModel;
    private DefaultTableModel wicketModel;

    public TeamReportFrame() {
        setTitle("Team Report");
        setSize(750, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        top.add(new JLabel("Select Team:"));

        teamDrop = new JComboBox<>(TeamDB.getAllTeamNames().toArray(new String[0]));
        teamDrop.setPreferredSize(new Dimension(260, 30));
        top.add(teamDrop);

        JButton btnGen = new JButton("Generate Report");
        top.add(btnGen);

        root.add(top, BorderLayout.NORTH);

        // Tables
        scorerModel = new DefaultTableModel(new String[]{"Player", "Runs"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        wicketModel = new DefaultTableModel(new String[]{"Player", "Wickets"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable scorerTable = new JTable(scorerModel);
        JTable wicketTable = new JTable(wicketModel);

        JPanel tables = new JPanel(new GridLayout(1, 2, 12, 12));
        tables.add(wrapTable("Top Scorers", scorerTable));
        tables.add(wrapTable("Top Wicket Takers", wicketTable));

        root.add(tables, BorderLayout.CENTER);

        setContentPane(root);

        btnGen.addActionListener(e -> loadReport());
    }

    private JPanel wrapTable(String title, JTable table) {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 14));
        p.add(t, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private void loadReport() {
        String teamName = (String) teamDrop.getSelectedItem();
        if (teamName == null || teamName.trim().isEmpty()) return;

        scorerModel.setRowCount(0);
        wicketModel.setRowCount(0);

        List<String[]> scorers = ReportDB.getTopScorers(teamName, 5);
        for (String[] row : scorers) {
            scorerModel.addRow(new Object[]{row[0], row[1]});
        }

        List<String[]> wickets = ReportDB.getTopWicketTakers(teamName, 5);
        for (String[] row : wickets) {
            wicketModel.addRow(new Object[]{row[0], row[1]});
        }

        if (scorers.isEmpty() && wickets.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No report data found for: " + teamName + "\n(Play some matches first)",
                    "No Data",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

}
