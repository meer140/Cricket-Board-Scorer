package cricket;

import javax.swing.*;
import java.awt.*;

public class DeleteTeamFrame extends JFrame {

    private JComboBox<String> teamDrop;

    public DeleteTeamFrame() {
        setTitle("Delete Team");
        setSize(450, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        JPanel center = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        center.add(new JLabel("Select Team:"));

        teamDrop = new JComboBox<>(TeamDB.getAllTeamNames().toArray(new String[0]));
        teamDrop.setPreferredSize(new Dimension(250, 30));
        center.add(teamDrop);

        root.add(center, BorderLayout.CENTER);

        JButton btnDelete = new JButton("Delete Team");
        btnDelete.setBackground(new Color(239, 68, 68));
        btnDelete.setForeground(Color.WHITE);

        root.add(btnDelete, BorderLayout.SOUTH);

        setContentPane(root);

        btnDelete.addActionListener(e -> doDelete());
    }

    private void doDelete() {
        String teamName = (String) teamDrop.getSelectedItem();
        if (teamName == null) return;

        int teamId = TeamDB.getTeamIdByName(teamName);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete team: " + teamName + " ?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = TeamDB.deleteTeamById(teamId);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Team deleted successfully!");
            teamDrop.removeItem(teamName);
        } else {
            JOptionPane.showMessageDialog(this, "Delete failed.");
        }
    }
}
