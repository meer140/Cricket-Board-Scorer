package cricket;
import java.sql.*;

public class AdminDB {

    public static boolean authenticate(String username, String password) {

        String sql = "SELECT * FROM Admin WHERE Username = ? AND Password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
