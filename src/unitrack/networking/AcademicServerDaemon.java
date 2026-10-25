package unitrack.networking;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;

/**
 * Step 29: Local Academic Server Daemon (TCP ServerSocket)
 * Demonstrates Java Networking & Stream I/O.
 * Listens on a local port (e.g. 8085) and serves institutional academic events/holidays.
 */
public class AcademicServerDaemon implements Runnable {
    public static final int DEFAULT_PORT = 8085;
    private ServerSocket serverSocket;
    private volatile boolean running = false;

    public void startServer() {
        if (running) return;
        running = true;
        Thread serverThread = new Thread(this, "UniTrack-SyncServer");
        serverThread.setDaemon(true);
        serverThread.start();
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(DEFAULT_PORT);
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    handleClient(clientSocket);
                } catch (IOException e) {
                    if (!running) break;
                }
            }
        } catch (IOException e) {
            System.err.println("Notice: Sync server port " + DEFAULT_PORT + " in use or unavailable: " + e.getMessage());
        }
    }

    private void handleClient(Socket socket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            String command = reader.readLine();
            if ("GET_ACADEMIC_FEED".equalsIgnoreCase(command)) {
                // Send back pipe-delimited official university announcements/holidays
                int currentYear = LocalDate.now().getYear();
                writer.println("University Convocation|" + currentYear + "-11-15|HOLIDAY|Campus Main Auditorium");
                writer.println("Winter Break Starts|" + currentYear + "-12-20|HOLIDAY|Campus-wide");
                writer.println("Spring Semester Orientation|" + (currentYear + 1) + "-01-10|LECTURE|Hall B");
                writer.println("END_FEED");
            }
        } catch (IOException e) {
            System.err.println("Error serving sync client: " + e.getMessage());
        }
    }

    public void stopServer() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException ignored) {}
    }
}
