package server;

import java.io.*;
import java.net.Socket;
import java.sql.*;
import db.DBConnectionBackup;

public class BackupHandler extends Thread {

    private Socket socket;

    public BackupHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));

                PrintWriter out = new PrintWriter(
                        socket.getOutputStream(), true)) {

            String request;

            while ((request = in.readLine()) != null) {

                String command = request.trim();
                System.out.println("📩 Backup Received: " + command);

                // ================= REPLICATION MESSAGES =================
                if (command.startsWith("REPLICATE_ADD")) {

                    String[] parts = command.split("\\|");

                    if (parts.length < 5)
                        continue;

                    String title = parts[1].trim();
                    String desc = parts[2].trim();
                    String status = parts[3].trim();
                    String user = parts[4].trim();

                    try {
                        Connection conn = DBConnectionBackup.getConnection();

                        String sql = "INSERT INTO tasks(title, description, status, assigned_to) VALUES(?,?,?,?)";
                        PreparedStatement ps = conn.prepareStatement(sql);

                        ps.setString(1, title);
                        ps.setString(2, desc);
                        ps.setString(3, status);
                        ps.setString(4, user);

                        ps.executeUpdate();
                        System.out.println("✅ Backup: Task replicated");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                else if (command.startsWith("REPLICATE_DELETE")) {

                    String[] parts = command.split("\\|");

                    if (parts.length < 2)
                        continue;

                    try {
                        Connection conn = DBConnectionBackup.getConnection();

                        String sql = "DELETE FROM tasks WHERE id=?";
                        PreparedStatement ps = conn.prepareStatement(sql);

                        ps.setString(1, parts[1]);

                        ps.executeUpdate();
                        System.out.println("✅ Backup: Task deleted");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                else if (command.startsWith("REPLICATE_UPDATE")) {

                    String[] parts = command.split("\\|");

                    if (parts.length < 5)
                        continue;

                    String id = parts[1].trim();
                    String title = parts[2].trim();
                    String desc = parts[3].trim();
                    String status = parts[4].trim();

                    try {
                        Connection conn = DBConnectionBackup.getConnection();

                        String sql = "UPDATE tasks SET title=?, description=?, status=? WHERE id=?";
                        PreparedStatement ps = conn.prepareStatement(sql);

                        ps.setString(1, title);
                        ps.setString(2, desc);
                        ps.setString(3, status);
                        ps.setString(4, id);

                        ps.executeUpdate();
                        System.out.println("✅ Backup: Task updated");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // ================= CLIENT MESSAGES (same as main) =================
                else if (command.startsWith("LOGIN")) {

                    String[] parts = command.split("\\|");

                    if (parts.length < 3) {
                        out.println("FAILED");
                        continue;
                    }

                    try {
                        Connection conn = DBConnectionBackup.getConnection();

                        String sql = "SELECT * FROM users WHERE username=? AND password=?";
                        PreparedStatement ps = conn.prepareStatement(sql);

                        ps.setString(1, parts[1]);
                        ps.setString(2, parts[2]);

                        ResultSet rs = ps.executeQuery();

                        if (rs.next()) {
                            out.println("SUCCESS");
                        } else {
                            out.println("FAILED");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        out.println("FAILED");
                    }
                }

                else if (command.startsWith("ADD_TASK")) {

                    String[] parts = command.split("\\|");

                    if (parts.length < 5) {
                        out.println("TASK_FAILED");
                        continue;
                    }

                    String title = parts[1].trim();
                    String desc = parts[2].trim();
                    String status = parts[3].trim();
                    String user = parts[4].trim();

                    if (title.isEmpty() || desc.isEmpty() || status.isEmpty() || user.isEmpty()) {
                        out.println("TASK_FAILED_EMPTY");
                        continue;
                    }

                    try {
                        Connection conn = DBConnectionBackup.getConnection();

                        String sql = "INSERT INTO tasks(title, description, status, assigned_to) VALUES(?,?,?,?)";
                        PreparedStatement ps = conn.prepareStatement(sql);

                        ps.setString(1, title);
                        ps.setString(2, desc);
                        ps.setString(3, status);
                        ps.setString(4, user);

                        int row = ps.executeUpdate();

                        if (row > 0) {
                            out.println("TASK_ADDED");
                        } else {
                            out.println("TASK_FAILED");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        out.println("TASK_FAILED");
                    }
                }

                else if (command.startsWith("DELETE_TASK")) {

                    String[] parts = command.split("\\|");

                    if (parts.length < 2) {
                        out.println("DELETE_FAILED");
                        continue;
                    }

                    try {
                        Connection conn = DBConnectionBackup.getConnection();

                        String sql = "DELETE FROM tasks WHERE id=?";
                        PreparedStatement ps = conn.prepareStatement(sql);

                        ps.setString(1, parts[1]);

                        int row = ps.executeUpdate();

                        if (row > 0) {
                            out.println("TASK_DELETED");
                        } else {
                            out.println("DELETE_FAILED");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        out.println("DELETE_FAILED");
                    }
                }

                else if (command.startsWith("UPDATE_TASK")) {

                    String[] parts = command.split("\\|");

                    if (parts.length < 5) {
                        out.println("UPDATE_FAILED");
                        continue;
                    }

                    String id = parts[1].trim();
                    String title = parts[2].trim();
                    String desc = parts[3].trim();
                    String status = parts[4].trim();

                    if (id.isEmpty() || title.isEmpty() || desc.isEmpty() || status.isEmpty()) {
                        out.println("UPDATE_FAILED_EMPTY");
                        continue;
                    }

                    try {
                        Connection conn = DBConnectionBackup.getConnection();

                        String sql = "UPDATE tasks SET title=?, description=?, status=? WHERE id=?";
                        PreparedStatement ps = conn.prepareStatement(sql);

                        ps.setString(1, title);
                        ps.setString(2, desc);
                        ps.setString(3, status);
                        ps.setString(4, id);

                        int rows = ps.executeUpdate();

                        if (rows > 0) {
                            out.println("TASK_UPDATED");
                        } else {
                            out.println("UPDATE_FAILED");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        out.println("UPDATE_FAILED");
                    }
                }

                else if (command.equals("VIEW_TASKS")) {

                    try {
                        Connection conn = DBConnectionBackup.getConnection();

                        String sql = "SELECT * FROM tasks";
                        PreparedStatement ps = conn.prepareStatement(sql);

                        ResultSet rs = ps.executeQuery();

                        StringBuilder response = new StringBuilder("TASKS");

                        while (rs.next()) {
                            response.append("|")
                                    .append(rs.getInt("id")).append(",")
                                    .append(rs.getString("title")).append(",")
                                    .append(rs.getString("description")).append(",")
                                    .append(rs.getString("status")).append(",")
                                    .append(rs.getString("assigned_to"));
                        }

                        out.println(response.toString());

                    } catch (Exception e) {
                        e.printStackTrace();
                        out.println("TASKS|EMPTY");
                    }
                }

                else {
                    out.println("UNKNOWN_COMMAND");
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Backup client disconnected: " + e.getMessage());
        }
    }
}