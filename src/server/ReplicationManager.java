package server;

import java.io.*;
import java.net.Socket;

public class ReplicationManager {

    private static final String BACKUP_HOST = "localhost";
    private static final int BACKUP_PORT = 5001;

    public static void sendReplication(String message) {
        try {
            Socket socket = new Socket(BACKUP_HOST, BACKUP_PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(message);
            socket.close();
            System.out.println("📤 Replication sent: " + message);
        } catch (Exception e) {
            System.out.println("⚠️ Replication failed: " + e.getMessage());
        }
    }
}