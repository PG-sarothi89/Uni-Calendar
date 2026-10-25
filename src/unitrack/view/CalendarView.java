package unitrack.view;

import unitrack.model.CalendarEvent;
import unitrack.model.User;
import unitrack.networking.AcademicServerDaemon;
import unitrack.networking.CalendarSyncClient;
import unitrack.service.AuthService;
import unitrack.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;

/**
 * Step 36: Main Calendar View
 *
 * Hosts the floating, unified CalendarCardPanel matching the reference UI,
 * along with a sleek, minimalist floating top navigation bar providing access to
 * Syllabus Progress, Personal Tasks, TCP Sync, and Logout.
 */
public class CalendarView extends JPanel {

    public interface CalendarViewListener {
        void onLogout();
    }

    private final User currentUser;
    private final CalendarViewListener viewListener;
    private final AcademicServerDaemon serverDaemon;
    private final CalendarCardPanel cardPanel;

    public CalendarView(CalendarViewListener listener, AcademicServerDaemon serverDaemon) {
        this.viewListener = listener;
        this.currentUser = AuthService.getInstance().getCurrentUser();
        this.serverDaemon = serverDaemon;

        setLayout(new BorderLayout());
        setBackground(UIUtils.WINDOW_BG); // Soft cool grey background from reference

        // 1. Sleek Floating Top Action Bar
        add(buildFloatingTopBar(), BorderLayout.NORTH);

        // 2. Centered Unified Calendar Card
        cardPanel = new CalendarCardPanel(new CalendarCardPanel.EventActionListener() {
            @Override
            public void onAddEvent(LocalDate date) {
                openEventDialog(null, date);
            }

            @Override
            public void onEditEvent(CalendarEvent event) {
                openEventDialog(event, event.getDate());
            }
        });

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(new EmptyBorder(10, 20, 20, 20));
        centerWrapper.add(cardPanel);

        add(centerWrapper, BorderLayout.CENTER);
    }

    private JPanel buildFloatingTopBar() {
        JPanel bar = new JPanel(new BorderLayout(0, 0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(14, 30, 0, 30));

        // Brand on Left: Green Diamond + Title
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        brandPanel.setOpaque(false);

        JComponent diamondIcon = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UIUtils.enableAntiAliasing(g2);
                g2.setColor(new Color(46, 125, 50));
                int[] xPoints = {6, 12, 6, 0};
                int[] yPoints = {0, 7, 14, 7};
                g2.fillPolygon(xPoints, yPoints, 4);
                g2.dispose();
            }
        };
        diamondIcon.setPreferredSize(new Dimension(14, 16));

        JLabel brandText = new JLabel("UniTrack — Smart Academic Calendar");
        brandText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        brandText.setForeground(new Color(45, 50, 65));

        brandPanel.add(diamondIcon);
        brandPanel.add(brandText);

        // Action Buttons on Right
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        // User role pill
        String roleTitle = currentUser != null ? currentUser.getName() + " [" + currentUser.getRole() + "]" : "Guest";
        JLabel rolePill = new JLabel("  " + roleTitle + "  ");
        rolePill.setFont(UIUtils.FONT_SMALL_BOLD);
        rolePill.setForeground(new Color(60, 65, 80));
        rolePill.setBackground(Color.WHITE);
        rolePill.setOpaque(true);
        rolePill.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(215, 220, 230), 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));
        actions.add(rolePill);

        // Syllabus Tracker Button
        JButton syllabusBtn = createTopBarButton("Syllabus Progress");
        syllabusBtn.addActionListener(e -> {
            Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
            SyllabusDialog dialog = new SyllabusDialog(owner);
            dialog.setVisible(true);
        });
        actions.add(syllabusBtn);

        // Personal To-Do Button
        JButton tasksBtn = createTopBarButton("My Tasks");
        tasksBtn.addActionListener(e -> {
            Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
            TodoDialog dialog = new TodoDialog(owner);
            dialog.setVisible(true);
        });
        actions.add(tasksBtn);

        // Sync Feed Button
        JButton syncBtn = createTopBarButton("Sync Feed");
        syncBtn.addActionListener(e -> onSyncClicked());
        actions.add(syncBtn);

        // Logout Button
        JButton logoutBtn = createTopBarButton("Logout");
        logoutBtn.setForeground(new Color(210, 50, 50));
        logoutBtn.addActionListener(e -> {
            AuthService.getInstance().logout();
            if (viewListener != null) {
                viewListener.onLogout();
            }
        });
        actions.add(logoutBtn);

        bar.add(brandPanel, BorderLayout.WEST);
        bar.add(actions, BorderLayout.EAST);
        return bar;
    }

    private JButton createTopBarButton(String label) {
        JButton btn = new JButton(label);
        btn.setFont(UIUtils.FONT_SMALL_BOLD);
        btn.setForeground(new Color(70, 75, 90));
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(215, 220, 230), 1, true),
                new EmptyBorder(5, 12, 5, 12)
        ));
        return btn;
    }

    private void openEventDialog(CalendarEvent existingEvent, LocalDate defaultDate) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        EventDialog dialog = new EventDialog(owner, defaultDate, existingEvent);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            cardPanel.repaint();
        }
    }

    private void onSyncClicked() {
        if (serverDaemon != null) {
            serverDaemon.startServer();
        }

        CalendarSyncClient.syncWithServer(new CalendarSyncClient.SyncCallback() {
            @Override
            public void onSyncComplete(int countAdded) {
                SwingUtilities.invokeLater(() -> {
                    cardPanel.repaint();
                    JOptionPane.showMessageDialog(CalendarView.this,
                            countAdded + " academic holidays synced from institutional server daemon.",
                            "Institutional Feed Synchronized", JOptionPane.INFORMATION_MESSAGE);
                });
            }

            @Override
            public void onSyncFailed(String reason) {
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(CalendarView.this,
                                "Sync server status: " + reason,
                                "Sync Notice", JOptionPane.INFORMATION_MESSAGE));
            }
        });
    }
}
