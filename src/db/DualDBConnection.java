package db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DualDBConnection {

    private static final String URL1 = "jdbc:mysql://localhost:3306/distributed_work_system";

    private static final String URL2 = "jdbc:mysql://localhost:3306/distributed_work_system_1";

    private static final String USER = "root";
    private static final String PASSWORD = "Brave1221";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            System.out.println("❌ Driver error");
        }
    }

    public static Connection getPrimary() {
        try {
            return DriverManager.getConnection(URL1, USER, PASSWORD);
        } catch (Exception e) {
            System.out.println("❌ Primary DB error");
            return null;
        }
    }

    public static Connection getReplica() {
        try {
            return DriverManager.getConnection(URL2, USER, PASSWORD);
        } catch (Exception e) {
            System.out.println("❌ Replica DB error");
            return null;
        }
    }
}