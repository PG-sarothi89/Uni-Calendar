package unitrack.view;

import unitrack.model.Course;
import unitrack.model.Teacher;
import unitrack.model.User;
import unitrack.service.AuthService;
import unitrack.service.BatchAdvisorService;
import unitrack.service.CourseService;
import unitrack.util.UIUtils;
import unitrack.view.components.ModernButton;
import unitrack.view.components.ModernPasswordField;
import unitrack.view.components.ModernTextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

/**
 * Step 38: Batch Advisor Administration Console
 * Exclusively provides the Batch Advisor with controls to:
 *   1. Register new teachers / faculty members in the department.
 *   2. Assign dedicated courses to teachers and auto-schedule weekly classes on the academic calendar.
 */
public class BatchAdvisorDialog extends JDialog {

    private final User currentUser;
    private final BatchAdvisorService batchAdvisorService = BatchAdvisorService.getInstance();
    private final CourseService courseService = CourseService.getInstance();
    private boolean changed = false;

    // Refreshable UI components
    private DefaultTableModel teachersTableModel;
    private DefaultTableModel coursesTableModel;
    private JComboBox<Teacher> teacherDropdown;

    public BatchAdvisorDialog(Frame owner) {
        super(owner, "Batch Advisor — Academic Console", true);
        this.currentUser = AuthService.getInstance().getCurrentUser();

        setSize(820, 560);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIUtils.WINDOW_BG);

        // Header Panel
        add(buildHeaderPanel(), BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIUtils.FONT_BODY_BOLD);
        tabbedPane.setBackground(Color.WHITE);

        tabbedPane.addTab("Register New Teacher", buildAddTeacherPanel());
        tabbedPane.addTab("Assign Dedicated Class", buildAddClassPanel());

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

        JLabel titleLbl = new JLabel("Batch Advisor Administration Console");
        titleLbl.setFont(UIUtils.FONT_TITLE);
        titleLbl.setForeground(UIUtils.LOGIN_TEXT_PRIMARY);

        String subtitleText = currentUser != null
                ? "Advisor: " + currentUser.getName() + " | Department of Computer Science & Engineering"
                : "Manage faculty appointments and curriculum class scheduling.";
        JLabel subtitleLbl = new JLabel(subtitleText);
        subtitleLbl.setFont(UIUtils.FONT_SUBTITLE);
        subtitleLbl.setForeground(UIUtils.LOGIN_TEXT_MUTED);

