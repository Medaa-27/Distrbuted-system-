package server;

import java.net.ServerSocket;
import java.net.Socket;

public class MainServer {

    private static final int PORT = 5000;

    public static void main(String[] args) {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("🚀 Main Server Started on port " + PORT + "...");

            while (true) {
                try {
                    Socket socket = serverSocket.accept();

                    System.out.println("✅ New client connected: "
                            + socket.getInetAddress().getHostAddress());

                    // Create new thread for each client
                    ClientHandler handler = new ClientHandler(socket);
                    handler.start();

                } catch (Exception clientError) {
                    System.out.println("⚠️ Error handling client: " + clientError.getMessage());
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Server failed to start: " + e.getMessage());
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (Exception e) {
                System.out.println("⚠️ Error closing server");
            }
        }
    }
}