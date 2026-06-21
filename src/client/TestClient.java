/*package client;

import java.io.*;
import java.net.Socket;

public class TestClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 5000);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(
                    socket.getOutputStream(), true);

            // 🔥 TEST VIEW TASKS
            out.println("VIEW_TASKS");

            String response;
            while ((response = in.readLine()) != null) {
                System.out.println(response);
            }

            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}*/