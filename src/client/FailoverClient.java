package client;

import java.io.*;
import java.net.Socket;

public class FailoverClient {

    public static Socket connectToServer() {
        String host = ClientConfig.getHost();
        int[] ports = ClientConfig.getPorts();
        for (int p : ports) {
            try {
                return new Socket(host, p);
            } catch (Exception e) {
                // try next
            }
        }
        return null;
    }

    public static void main(String[] args) {
        Socket socket = connectToServer();
        if (socket == null) {
            System.out.println("No server available");
            return;
        }

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            out.println("VIEW_TASKS");
            String response = in.readLine();
            System.out.println("Response: " + response);

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}