package cricket;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EditTeamFrame extends JFrame {

    private JComboBox<String> teamDrop;
    private JTextField[] playerFields;
    private JComboBox<String>[] roleDrops;

    public EditTeamFrame() {
        setTitle("Update Team Players");
        setSize(650, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        top.add(new JLabel("Select Team:"));

        teamDrop = new JComboBox<>(TeamDB.getAllTeamNames().toArray(new String[0]));
        teamDrop.setPreferredSize(new Dimension(250, 30));
        top.add(teamDrop);

        JButton btnLoad = new JButton("Load Players");
        top.add(btnLoad);

        root.add(top, BorderLayout.NORTH);

        // Center (players list)
        JPanel form = new JPanel(new GridLayout(12, 3, 10, 10));
        form.add(new JLabel("#"));
        form.add(new JLabel("Player Name"));
        form.add(new JLabel("Role"));

        playerFields = new JTextField[11];
        roleDrops = new JComboBox[11];

        String[] roles = {"Batsman", "Bowler", "All-Rounder"};

        for (int i = 0; i < 11; i++) {
            form.add(new JLabel(String.valueOf(i+1)));
            playerFields[i] = new JTextField();
            roleDrops[i] = new JComboBox<>(roles);

            form.add(playerFields[i]);
            form.add(roleDrops[i]);
        }

        root.add(new JScrollPane(form), BorderLayout.CENTER);

        JButton btnSave = new JButton("Save Changes");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        root.add(btnSave, BorderLayout.SOUTH);

        setContentPane(root);

        btnLoad.addActionListener(e -> loadPlayers());
        btnSave.addActionListener(e -> savePlayers());
    }

    private void loadPlayers() {
        String teamName = (String) teamDrop.getSelectedItem();
        if (teamName == null) return;

        int teamId = TeamDB.getTeamIdByName(teamName);
        List<TeamDB.PlayerRow> players = TeamDB.getPlayersByTeamId(teamId);

        for (int i = 0; i < 11; i++) {
            playerFields[i].setText("");
            roleDrops[i].setSelectedIndex(0);
        }

        // fill
        for (int i = 0; i < players.size() && i < 11; i++) {
            TeamDB.PlayerRow p = players.get(i);
            playerFields[i].setText(p.name);

            if ("Bowler".equalsIgnoreCase(p.role)) roleDrops[i].setSelectedItem("Bowler");
            else if ("All-Rounder".equalsIgnoreCase(p.role) || "All-Rounders".equalsIgnoreCase(p.role))
                roleDrops[i].setSelectedItem("All-Rounder");
            else roleDrops[i].setSelectedItem("Batsman");
        }
    }

    private void savePlayers() {
        String teamName = (String) teamDrop.getSelectedItem();
        if (teamName == null) return;

        int teamId = TeamDB.getTeamIdByName(teamName);

        // validate (at least 11 names)
        for (int i = 0; i < 11; i++) {
            if (playerFields[i].getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all 11 player names.");
                return;
            }
        }
        TeamDB.deletePlayersByTeamId(teamId);

        for (int i = 0; i < 11; i++) {
            String name = playerFields[i].getText().trim();
            String role = (String) roleDrops[i].getSelectedItem();
            TeamDB.insertPlayer(teamId, name, role);
        }

        JOptionPane.showMessageDialog(this, "Players updated successfully!");
    }
}
