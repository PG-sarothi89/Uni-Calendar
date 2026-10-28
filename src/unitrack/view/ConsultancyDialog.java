package unitrack.view;

import unitrack.model.ConsultancyRequest;
import unitrack.model.Teacher;
import unitrack.model.User;
import unitrack.service.AuthService;
import unitrack.service.BatchAdvisorService;
import unitrack.service.ConsultancyService;
import unitrack.util.DateUtils;
import unitrack.util.UIUtils;
import unitrack.view.components.ModernButton;
import unitrack.view.components.ModernTextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Step 37: Student-Teacher Consultancy Dialog
 * Enables:
 *   1. Students to request office-hour consultancy slots with faculty members.
 *   2. Teachers to review, approve (auto-adding to both calendars), or decline requests.
 *   3. Batch Advisors to monitor consultation traffic across the academic batch.
 */
public class ConsultancyDialog extends JDialog {

    private final User currentUser;
    private final ConsultancyService consultancyService = ConsultancyService.getInstance();
    private final BatchAdvisorService batchAdvisorService = BatchAdvisorService.getInstance();
    private boolean changed = false;

    // Refreshable UI components
    private JTabbedPane tabbedPane;
    private DefaultTableModel requestsTableModel;
    private JPanel pendingCardsContainer;

    public ConsultancyDialog(Frame owner) {
        super(owner, "Student-Teacher Academic Consultancy", true);
        this.currentUser = AuthService.getInstance().getCurrentUser();

        setSize(780, 520);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIUtils.WINDOW_BG);

        // Header Panel
        add(buildHeaderPanel(), BorderLayout.NORTH);

        // Tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIUtils.FONT_BODY_BOLD);
        tabbedPane.setBackground(Color.WHITE);

        if (currentUser != null && currentUser.isStudent()) {
            tabbedPane.addTab("Book New Consultancy", buildStudentBookingPanel());
            tabbedPane.addTab("My Requests & Status", buildStudentRequestsPanel());
        } else if (currentUser != null && currentUser.isTeacher()) {
            tabbedPane.addTab("Pending Requests for Approval", buildTeacherReviewPanel());
            tabbedPane.addTab("All Consultancy Sessions", buildAllConsultanciesPanel());
        } else {
            // Batch Advisor / Admin
            tabbedPane.addTab("All Department Consultancies", buildAllConsultanciesPanel());
        }

        add(tabbedPane, BorderLayout.CENTER);

