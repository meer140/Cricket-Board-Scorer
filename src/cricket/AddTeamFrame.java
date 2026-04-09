package cricket;

import javax.swing.*;
import java.awt.*;

public class AddTeamFrame extends JFrame {

    public AddTeamFrame() {

        setTitle("Add New Team");
        setSize(520, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JTextField tfTeamName = new JTextField();
        JTextField tfShortName = new JTextField();
        JTextField tfCountryCode = new JTextField();

        panel.add(new JLabel("Team Name:"));
        panel.add(tfTeamName);

        panel.add(new JLabel("Short Name (IND, PAK, AUS):"));
        panel.add(tfShortName);

        panel.add(new JLabel("Country Code (in, pk, au):"));
        panel.add(tfCountryCode);

        JTextField[] playerFields = new JTextField[11];
        JComboBox<String>[] roleBoxes = new JComboBox[11];
        String[] roles = {"BAT", "BOWL", "AR", "WK"};

        for (int i = 0; i < 11; i++) {
            playerFields[i] = new JTextField();
            roleBoxes[i] = new JComboBox<>(roles);
            roleBoxes[i].setSelectedItem("BAT");

            panel.add(new JLabel("Player " + (i + 1) + " Name:"));
            panel.add(playerFields[i]);

            panel.add(new JLabel("Player " + (i + 1) + " Role:"));
            panel.add(roleBoxes[i]);
        }

        JButton btnSave = new JButton("Save Team");
        JButton btnCancel = new JButton("Cancel");

        btnSave.addActionListener(e -> {

            String teamName = tfTeamName.getText().trim();
            String shortName = tfShortName.getText().trim(); // not used unless you add column in Team table later
            String countryCode = tfCountryCode.getText().trim().toLowerCase();

            if (teamName.isEmpty() || shortName.isEmpty() || countryCode.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill Team Name, Short Name and Country Code.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String flagPath = countryCode + ".png";

            try {
                int teamId = TeamDB.insertTeam(teamName, countryCode);

                if (teamId <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "Team could not be saved (teamId not generated).",
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                for (int i = 0; i < 11; i++) {
                    String playerName = playerFields[i].getText().trim();
                    if (!playerName.isEmpty()) {
                        String role = (String) roleBoxes[i].getSelectedItem();
                        PlayerDB.insertPlayer(teamId, playerName, role);
                    }
                }

                JOptionPane.showMessageDialog(this,
                        "Team and players added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error saving team.\n" + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dispose());

        panel.add(btnSave);
        panel.add(btnCancel);

        add(new JScrollPane(panel));
        setVisible(true);
    }
}
