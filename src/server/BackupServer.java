package server;

import java.net.ServerSocket;
import java.net.Socket;

public class BackupServer {

    private static final int PORT = 5001;

    public static void main(String[] args) {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("🚀 Backup Server Started on port " + PORT + "...");

            while (true) {
                try {
                    Socket socket = serverSocket.accept();

                    System.out.println("✅ New connection to backup: "
                            + socket.getInetAddress().getHostAddress());

                    // Create new thread for each connection
                    BackupHandler handler = new BackupHandler(socket);
                    handler.start();

                } catch (Exception clientError) {
                    System.out.println("⚠️ Error handling backup connection: " + clientError.getMessage());
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Backup Server failed to start: " + e.getMessage());
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (Exception e) {
                System.out.println("⚠️ Error closing backup server");
            }
        }
    }
}