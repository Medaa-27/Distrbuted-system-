package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBHelper {

    public enum DBResult {
        SUCCESS, USER_EXISTS, FAILED, SERVER_UNAVAILABLE
    }

    public static DBResult registerUser(String username, String password) {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn == null)
                return DBResult.SERVER_UNAVAILABLE;

            String checkSql = "SELECT * FROM users WHERE username=?";
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setString(1, username);
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next())
                        return DBResult.USER_EXISTS;
                }
            }

            String insertSql = "INSERT INTO users(username, password) VALUES(?, ?)";
            try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                insertPs.setString(1, username);
                insertPs.setString(2, password);
                int row = insertPs.executeUpdate();
                if (row <= 0)
                    return DBResult.FAILED;
            }

            // try backup best-effort (auto-create replica schema on missing table)
            try {
                Connection b = DBConnectionBackup.getConnection();
                if (b != null) {
                    try (PreparedStatement insertPs2 = b.prepareStatement(insertSql)) {
                        insertPs2.setString(1, username);
                        insertPs2.setString(2, password);
                        insertPs2.executeUpdate();
                    }
                }
            } catch (Exception be) {
                String msg = be.getMessage() == null ? "" : be.getMessage().toLowerCase();
                System.err.println("⚠️ Backup write (registerUser) failed: " + be.getMessage());
                if (msg.contains("doesn't exist") || msg.contains("no such table") || msg.contains("unknown table")) {
                    try {
                        Connection b2 = DBConnectionBackup.getConnection();
                        if (b2 != null) {
                            ensureReplicaSchema(b2);
                            try (PreparedStatement insertPs3 = b2.prepareStatement(insertSql)) {
                                insertPs3.setString(1, username);
                                insertPs3.setString(2, password);
                                insertPs3.executeUpdate();
                                System.out.println("✅ Replica schema created and registerUser retried successfully");
                            }
                        }
                    } catch (Exception be2) {
                        System.err.println("⚠️ Replica retry (registerUser) failed: " + be2.getMessage());
                        be2.printStackTrace();
                    }
                } else {
                    be.printStackTrace();
                }
            }

            return DBResult.SUCCESS;

        } catch (Exception e) {
            String msg = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
            if (msg.contains("doesn't exist") || msg.contains("no such table") || msg.contains("unknown column")) {
                return DBResult.SERVER_UNAVAILABLE;
            }
            return DBResult.FAILED;
        }
    }

    public static DBResult insertTask(String title, String desc, String status, String user, String priority,
            java.sql.Date dueDate) {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn == null)
                return DBResult.SERVER_UNAVAILABLE;

            String sqlWithDate = "INSERT INTO tasks(title, description, status, assigned_to, priority, due_date) VALUES(?,?,?,?,?,?)";

            try (PreparedStatement ps = conn.prepareStatement(sqlWithDate)) {
                ps.setString(1, title);
                ps.setString(2, desc);
                ps.setString(3, status);
                ps.setString(4, user);
                ps.setString(5, priority);
                ps.setDate(6, dueDate);
                int row = ps.executeUpdate();
                if (row <= 0)
                    return DBResult.FAILED;
            }

            // backup best-effort (auto-create replica schema on missing table)
            try {
                Connection b = DBConnectionBackup.getConnection();
                if (b != null) {
                    try (PreparedStatement ps2 = b.prepareStatement(sqlWithDate)) {
                        ps2.setString(1, title);
                        ps2.setString(2, desc);
                        ps2.setString(3, status);
                        ps2.setString(4, user);
                        ps2.setString(5, priority);
                        ps2.setDate(6, dueDate);
                        ps2.executeUpdate();
                    }
                }
            } catch (Exception be) {
                String msg = be.getMessage() == null ? "" : be.getMessage().toLowerCase();
                System.err.println("⚠️ Backup write (insertTask with date) failed: " + be.getMessage());
                if (msg.contains("doesn't exist") || msg.contains("no such table") || msg.contains("unknown table")) {
                    try {
                        Connection b2 = DBConnectionBackup.getConnection();
                        if (b2 != null) {
                            ensureReplicaSchema(b2);
                            try (PreparedStatement ps3 = b2.prepareStatement(sqlWithDate)) {
                                ps3.setString(1, title);
                                ps3.setString(2, desc);
                                ps3.setString(3, status);
                                ps3.setString(4, user);
                                ps3.setString(5, priority);
                                ps3.setDate(6, dueDate);
                                ps3.executeUpdate();
                                System.out.println("✅ Replica schema created and insertTask retried successfully");
                            }
                        }
                    } catch (Exception be2) {
                        System.err.println("⚠️ Replica retry (insertTask) failed: " + be2.getMessage());
                        be2.printStackTrace();
                    }
                } else {
                    be.printStackTrace();
                }
            }

            return DBResult.SUCCESS;

        } catch (java.sql.SQLException sqe) {
            String message = sqe.getMessage() == null ? "" : sqe.getMessage().toLowerCase();
            // try fallback without due_date
            if (message.contains("unknown column") || message.contains("no such column")
                    || message.contains("column \"due_date\"")) {
                try {
                    Connection conn = DBConnection.getConnection();
                    if (conn == null)
                        return DBResult.SERVER_UNAVAILABLE;
                    String sql = "INSERT INTO tasks(title, description, status, assigned_to, priority) VALUES(?,?,?,?,?)";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, title);
                        ps.setString(2, desc);
                        ps.setString(3, status);
                        ps.setString(4, user);
                        ps.setString(5, priority);
                        int row = ps.executeUpdate();
                        if (row <= 0)
                            return DBResult.FAILED;
                    }

                    // backup best-effort (auto-create replica schema on missing table)
                    try {
                        Connection b = DBConnectionBackup.getConnection();
                        if (b != null) {
                            try (PreparedStatement ps2 = b.prepareStatement(sql)) {
                                ps2.setString(1, title);
                                ps2.setString(2, desc);
                                ps2.setString(3, status);
                                ps2.setString(4, user);
                                ps2.setString(5, priority);
                                ps2.executeUpdate();
                            }
                        }
                    } catch (Exception be) {
                        String msg = be.getMessage() == null ? "" : be.getMessage().toLowerCase();
                        System.err.println("⚠️ Backup write (insertTask fallback) failed: " + be.getMessage());
                        if (msg.contains("doesn't exist") || msg.contains("no such table")
                                || msg.contains("unknown table")) {
                            try {
                                Connection b2 = DBConnectionBackup.getConnection();
                                if (b2 != null) {
                                    ensureReplicaSchema(b2);
                                    try (PreparedStatement ps3 = b2.prepareStatement(sql)) {
                                        ps3.setString(1, title);
                                        ps3.setString(2, desc);
                                        ps3.setString(3, status);
                                        ps3.setString(4, user);
                                        ps3.setString(5, priority);
                                        ps3.executeUpdate();
                                        System.out.println(
                                                "✅ Replica schema created and insertTask(fallback) retried successfully");
                                    }
                                }
                            } catch (Exception be2) {
                                System.err
                                        .println("⚠️ Replica retry (insertTask fallback) failed: " + be2.getMessage());
                                be2.printStackTrace();
                            }
                        } else {
                            be.printStackTrace();
                        }
                    }

                    return DBResult.SUCCESS;
                } catch (Exception e) {
                    String msg = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
                    if (msg.contains("doesn't exist") || msg.contains("no such table"))
                        return DBResult.SERVER_UNAVAILABLE;
                    return DBResult.FAILED;
                }
            }
            String msg = sqe.getMessage() == null ? "" : sqe.getMessage().toLowerCase();
            if (msg.contains("doesn't exist") || msg.contains("no such table"))
                return DBResult.SERVER_UNAVAILABLE;
            return DBResult.FAILED;
        } catch (Exception e) {
            return DBResult.FAILED;
        }
    }

    public static DBResult deleteTask(String id) {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn == null)
                return DBResult.SERVER_UNAVAILABLE;

            String sql = "DELETE FROM tasks WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, id);
                int row = ps.executeUpdate();
                if (row <= 0)
                    return DBResult.FAILED;
            }

            // backup best-effort (auto-create replica schema on missing table)
            try {
                Connection b = DBConnectionBackup.getConnection();
                if (b != null) {
                    try (PreparedStatement ps2 = b.prepareStatement(sql)) {
                        ps2.setString(1, id);
                        ps2.executeUpdate();
                    }
                }
            } catch (Exception be) {
                String msg = be.getMessage() == null ? "" : be.getMessage().toLowerCase();
                System.err.println("⚠️ Backup write (deleteTask) failed: " + be.getMessage());
                if (msg.contains("doesn't exist") || msg.contains("no such table") || msg.contains("unknown table")) {
                    try {
                        Connection b2 = DBConnectionBackup.getConnection();
                        if (b2 != null) {
                            ensureReplicaSchema(b2);
                            try (PreparedStatement ps3 = b2.prepareStatement(sql)) {
                                ps3.setString(1, id);
                                ps3.executeUpdate();
                                System.out.println("✅ Replica schema created and deleteTask retried successfully");
                            }
                        }
                    } catch (Exception be2) {
                        System.err.println("⚠️ Replica retry (deleteTask) failed: " + be2.getMessage());
                        be2.printStackTrace();
                    }
                } else {
                    be.printStackTrace();
                }
            }

            return DBResult.SUCCESS;
        } catch (Exception e) {
            String msg = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
            if (msg.contains("doesn't exist") || msg.contains("no such table"))
                return DBResult.SERVER_UNAVAILABLE;
            return DBResult.FAILED;
        }
    }

    public static DBResult updateTask(String id, String title, String desc, String status, String priority,
            String dueDate) {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn == null)
                return DBResult.SERVER_UNAVAILABLE;

            String sql = "UPDATE tasks SET title=?, description=?, status=?, priority=?, due_date=? WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, title);
                ps.setString(2, desc);
                ps.setString(3, status);
                ps.setString(4, priority);
                ps.setString(5, dueDate);
                ps.setString(6, id);

                int rows = ps.executeUpdate();
                if (rows <= 0)
                    return DBResult.FAILED;
            }

            // backup best-effort (auto-create replica schema on missing table)
            try {
                Connection b = DBConnectionBackup.getConnection();
                if (b != null) {
                    try (PreparedStatement ps2 = b.prepareStatement(sql)) {
                        ps2.setString(1, title);
                        ps2.setString(2, desc);
                        ps2.setString(3, status);
                        ps2.setString(4, priority);
                        ps2.setString(5, dueDate);
                        ps2.setString(6, id);
                        ps2.executeUpdate();
                    }
                }
            } catch (Exception be) {
                String msg = be.getMessage() == null ? "" : be.getMessage().toLowerCase();
                System.err.println("⚠️ Backup write (updateTask) failed: " + be.getMessage());
                if (msg.contains("doesn't exist") || msg.contains("no such table") || msg.contains("unknown table")) {
                    try {
                        Connection b2 = DBConnectionBackup.getConnection();
                        if (b2 != null) {
                            ensureReplicaSchema(b2);
                            try (PreparedStatement ps3 = b2.prepareStatement(sql)) {
                                ps3.setString(1, title);
                                ps3.setString(2, desc);
                                ps3.setString(3, status);
                                ps3.setString(4, priority);
                                ps3.setString(5, dueDate);
                                ps3.setString(6, id);
                                ps3.executeUpdate();
                                System.out.println("✅ Replica schema created and updateTask retried successfully");
                            }
                        }
                    } catch (Exception be2) {
                        System.err.println("⚠️ Replica retry (updateTask) failed: " + be2.getMessage());
                        be2.printStackTrace();
                    }
                } else {
                    be.printStackTrace();
                }
            }

            return DBResult.SUCCESS;
        } catch (Exception e) {
            String msg = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
            if (msg.contains("doesn't exist") || msg.contains("no such table"))
                return DBResult.SERVER_UNAVAILABLE;
            return DBResult.FAILED;
        }
    }

    private static void ensureReplicaSchema(Connection b) {
        try (Statement st = b.createStatement()) {
            String createUsers = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "username VARCHAR(100) NOT NULL UNIQUE,"
                    + "password VARCHAR(255) NOT NULL,"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP) ENGINE=InnoDB";

            String createTasks = "CREATE TABLE IF NOT EXISTS tasks ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "title VARCHAR(255) NOT NULL,"
                    + "description TEXT,"
                    + "status VARCHAR(50),"
                    + "assigned_to VARCHAR(100),"
                    + "priority VARCHAR(50),"
                    + "due_date DATE,"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP) ENGINE=InnoDB";

            st.executeUpdate(createUsers);
            st.executeUpdate(createTasks);
            System.out.println("✅ Replica schema ensured (users, tasks)");
        } catch (Exception e) {
            System.err.println("❌ Failed to ensure replica schema: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
