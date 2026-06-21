package server;

import java.io.*;
import java.net.Socket;
import java.sql.*;
import db.DBConnection;
import db.DBHelper;

public class ClientHandler extends Thread {

    private Socket socket;

    public ClientHandler(Socket socket) {
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
                System.out.println("📩 Received: " + command);

                // ================= LOGIN =================
                if (command.startsWith("LOGIN")) {

                    String[] parts = command.split("\\|");

                    if (parts.length < 3) {
                        out.println("FAILED");
                        continue;
                    }

                    try {
                        Connection conn = DBConnection.getConnection();
                        if (conn == null) {
                            out.println("SERVER_UNAVAILABLE");
                            continue;
                        }

                        String sql = "SELECT * FROM users WHERE username=? AND password=?";
                        try (PreparedStatement ps = conn.prepareStatement(sql)) {
                            ps.setString(1, parts[1]);
                            ps.setString(2, parts[2]);
                            try (ResultSet rs = ps.executeQuery()) {
                                if (rs.next()) {
                                    out.println("SUCCESS");
                                } else {
                                    out.println("FAILED");
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        out.println("SERVER_UNAVAILABLE");
                    }
                }

                // ================= REGISTER =================
                else if (command.startsWith("REGISTER")) {

                    String[] parts = command.split("\\|");

                    if (parts.length < 3) {
                        out.println("REGISTER_FAILED");
                        continue;
                    }

                    String username = parts[1].trim();
                    String password = parts[2].trim();

                    if (username.isEmpty() || password.isEmpty()) {
                        out.println("REGISTER_FAILED");
                        continue;
                    }

                    try {
                        DBHelper.DBResult r = DBHelper.registerUser(username, password);
                        switch (r) {
                            case USER_EXISTS:
                                out.println("USER_EXISTS");
                                break;
                            case SUCCESS:
                                out.println("REGISTER_SUCCESS");
                                System.out.println("✅ New user registered: " + username);
                                break;
                            case SERVER_UNAVAILABLE:
                                out.println("SERVER_UNAVAILABLE");
                                break;
                            default:
                                out.println("REGISTER_FAILED");
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        out.println("REGISTER_FAILED");
                    }
                }

                else if (command.startsWith("ADD_TASK")) {

                    String[] parts = command.split("\\|");

                    if (parts.length < 7) {
                        out.println("TASK_FAILED");
                        continue;
                    }

                    String title = parts[1].trim();
                    String desc = parts[2].trim();
                    String status = parts[3].trim();
                    String user = parts[4].trim();
                    String priority = parts[5].trim();
                    String dueDate = parts[6].trim();

                    // ================= VALIDATION =================
                    if (title.isEmpty() || desc.isEmpty() ||
                            status.isEmpty() || user.isEmpty() || priority.isEmpty() || dueDate.isEmpty()) {

                        out.println("TASK_FAILED_EMPTY");
                        System.out.println("❌ Empty task rejected");
                        continue;
                    }

                    try {
                        java.sql.Date sqlDueDate;
                        try {
                            sqlDueDate = java.sql.Date.valueOf(dueDate);
                        } catch (IllegalArgumentException invalidDate) {
                            out.println("TASK_FAILED");
                            System.out.println("❌ Invalid due date: " + dueDate);
                            continue;
                        }

                        try {
                            DBHelper.DBResult r = DBHelper.insertTask(title, desc, status, user, priority, sqlDueDate);
                            switch (r) {
                                case SUCCESS:
                                    out.println("TASK_ADDED");
                                    System.out.println("✅ Task inserted");
                                    ReplicationManager.sendReplication(
                                            "REPLICATE_ADD|" + title + "|" + desc + "|" + status + "|" + user);
                                    break;
                                case SERVER_UNAVAILABLE:
                                    out.println("SERVER_UNAVAILABLE");
                                    break;
                                default:
                                    out.println("TASK_FAILED");
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            out.println("TASK_FAILED");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        out.println("TASK_FAILED");
                    }
                }
                // ================= DELETE TASK =================
                else if (command.startsWith("DELETE_TASK")) {

                    String[] parts = command.split("\\|");

                    if (parts.length < 2) {
                        out.println("DELETE_FAILED");
                        continue;
                    }

                    try {
                        DBHelper.DBResult r = DBHelper.deleteTask(parts[1]);
                        switch (r) {
                            case SUCCESS:
                                out.println("TASK_DELETED");
                                ReplicationManager.sendReplication("REPLICATE_DELETE|" + parts[1]);
                                break;
                            case SERVER_UNAVAILABLE:
                                out.println("SERVER_UNAVAILABLE");
                                break;
                            default:
                                out.println("DELETE_FAILED");
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        out.println("DELETE_FAILED");
                    }
                }

                // ================= UPDATE TASK =================
                else if (command.startsWith("UPDATE_TASK")) {

                    String[] parts = command.split("\\|");

                    if (parts.length < 7) {
                        out.println("UPDATE_FAILED");
                        continue;
                    }

                    String id = parts[1].trim();
                    String title = parts[2].trim();
                    String desc = parts[3].trim();
                    String status = parts[4].trim();
                    String priority = parts[5].trim();
                    String dueDate = parts[6].trim();

                    // ✅ VALIDATION
                    if (id.isEmpty() || title.isEmpty() || desc.isEmpty() || status.isEmpty() || priority.isEmpty()) {
                        out.println("UPDATE_FAILED_EMPTY");
                        continue;
                    }

                    try {
                        DBHelper.DBResult r = DBHelper.updateTask(id, title, desc, status, priority, dueDate);
                        switch (r) {
                            case SUCCESS:
                                out.println("TASK_UPDATED");
                                ReplicationManager.sendReplication(
                                        "REPLICATE_UPDATE|" + id + "|" + title + "|" + desc + "|" + status);
                                break;
                            case SERVER_UNAVAILABLE:
                                out.println("SERVER_UNAVAILABLE");
                                break;
                            default:
                                out.println("UPDATE_FAILED");
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        out.println("UPDATE_FAILED");
                    }
                }
                // ================= VIEW TASKS (FIXED PROTOCOL) =================
                else if (command.equals("VIEW_TASKS")) {

                    try {
                        Connection conn = DBConnection.getConnection();

                        String sql = "SELECT * FROM tasks";
                        PreparedStatement ps = conn.prepareStatement(sql);

                        ResultSet rs = ps.executeQuery();

                        // IMPORTANT: SINGLE LINE RESPONSE FORMAT
                        StringBuilder response = new StringBuilder("TASKS");

                        while (rs.next()) {

                            response.append("|")
                                    .append(rs.getInt("id")).append(",")
                                    .append(rs.getString("title")).append(",")
                                    .append(rs.getString("description")).append(",")
                                    .append(rs.getString("status")).append(",")
                                    .append(rs.getString("assigned_to")).append(",")
                                    .append(rs.getString("priority")).append(",")
                                    .append(rs.getString("due_date"));
                        }

                        out.println(response.toString());

                    } catch (Exception e) {
                        e.printStackTrace();
                        out.println("TASKS|EMPTY");
                    }
                }

                // ================= UNKNOWN =================
                else {
                    out.println("UNKNOWN_COMMAND");
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Client disconnected: " + e.getMessage());
        }
    }
}