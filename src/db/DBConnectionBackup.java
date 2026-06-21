package db;

import java.sql.Connection;
import db.DualDBConnection;

public class DBConnectionBackup {
    public static Connection getConnection() {
        return DualDBConnection.getReplica();
    }
}