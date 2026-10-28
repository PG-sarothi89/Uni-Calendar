package unitrack.view;

import unitrack.model.CalendarEvent;
import unitrack.model.User;
import unitrack.networking.AcademicServerDaemon;
import unitrack.networking.CalendarSyncClient;
import unitrack.service.AuthService;
import unitrack.service.ConsultancyService;
import unitrack.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;

/**
 * Step 36: Main Calendar View
 *
 * Implements:
 *   1. Consistent scenic background photo across every page using UIUtils.paintScenicBackground.
 *   2. Floating unified CalendarCardPanel centered in the viewport.
 *   3. Harmonized translucent glassmorphism navigation buttons with Segoe UI typography.
 *   4. Student-Teacher Consultancy booking and Batch Advisor administrative controls.
 */
public class CalendarView extends JPanel {

    public interface CalendarViewListener {
        void onLogout();
    }

    private final User currentUser;
    private final CalendarViewListener viewListener;
    private final AcademicServerDaemon serverDaemon;
    private final CalendarCardPanel cardPanel;
    private final ConsultancyService consultancyService = ConsultancyService.getInstance();

    // Top action bar button references for dynamic badge updates
    private JButton consultancyBtn;

    public CalendarView(CalendarViewListener listener, AcademicServerDaemon serverDaemon) {
        this.viewListener = listener;
        this.currentUser = AuthService.getInstance().getCurrentUser();
        this.serverDaemon = serverDaemon;

        setLayout(new BorderLayout());
        setOpaque(true);

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

    /**
     * Paints the scenic background image across every page with dark atmospheric overlay
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        UIUtils.paintScenicBackground(g, getWidth(), getHeight());
    }

    private JPanel buildFloatingTopBar() {
        JPanel bar = new JPanel(new BorderLayout(0, 0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(14, 30, 0, 30));

        // Brand on Left: Green Diamond + Title in high-contrast crisp white/silver
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandPanel.setOpaque(false);

        JComponent diamondIcon = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UIUtils.enableAntiAliasing(g2);
                g2.setColor(new Color(76, 175, 80)); // Vibrant emerald green
                int[] xPoints = {7, 14, 7, 0};
                int[] yPoints = {0, 8, 16, 8};
                g2.fillPolygon(xPoints, yPoints, 4);
                g2.dispose();
            }
        };
        diamondIcon.setPreferredSize(new Dimension(16, 18));

        JLabel brandText = new JLabel("UniTrack — Smart Academic Calendar");
        brandText.setFont(new Font("Segoe UI", Font.BOLD, 15));
        brandText.setForeground(new Color(245, 247, 252));

        brandPanel.add(diamondIcon);
        brandPanel.add(brandText);

        // Action Buttons on Right
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        // User role pill
        String roleTitle = currentUser != null
                ? currentUser.getName() + " [" + currentUser.getRole().getDisplayName() + "]"
                : "Guest";
        JLabel rolePill = new JLabel("  " + roleTitle + "  ");
        rolePill.setFont(UIUtils.FONT_SMALL_BOLD);
        rolePill.setForeground(new Color(230, 235, 245));
        rolePill.setBackground(new Color(20, 25, 45, 200));
        rolePill.setOpaque(true);
        rolePill.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 45), 1, true),
                new EmptyBorder(5, 12, 5, 12)
        ));
        actions.add(rolePill);

        // -------------------------------------------------------------
        // Feature 1: Student-Teacher Consultancy Button
        // -------------------------------------------------------------
        if (currentUser != null && currentUser.isStudent()) {
            consultancyBtn = createAccentPillButton("💬 Book Consultancy", UIUtils.LOGIN_ACCENT);
            consultancyBtn.addActionListener(e -> openConsultancyDialog());
            actions.add(consultancyBtn);
        } else if (currentUser != null && currentUser.isTeacher()) {
            int pending = consultancyService.getPendingCountForTeacher(currentUser.getId());
            String btnText = pending > 0 ? "💬 Consultancies (" + pending + ")" : "💬 Consultancies";
            consultancyBtn = createAccentPillButton(btnText, pending > 0 ? new Color(245, 124, 0) : new Color(35, 45, 75));
            consultancyBtn.addActionListener(e -> openConsultancyDialog());
            actions.add(consultancyBtn);
        } else if (currentUser != null && currentUser.isBatchAdvisor()) {
            // Batch Advisor Console Button
            JButton advisorBtn = createAccentPillButton("🎓 Batch Advisor Console", UIUtils.LOGIN_ACCENT);
            advisorBtn.addActionListener(e -> openBatchAdvisorDialog());
            actions.add(advisorBtn);

            consultancyBtn = createGlassPillButton("💬 All Consultancies");
            consultancyBtn.addActionListener(e -> openConsultancyDialog());
            actions.add(consultancyBtn);
        }

        // Syllabus Progress Tracker Button
        JButton syllabusBtn = createGlassPillButton("Syllabus Progress");
        syllabusBtn.addActionListener(e -> {
            Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
            SyllabusDialog dialog = new SyllabusDialog(owner);
            dialog.setVisible(true);
        });
        actions.add(syllabusBtn);

        // Personal To-Do Button
        JButton tasksBtn = createGlassPillButton("My Tasks");
        tasksBtn.addActionListener(e -> {
            Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
            TodoDialog dialog = new TodoDialog(owner);
            dialog.setVisible(true);
        });
        actions.add(tasksBtn);

        // Sync Feed Button
        JButton syncBtn = createGlassPillButton("Sync Feed");
        syncBtn.addActionListener(e -> onSyncClicked());
        actions.add(syncBtn);

        // Logout Button
        JButton logoutBtn = createGlassPillButton("Logout");
        logoutBtn.setForeground(new Color(255, 120, 120));
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

    /**
     * Creates a translucent frosted glass pill button for the top navigation bar
     */
    private JButton createGlassPillButton(String label) {
        JButton btn = new JButton(label);
        btn.setFont(UIUtils.FONT_SMALL_BOLD);
        btn.setForeground(new Color(240, 245, 255));
        btn.setBackground(new Color(25, 32, 54, 195));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 45), 1, true),
                new EmptyBorder(5, 12, 5, 12)
        ));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(45, 58, 92, 230));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(25, 32, 54, 195));
            }
        });
        return btn;
    }

    /**
     * Creates a prominent accent pill button (e.g. for consultancy or advisor actions)
     */
    private JButton createAccentPillButton(String label, Color accentColor) {
        JButton btn = new JButton(label);
        btn.setFont(UIUtils.FONT_SMALL_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(accentColor);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 60), 1, true),
                new EmptyBorder(5, 12, 5, 12)
        ));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(accentColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(accentColor);
            }
        });
        return btn;
    }

    private void openConsultancyDialog() {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        ConsultancyDialog dialog = new ConsultancyDialog(owner);
        dialog.setVisible(true);

        if (dialog.isChanged()) {
            cardPanel.repaint();
            updateConsultancyBadge();
        }
    }

    private void openBatchAdvisorDialog() {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        BatchAdvisorDialog dialog = new BatchAdvisorDialog(owner);
        dialog.setVisible(true);

        if (dialog.isChanged()) {
            cardPanel.repaint();
        }
    }

    private void updateConsultancyBadge() {
        if (currentUser != null && currentUser.isTeacher() && consultancyBtn != null) {
            int pending = consultancyService.getPendingCountForTeacher(currentUser.getId());
            String btnText = pending > 0 ? "💬 Consultancies (" + pending + ")" : "💬 Consultancies";
            consultancyBtn.setText(btnText);
            consultancyBtn.setBackground(pending > 0 ? new Color(245, 124, 0) : new Color(35, 45, 75));
        }
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
