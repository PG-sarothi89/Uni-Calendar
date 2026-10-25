package unitrack.networking;

import unitrack.model.CalendarEvent;
import unitrack.model.EventType;
import unitrack.service.CalendarService;
import unitrack.service.AuthService;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Step 30: Academic Sync Client (TCP Socket)
 * Connects to the local server daemon to synchronize official academic events.
 */
public class CalendarSyncClient {

    public interface SyncCallback {
        void onSyncComplete(int countAdded);
        void onSyncFailed(String reason);
    }

    public static void syncWithServer(SyncCallback callback) {
        new Thread(() -> {
            List<CalendarEvent> newEvents = new ArrayList<>();
            try (Socket socket = new Socket("localhost", AcademicServerDaemon.DEFAULT_PORT);
                 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                writer.println("GET_ACADEMIC_FEED");

                String line;
                while ((line = reader.readLine()) != null) {
                    if ("END_FEED".equals(line)) break;

                    String[] parts = line.split("\\|");
                    if (parts.length >= 4) {
                        String title = parts[0];
                        LocalDate date = LocalDate.parse(parts[1]);
                        EventType type = EventType.valueOf(parts[2]);
                        String location = parts[3];

                        CalendarEvent evt = new CalendarEvent(0, title, "Synchronized from institutional feed",
                                type, date, LocalTime.of(9, 0), LocalTime.of(17, 0), null, 4, location, null);
                        newEvents.add(evt);
                    }
                }

                // Add to calendar
                int added = 0;
                for (CalendarEvent e : newEvents) {
                    try {
                        CalendarService.getInstance().createEvent(e, AuthService.getInstance().getCurrentUser());
                        added++;
                    } catch (Exception ignored) {}
                }

                int count = added;
                if (callback != null) {
                    callback.onSyncComplete(count);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onSyncFailed(e.getMessage());
                }
            }
        }).start();
    }
}
