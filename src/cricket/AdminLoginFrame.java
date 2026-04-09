package cricket;
import javax.swing.*;
import java.awt.*;

public class AdminLoginFrame extends JFrame {

    public AdminLoginFrame() {
        setTitle("Admin Login");
        setSize(350, 250);
        setLayout(new GridBagLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 10);

        JLabel userLbl = new JLabel("Username:");
        JLabel passLbl = new JLabel("Password:");

        JTextField username = new JTextField(15);
        JPasswordField password = new JPasswordField(15);

        JButton loginBtn = new JButton("Login");

        gc.gridx = 0; gc.gridy = 0; add(userLbl, gc);
        gc.gridx = 1; add(username, gc);

        gc.gridx = 0; gc.gridy = 1; add(passLbl, gc);
        gc.gridx = 1; add(password, gc);

        gc.gridx = 1; gc.gridy = 2;
        add(loginBtn, gc);

        loginBtn.addActionListener(e -> {
            String u = username.getText();
            String p = new String(password.getPassword());

            if (AdminDB.authenticate(u, p)) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                dispose();
                new AdminDashboardFrame();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Credentials!");
            }
        });

        setVisible(true);
    }
}
