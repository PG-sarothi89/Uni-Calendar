package unitrack.concurrency;

import unitrack.model.CalendarEvent;
import unitrack.service.CalendarService;

import javax.swing.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Step 28: Multithreaded Background Reminder Daemon
 * Demonstrates Java Concurrency (ScheduledExecutorService, Thread Safety).
 * Periodically monitors approaching deadlines and safely dispatches alerts
 * to the Swing Event Dispatch Thread (EDT) via SwingUtilities.invokeLater.
 */
public class ReminderDaemon {
    private final ScheduledExecutorService scheduler;
    private final Set<String> sentAlertKeys = ConcurrentHashMap.newKeySet();
    private final CalendarService calendarService = CalendarService.getInstance();
    private boolean isRunning = false;

    public ReminderDaemon() {
        // Create a single-thread background daemon executor
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "UniTrack-Reminder-Daemon");
            t.setDaemon(true); // Daemon thread exits cleanly with JVM
            return t;
        });
    }

    /**
     * Starts the periodic background deadline check (runs every 60 seconds)
     */
    public synchronized void start() {
        if (isRunning) return;
        isRunning = true;

        // Schedule at fixed rate: initial delay 5s, repeat every 60s
        scheduler.scheduleAtFixedRate(this::checkApproachingDeadlines, 5, 60, TimeUnit.SECONDS);
    }

    /**
     * Background task executed by the daemon worker thread
     */
    private void checkApproachingDeadlines() {
        try {
            LocalDate today = LocalDate.now();
            LocalDate tomorrow = today.plusDays(1);

            // Fetch upcoming events scheduled for today and tomorrow
            List<CalendarEvent> upcoming = calendarService.getUpcomingEvents(1);

            for (CalendarEvent event : upcoming) {
                // Thread-safe deduplication key: eventId + eventDate
                String dedupKey = event.getId() + "_" + event.getDate().toString();

                if (sentAlertKeys.add(dedupKey)) {
                    // Critical Swing Rule: Update UI strictly on Event Dispatch Thread (EDT)
                    SwingUtilities.invokeLater(() -> showDeadlineAlert(event));
                }
            }
        } catch (Exception e) {
            System.err.println("ReminderDaemon error during check: " + e.getMessage());
        }
    }

    /**
     * Dispatches a deadline alert popup on the EDT
     */
    private void showDeadlineAlert(CalendarEvent event) {
        String msg = String.format("Academic Reminder:\n\n[%s] %s\nDate: %s at %s\nLocation: %s\n\nPlease ensure your preparation is complete.",
                event.getType(), event.getTitle(), event.getDate(),
                event.getStartTime() != null ? event.getStartTime() : "All Day",
                event.getLocation() != null ? event.getLocation() : "N/A");

        JOptionPane.showMessageDialog(null, msg, "UniTrack — Upcoming Deadline Alert", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Clean shutdown hook called during window close
     */
    public synchronized void shutdown() {
        if (!isRunning) return;
        isRunning = false;
        scheduler.shutdownNow();
    }
}