        header.add(titleLbl, BorderLayout.NORTH);
        header.add(subtitleLbl, BorderLayout.SOUTH);
        return header;
    }

    // -------------------------------------------------------------
    // Tab 1: Register New Teacher
    // -------------------------------------------------------------
    private JPanel buildAddTeacherPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(16, 24, 16, 24));

        // Form at Top
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235), 1, true),
                "Add New Faculty Member",
                0, 0, UIUtils.FONT_BODY_BOLD, new Color(40, 50, 70)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 10, 6, 10);

        // Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.2;
        JLabel nameLbl = new JLabel("Faculty Name:");
        nameLbl.setFont(UIUtils.FONT_BODY_BOLD);
        form.add(nameLbl, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.8;
        ModernTextField nameField = new ModernTextField();
        nameField.setText("Prof. Grace Hopper");
        form.add(nameField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.2;
        JLabel emailLbl = new JLabel("University Email:");
        emailLbl.setFont(UIUtils.FONT_BODY_BOLD);
        form.add(emailLbl, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.8;
        ModernTextField emailField = new ModernTextField();
        emailField.setText("grace.hopper@uni.edu");
        form.add(emailField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.2;
        JLabel passLbl = new JLabel("Initial Password:");
        passLbl.setFont(UIUtils.FONT_BODY_BOLD);
        form.add(passLbl, gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.8;
        ModernPasswordField passField = new ModernPasswordField();
        passField.setText("pass123");
        form.add(passField, gbc);

        // Department
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.2;
        JLabel deptLbl = new JLabel("Department:");
        deptLbl.setFont(UIUtils.FONT_BODY_BOLD);
        form.add(deptLbl, gbc);

        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.8;
        ModernTextField deptField = new ModernTextField();
        deptField.setText("Department of Computer Science & Engineering");
        form.add(deptField, gbc);

        // Submit Button
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 0.8;
        gbc.insets = new Insets(12, 10, 8, 10);
        ModernButton addTeacherBtn = new ModernButton("+ Register Faculty Member", UIUtils.LOGIN_ACCENT, UIUtils.LOGIN_ACCENT_HOVER);
        addTeacherBtn.setPreferredSize(new Dimension(240, 36));
        addTeacherBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String pass = new String(passField.getPassword()).trim();
            String dept = deptField.getText().trim();

            try {
                Teacher t = batchAdvisorService.addNewTeacher(name, email, pass, dept, currentUser);
                changed = true;
                refreshTeachersTable();
                refreshTeacherDropdown();
                JOptionPane.showMessageDialog(this,
                        "Faculty member " + t.getName() + " registered successfully!\nThey can now be assigned dedicated classes and accept student consultancies.",
                        "Faculty Registered", JOptionPane.INFORMATION_MESSAGE);
                nameField.setText("");
                emailField.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Registration Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        form.add(addTeacherBtn, gbc);

        panel.add(form, BorderLayout.NORTH);

        // Table of Current Faculty Members
        String[] cols = {"ID", "Faculty Name", "Email", "Department", "Assigned Courses"};
        teachersTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(teachersTableModel);
        table.setFont(UIUtils.FONT_BODY);
        table.setRowHeight(26);
        table.getTableHeader().setFont(UIUtils.FONT_BODY_BOLD);

        refreshTeachersTable();

        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setOpaque(false);
        JLabel tableTitle = new JLabel("Registered Faculty Members:");
        tableTitle.setFont(UIUtils.FONT_BODY_BOLD);
        tableTitle.setBorder(new EmptyBorder(6, 0, 6, 0));
        tableWrapper.add(tableTitle, BorderLayout.NORTH);
        tableWrapper.add(new JScrollPane(table), BorderLayout.CENTER);

        panel.add(tableWrapper, BorderLayout.CENTER);
        return panel;
    }

    private void refreshTeachersTable() {
        if (teachersTableModel == null) return;
        teachersTableModel.setRowCount(0);
        List<Teacher> teachers = batchAdvisorService.getAllTeachers();
        for (Teacher t : teachers) {
            teachersTableModel.addRow(new Object[]{
                    t.getId(),
                    t.getName(),
                    t.getEmail(),
                    t.getDepartment(),
                    t.getAssignedCourseIds().size() + " courses"
            });
        }
    }

    // -------------------------------------------------------------
    // Tab 2: Assign Dedicated Class
    // -------------------------------------------------------------
    private JPanel buildAddClassPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(16, 24, 16, 24));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235), 1, true),
                "Schedule Dedicated Class & Recurring Lectures",
                0, 0, UIUtils.FONT_BODY_BOLD, new Color(40, 50, 70)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 10, 6, 10);

        // Teacher
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.2;
        JLabel teacherLbl = new JLabel("Select Teacher:");
        teacherLbl.setFont(UIUtils.FONT_BODY_BOLD);
        form.add(teacherLbl, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.8;
        teacherDropdown = new JComboBox<>();
        teacherDropdown.setFont(UIUtils.FONT_BODY);
        teacherDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Teacher) {
                    Teacher t = (Teacher) value;
                    setText(t.getName() + " (" + t.getEmail() + ")");
                }
                return this;
            }
        });
        refreshTeacherDropdown();
        form.add(teacherDropdown, gbc);

        // Course Code
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.2;
        JLabel codeLbl = new JLabel("Course Code:");
        codeLbl.setFont(UIUtils.FONT_BODY_BOLD);
        form.add(codeLbl, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.8;
        ModernTextField codeField = new ModernTextField();
        codeField.setText("CSE 3101");
        form.add(codeField, gbc);

        // Course Title
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.2;
        JLabel titleLbl = new JLabel("Course Title:");
        titleLbl.setFont(UIUtils.FONT_BODY_BOLD);
        form.add(titleLbl, gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.8;
        ModernTextField titleField = new ModernTextField();
        titleField.setText("Database Management Systems");
        form.add(titleField, gbc);

        // Day of Week
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.2;
        JLabel dayLbl = new JLabel("Dedicated Class Day:");
        dayLbl.setFont(UIUtils.FONT_BODY_BOLD);
        form.add(dayLbl, gbc);

        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.8;
        DayOfWeek[] days = {
                DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
        };
        JComboBox<DayOfWeek> dayCombo = new JComboBox<>(days);
        dayCombo.setFont(UIUtils.FONT_BODY);
        form.add(dayCombo, gbc);

        // Time Slot
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.2;
        JLabel timeLbl = new JLabel("Lecture Time Slot:");
        timeLbl.setFont(UIUtils.FONT_BODY_BOLD);
        form.add(timeLbl, gbc);

        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 0.8;
        String[] timeSlots = {
                "08:30 - 10:00",
                "10:00 - 11:30",
                "11:30 - 13:00",
                "14:00 - 15:30",
                "15:30 - 17:00"
        };
        JComboBox<String> timeCombo = new JComboBox<>(timeSlots);
        timeCombo.setFont(UIUtils.FONT_BODY);
        form.add(timeCombo, gbc);

        // Room
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.2;
        JLabel roomLbl = new JLabel("Dedicated Room / Lab:");
        roomLbl.setFont(UIUtils.FONT_BODY_BOLD);
        form.add(roomLbl, gbc);

        gbc.gridx = 1; gbc.gridy = 5; gbc.weightx = 0.8;
        ModernTextField roomField = new ModernTextField();
        roomField.setText("Audi 1");
        form.add(roomField, gbc);

        // Submit Button
        gbc.gridx = 1; gbc.gridy = 6; gbc.weightx = 0.8;
        gbc.insets = new Insets(12, 10, 8, 10);
        ModernButton addClassBtn = new ModernButton("+ Assign Dedicated Class", UIUtils.LOGIN_ACCENT, UIUtils.LOGIN_ACCENT_HOVER);
        addClassBtn.setPreferredSize(new Dimension(240, 36));
        addClassBtn.addActionListener(e -> {
            Teacher selectedTeacher = (Teacher) teacherDropdown.getSelectedItem();
            if (selectedTeacher == null) {
                JOptionPane.showMessageDialog(this, "Please select a teacher.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String code = codeField.getText().trim();
            String title = titleField.getText().trim();
            DayOfWeek day = (DayOfWeek) dayCombo.getSelectedItem();
            String timeSlot = (String) timeCombo.getSelectedItem();
            String room = roomField.getText().trim();

            String[] parts = timeSlot.split(" - ");
            LocalTime start = LocalTime.parse(parts[0].trim());
            LocalTime end = LocalTime.parse(parts[1].trim());

            try {
                int count = batchAdvisorService.addDedicatedClass(
                        selectedTeacher.getId(), code, title, "Fall 2026",
                        day, start, end, room, currentUser
                );

                changed = true;
                refreshCoursesTable();
                refreshTeachersTable();
                JOptionPane.showMessageDialog(this,
                        "Dedicated course " + code + " successfully assigned to " + selectedTeacher.getName() + "!\n" +
                                count + " weekly recurring lecture events added to the calendar.",
                        "Dedicated Class Scheduled", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Assignment Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        form.add(addClassBtn, gbc);

        panel.add(form, BorderLayout.NORTH);

        // Table of Current Courses
        String[] cols = {"Code", "Title", "Assigned Teacher", "Semester"};
        coursesTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(coursesTableModel);
        table.setFont(UIUtils.FONT_BODY);
        table.setRowHeight(26);
        table.getTableHeader().setFont(UIUtils.FONT_BODY_BOLD);

        refreshCoursesTable();

        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setOpaque(false);
        JLabel tableTitle = new JLabel("Curriculum Courses & Assigned Faculty:");
        tableTitle.setFont(UIUtils.FONT_BODY_BOLD);
        tableTitle.setBorder(new EmptyBorder(6, 0, 6, 0));
        tableWrapper.add(tableTitle, BorderLayout.NORTH);
        tableWrapper.add(new JScrollPane(table), BorderLayout.CENTER);

        panel.add(tableWrapper, BorderLayout.CENTER);
        return panel;
    }

    private void refreshTeacherDropdown() {
        if (teacherDropdown == null) return;
        teacherDropdown.removeAllItems();
        List<Teacher> teachers = batchAdvisorService.getAllTeachers();
        for (Teacher t : teachers) {
            teacherDropdown.addItem(t);
        }
    }

    private void refreshCoursesTable() {
        if (coursesTableModel == null) return;
        coursesTableModel.setRowCount(0);
        List<Course> courses = courseService.getAllCourses();
        for (Course c : courses) {
            coursesTableModel.addRow(new Object[]{
                    c.getCode(),
                    c.getTitle(),
                    c.getInstructorName(),
                    c.getSemester()
            });
        }
    }

    public boolean isChanged() {
        return changed;
    }
}
