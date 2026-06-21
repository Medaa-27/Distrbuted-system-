package server;

import db.DBConnection;
import java.sql.*;

public class TaskManager {

    public static boolean addTask(String title, String desc, String status, String user) {
        try {
            Connection conn = DBConnection.getConnection();

            String sql = "INSERT INTO tasks (title, description, status, assigned_to) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, title);
            ps.setString(2, desc);
            ps.setString(3, status);
            ps.setString(4, user);

            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ NEW: View tasks
    public static String getAllTasks() {
        StringBuilder result = new StringBuilder();

        try {
            Connection conn = DBConnection.getConnection();

            String sql = "SELECT * FROM tasks";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                result.append(
                        rs.getInt("id") + " | " +
                                rs.getString("title") + " | " +
                                rs.getString("description") + " | " +
                                rs.getString("status") + " | " +
                                rs.getString("assigned_to") + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error fetching tasks";
        }

        return result.toString();
    }
}