        // Bottom Action Bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        bottomBar.setBackground(Color.WHITE);
        bottomBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(225, 228, 235)));

        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(UIUtils.FONT_BODY);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());
        bottomBar.add(closeBtn);

        add(bottomBar, BorderLayout.SOUTH);
    }

    private JPanel buildHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(225, 228, 235)),
                new EmptyBorder(16, 24, 16, 24)
        ));

        JLabel titleLbl = new JLabel("Faculty Office Hours & Student Consultancy");
        titleLbl.setFont(UIUtils.FONT_TITLE);
        titleLbl.setForeground(UIUtils.LOGIN_TEXT_PRIMARY);

        String subtitleText = currentUser != null
                ? "Logged in as " + currentUser.getName() + " (" + currentUser.getRole().getDisplayName() + ")"
                : "Manage and schedule 1-on-1 academic consultation sessions.";
        JLabel subtitleLbl = new JLabel(subtitleText);
        subtitleLbl.setFont(UIUtils.FONT_SUBTITLE);
        subtitleLbl.setForeground(UIUtils.LOGIN_TEXT_MUTED);

        header.add(titleLbl, BorderLayout.NORTH);
        header.add(subtitleLbl, BorderLayout.SOUTH);
        return header;
    }

    // -------------------------------------------------------------
    // Student: Booking Form
    // -------------------------------------------------------------
    private JPanel buildStudentBookingPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Teacher Selection
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.25;
        JLabel teacherLbl = new JLabel("Select Faculty Member:");
        teacherLbl.setFont(UIUtils.FONT_BODY_BOLD);
        panel.add(teacherLbl, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.75;
        List<Teacher> teachers = batchAdvisorService.getAllTeachers();
        JComboBox<Teacher> teacherCombo = new JComboBox<>(teachers.toArray(new Teacher[0]));
        teacherCombo.setFont(UIUtils.FONT_BODY);
        teacherCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Teacher) {
                    Teacher t = (Teacher) value;
                    setText(t.getName() + " (" + t.getDepartment() + ")");
                }
                return this;
            }
        });
        panel.add(teacherCombo, gbc);

        // Date Selection
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.25;
        JLabel dateLbl = new JLabel("Proposed Date (YYYY-MM-DD):");
        dateLbl.setFont(UIUtils.FONT_BODY_BOLD);
        panel.add(dateLbl, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.75;
        ModernTextField dateField = new ModernTextField();
        dateField.setText(LocalDate.now().plusDays(1).toString());
        panel.add(dateField, gbc);

        // Time Slot Selection
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.25;
        JLabel timeLbl = new JLabel("Preferred Time Slot:");
        timeLbl.setFont(UIUtils.FONT_BODY_BOLD);
        panel.add(timeLbl, gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.75;
        String[] slots = {
                "09:00 - 09:30", "09:30 - 10:00",
                "10:00 - 10:30", "10:30 - 11:00",
                "11:00 - 11:30", "11:30 - 12:00",
                "14:00 - 14:30", "14:30 - 15:00",
                "15:00 - 15:30", "15:30 - 16:00"
        };
        JComboBox<String> slotCombo = new JComboBox<>(slots);
        slotCombo.setFont(UIUtils.FONT_BODY);
        panel.add(slotCombo, gbc);

        // Topic / Problem description
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.25;
        JLabel topicLbl = new JLabel("Consultancy Topic / Agenda:");
        topicLbl.setFont(UIUtils.FONT_BODY_BOLD);
        panel.add(topicLbl, gbc);

        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.75;
        ModernTextField topicField = new ModernTextField();
        topicField.setText("OODP Project Architecture & Design Pattern Review");
        panel.add(topicField, gbc);

        // Submit Button
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 0.75;
        gbc.insets = new Insets(18, 8, 8, 8);
        ModernButton submitBtn = new ModernButton("Submit Consultancy Request", UIUtils.LOGIN_ACCENT, UIUtils.LOGIN_ACCENT_HOVER);
        submitBtn.setPreferredSize(new Dimension(240, 38));
        submitBtn.addActionListener(e -> {
            Teacher selectedTeacher = (Teacher) teacherCombo.getSelectedItem();
            if (selectedTeacher == null) {
                JOptionPane.showMessageDialog(this, "Please select a teacher.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String dateStr = dateField.getText().trim();
            LocalDate date;
            try {
                date = LocalDate.parse(dateStr);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid date (YYYY-MM-DD).", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String slot = (String) slotCombo.getSelectedItem();
            String[] times = slot.split(" - ");
            LocalTime start = LocalTime.parse(times[0].trim());
            LocalTime end = LocalTime.parse(times[1].trim());
            String topic = topicField.getText().trim();

            if (topic.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a topic for the consultation.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            consultancyService.requestConsultancy(
                    currentUser.getId(), currentUser.getName(),
                    selectedTeacher.getId(), selectedTeacher.getName(),
                    date, start, end, topic
            );

            changed = true;
            JOptionPane.showMessageDialog(this,
                    "Consultancy request submitted to " + selectedTeacher.getName() + "!\nStatus is Pending until faculty approves.",
                    "Request Submitted", JOptionPane.INFORMATION_MESSAGE);

            // Switch to requests list tab
            if (tabbedPane.getTabCount() > 1) {
                refreshStudentRequestsTable();
                tabbedPane.setSelectedIndex(1);
            }
        });
        panel.add(submitBtn, gbc);

        return panel;
    }

    // -------------------------------------------------------------
    // Student: Requests Status Table
    // -------------------------------------------------------------
    private JPanel buildStudentRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(16, 20, 16, 20));

        String[] cols = {"ID", "Faculty", "Date", "Time", "Topic", "Status", "Faculty Note"};
        requestsTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(requestsTableModel);
        table.setFont(UIUtils.FONT_BODY);
        table.setRowHeight(28);
        table.getTableHeader().setFont(UIUtils.FONT_BODY_BOLD);
        table.getTableHeader().setBackground(new Color(245, 247, 250));

        // Center align date, time, and status columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(130);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setPreferredWidth(180);
        table.getColumnModel().getColumn(5).setPreferredWidth(90);
        table.getColumnModel().getColumn(6).setPreferredWidth(160);

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        // Status pill renderer
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(UIUtils.FONT_SMALL_BOLD);
                String val = String.valueOf(value);
                if ("APPROVED".equalsIgnoreCase(val)) {
                    lbl.setForeground(new Color(46, 125, 50));
                } else if ("DECLINED".equalsIgnoreCase(val)) {
                    lbl.setForeground(new Color(211, 47, 47));
                } else {
                    lbl.setForeground(new Color(230, 81, 0));
                }
                return lbl;
            }
        });

        refreshStudentRequestsTable();
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void refreshStudentRequestsTable() {
        if (requestsTableModel == null || currentUser == null) return;
        requestsTableModel.setRowCount(0);
        List<ConsultancyRequest> list = consultancyService.getRequestsForStudent(currentUser.getId());
        for (ConsultancyRequest r : list) {
            requestsTableModel.addRow(new Object[]{
                    "#" + r.getId(),
                    r.getTeacherName(),
                    r.getDate().toString(),
                    DateUtils.formatTime(r.getStartTime()) + " - " + DateUtils.formatTime(r.getEndTime()),
                    r.getTopic(),
                    r.getStatus().name(),
                    r.getResponseNote() != null ? r.getResponseNote() : "-"
            });
        }
    }

    // -------------------------------------------------------------
    // Teacher: Pending Review Panel
    // -------------------------------------------------------------
    private JPanel buildTeacherReviewPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(new EmptyBorder(16, 20, 16, 20));

        pendingCardsContainer = new JPanel();
        pendingCardsContainer.setLayout(new BoxLayout(pendingCardsContainer, BoxLayout.Y_AXIS));
        pendingCardsContainer.setBackground(Color.WHITE);

        refreshTeacherPendingCards();

        JScrollPane scroll = new JScrollPane(pendingCardsContainer);
        scroll.setBorder(null);
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    private void refreshTeacherPendingCards() {
        if (pendingCardsContainer == null || currentUser == null) return;
        pendingCardsContainer.removeAll();

        List<ConsultancyRequest> pendingList = consultancyService.getPendingRequestsForTeacher(currentUser.getId());
        if (pendingList.isEmpty()) {
            JLabel emptyLbl = new JLabel("No pending consultancy requests at this time. All caught up!");
            emptyLbl.setFont(UIUtils.FONT_BODY);
            emptyLbl.setForeground(UIUtils.LOGIN_TEXT_MUTED);
            emptyLbl.setBorder(new EmptyBorder(30, 20, 20, 20));
            pendingCardsContainer.add(emptyLbl);
        } else {
            for (ConsultancyRequest req : pendingList) {
                pendingCardsContainer.add(createRequestReviewCard(req));
                pendingCardsContainer.add(Box.createRigidArea(new Dimension(0, 12)));
            }
        }
        pendingCardsContainer.revalidate();
        pendingCardsContainer.repaint();
    }

    private JPanel createRequestReviewCard(ConsultancyRequest req) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(new Color(250, 251, 254));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 230, 240), 1, true),
                new EmptyBorder(12, 16, 12, 16)
        ));

        // Left info
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel studentLbl = new JLabel("Student: " + req.getStudentName());
        studentLbl.setFont(UIUtils.FONT_BODY_BOLD);
        studentLbl.setForeground(UIUtils.LOGIN_TEXT_PRIMARY);

        String timeStr = req.getDate() + " (" + DateUtils.formatTime(req.getStartTime()) + " - " + DateUtils.formatTime(req.getEndTime()) + ")";
        JLabel timeLbl = new JLabel("Time: " + timeStr);
        timeLbl.setFont(UIUtils.FONT_BODY);
        timeLbl.setForeground(new Color(60, 90, 180));

        JLabel topicLbl = new JLabel("Topic: " + req.getTopic());
        topicLbl.setFont(UIUtils.FONT_BODY);
        topicLbl.setForeground(UIUtils.LOGIN_TEXT_MUTED);

        info.add(studentLbl);
        info.add(Box.createRigidArea(new Dimension(0, 4)));
        info.add(timeLbl);
        info.add(Box.createRigidArea(new Dimension(0, 4)));
        info.add(topicLbl);

        // Right Action Buttons: Approve & Decline
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        actions.setOpaque(false);

        JButton approveBtn = new JButton("Approve");
        approveBtn.setFont(UIUtils.FONT_BODY_BOLD);
        approveBtn.setForeground(Color.WHITE);
        approveBtn.setBackground(new Color(46, 125, 50));
        approveBtn.setFocusPainted(false);
        approveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        approveBtn.addActionListener(e -> {
            String note = JOptionPane.showInputDialog(this,
                    "Add optional note or room instruction for " + req.getStudentName() + ":",
                    "Approved. Faculty Room 214");
            if (note != null) {
                consultancyService.approveRequest(req.getId(), note);
                changed = true;
                refreshTeacherPendingCards();
                JOptionPane.showMessageDialog(this,
                        "Consultancy approved! Added to both your calendar and " + req.getStudentName() + "'s schedule.",
                        "Appointment Confirmed", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton declineBtn = new JButton("Decline");
        declineBtn.setFont(UIUtils.FONT_BODY);
        declineBtn.setForeground(new Color(180, 40, 40));
        declineBtn.setBackground(Color.WHITE);
        declineBtn.setFocusPainted(false);
        declineBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        declineBtn.addActionListener(e -> {
            String reason = JOptionPane.showInputDialog(this,
                    "Reason for declining request (will be shown to student):",
                    "Schedule conflict with faculty departmental meeting.");
            if (reason != null) {
                consultancyService.declineRequest(req.getId(), reason);
                changed = true;
                refreshTeacherPendingCards();
            }
        });

        actions.add(approveBtn);
        actions.add(declineBtn);

        card.add(info, BorderLayout.CENTER);
        card.add(actions, BorderLayout.EAST);
        return card;
    }

    // -------------------------------------------------------------
    // All Consultancies Overview (for Teacher / Batch Advisor)
    // -------------------------------------------------------------
    private JPanel buildAllConsultanciesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(16, 20, 16, 20));

        String[] cols = {"ID", "Student", "Faculty", "Date", "Time", "Topic", "Status", "Note"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setFont(UIUtils.FONT_BODY);
        table.setRowHeight(28);
        table.getTableHeader().setFont(UIUtils.FONT_BODY_BOLD);
        table.getTableHeader().setBackground(new Color(245, 247, 250));

        List<ConsultancyRequest> all = consultancyService.getRequestsForUser(currentUser);
        for (ConsultancyRequest r : all) {
            model.addRow(new Object[]{
                    "#" + r.getId(),
                    r.getStudentName(),
                    r.getTeacherName(),
                    r.getDate().toString(),
                    DateUtils.formatTime(r.getStartTime()) + " - " + DateUtils.formatTime(r.getEndTime()),
                    r.getTopic(),
                    r.getStatus().name(),
                    r.getResponseNote() != null ? r.getResponseNote() : "-"
            });
        }

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    public boolean isChanged() {
        return changed;
    }
}
