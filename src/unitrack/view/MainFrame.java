package unitrack.view;

import unitrack.concurrency.ReminderDaemon;
import unitrack.model.User;
import unitrack.networking.AcademicServerDaemon;
import unitrack.util.UIUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Step 37: Root Application Window (MainFrame)
 *
 * The top-level JFrame that manages the application's two main screens:
 *   1. LoginView  — presented on launch / after logout
 *   2. CalendarView — presented after successful authentication
 *
 * Uses CardLayout to switch between screens without creating new windows.
 * Also owns the ReminderDaemon and AcademicServerDaemon lifecycles:
 *   - Daemon is started when the user logs in.
 *   - Daemon is cleanly shut down when the window is closed.
 */
public class MainFrame extends JFrame {

    // Card names for switching panels
    private static final String CARD_LOGIN    = "LOGIN";
    private static final String CARD_CALENDAR = "CALENDAR";

    private final CardLayout  cardLayout = new CardLayout();
    private final JPanel      rootPanel  = new JPanel(cardLayout);

    // Background services
    private final ReminderDaemon      reminderDaemon  = new ReminderDaemon();
    private final AcademicServerDaemon serverDaemon   = new AcademicServerDaemon();

    // Keep reference to replace CalendarView after logout / re-login
    private CalendarView calendarView;

    public MainFrame() {
        super("UniTrack — Smart Academic Calendar");

        // ── Window configuration ──────────────────────────────────────
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(1060, 680));
        setPreferredSize(new Dimension(1160, 760));
        setLocationRelativeTo(null);

        // Custom title-bar icon (Unicode diamond, real icon would be set via setIconImage)
        // setIconImage(ImageIO.read(...));  // swap for real PNG icon if available

        // ── Register window-close hook for clean daemon shutdown ───────
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                // Cleanly stop background threads before exit
                reminderDaemon.shutdown();
                serverDaemon.stopServer();
                dispose();
                System.exit(0);
            }
        });

        // ── Global Look & Feel tweaks ──────────────────────────────────
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // ── Build root panel ───────────────────────────────────────────
        rootPanel.setBackground(new Color(236, 239, 246));

        // Login screen (always present)
        LoginView loginView = new LoginView(this::onLoginSuccess);
        rootPanel.add(loginView, CARD_LOGIN);

        setContentPane(rootPanel);
        pack();

        // Start on the Login card
        cardLayout.show(rootPanel, CARD_LOGIN);
    }

    // ----------------------------------------------------------------
    // Called by LoginView when credentials are successfully verified
    // ----------------------------------------------------------------
    private void onLoginSuccess(User user) {
        // Start background daemon services after login
        serverDaemon.startServer();
        reminderDaemon.start();

        // Build / replace CalendarView for this user session
        if (calendarView != null) {
            rootPanel.remove(calendarView);
        }

        calendarView = new CalendarView(
                // Logout callback: stop daemons, return to login
                () -> {
                    reminderDaemon.shutdown();
                    serverDaemon.stopServer();
                    cardLayout.show(rootPanel, CARD_LOGIN);
                },
                serverDaemon
        );

        rootPanel.add(calendarView, CARD_CALENDAR);
        rootPanel.revalidate();

        // Switch to the calendar screen
        cardLayout.show(rootPanel, CARD_CALENDAR);
    }
}
