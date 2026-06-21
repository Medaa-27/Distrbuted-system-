
package db;

import java.sql.Connection;
import db.DualDBConnection;

public class DBConnection {
    public static Connection getConnection() {
        return DualDBConnection.getPrimary();
    }
}