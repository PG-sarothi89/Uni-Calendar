package unitrack.view;

import unitrack.model.CalendarEvent;
import unitrack.model.Course;
import unitrack.model.EventType;
import unitrack.model.User;
import unitrack.service.AuthService;
import unitrack.service.CalendarService;
import unitrack.service.CourseService;
import unitrack.util.DateUtils;
import unitrack.util.UIUtils;
import unitrack.view.components.ModernButton;
import unitrack.view.components.ModernTextField;
import unitrack.view.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Step 31: Add / Edit Academic Event Modal Dialog
 * Allows teachers and administrators to schedule lectures, exams, assignments,
 * and attach presentation slides.
 */
public class EventDialog extends JDialog {
    private final CalendarService calendarService = CalendarService.getInstance();
    private final CourseService courseService = CourseService.getInstance();
    private final User currentUser = AuthService.getInstance().getCurrentUser();

    private ModernTextField titleField;
    private JTextArea descArea;
    private JComboBox<EventType> typeCombo;
    private JComboBox<CourseItem> courseCombo;
    private ModernTextField dateField;
    private ModernTextField startTimeField;
    private ModernTextField endTimeField;
    private ModernTextField locationField;
    private ModernTextField fileField;

    private CalendarEvent existingEvent;
    private boolean saved = false;

    // Helper item for course combobox
    private static class CourseItem {
        final Integer id;
        final String label;

        CourseItem(Integer id, String label) {
            this.id = id;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public EventDialog(Frame owner, LocalDate defaultDate, CalendarEvent eventToEdit) {
        super(owner, eventToEdit == null ? "Add Academic Event" : "Edit Event", true);
        this.existingEvent = eventToEdit;

        setSize(480, 620);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 247, 250));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        contentPanel.setOpaque(false);

        JLabel header = new JLabel(eventToEdit == null ? "Schedule Academic Event" : "Event Details");
        header.setFont(UIUtils.FONT_HEADER);
        header.setForeground(UIUtils.CAL_DARK_BG);

        // Fields initialization
        titleField = new ModernTextField("e.g. CSE 2102: Lecture on Polymorphism");
        descArea = new JTextArea(3, 20);
        descArea.setFont(UIUtils.FONT_BODY);
        descArea.setBorder(BorderFactory.createLineBorder(UIUtils.LOGIN_INPUT_BORDER));

        typeCombo = new JComboBox<>(EventType.values());
        typeCombo.setFont(UIUtils.FONT_BODY);

        courseCombo = new JComboBox<>();
        courseCombo.addItem(new CourseItem(null, "— No Course (Institutional / Personal) —"));
        for (Course c : courseService.getAllCourses()) {
            courseCombo.addItem(new CourseItem(c.getId(), c.getCode() + ": " + c.getTitle()));
        }

        LocalDate d = (eventToEdit != null) ? eventToEdit.getDate() : (defaultDate != null ? defaultDate : LocalDate.now());
        dateField = new ModernTextField("YYYY-MM-DD");
        dateField.setText(d.toString());

        startTimeField = new ModernTextField("HH:mm");
        startTimeField.setText(eventToEdit != null && eventToEdit.getStartTime() != null ?
                DateUtils.formatTime(eventToEdit.getStartTime()) : "09:00");

        endTimeField = new ModernTextField("HH:mm");
        endTimeField.setText(eventToEdit != null && eventToEdit.getEndTime() != null ?
                DateUtils.formatTime(eventToEdit.getEndTime()) : "10:30");

        locationField = new ModernTextField("e.g. Room 402 or Online");
        locationField.setText(eventToEdit != null && eventToEdit.getLocation() != null ? eventToEdit.getLocation() : "Room 402");

        fileField = new ModernTextField("Attached file name (e.g. Lecture_Slides.pdf)");
        if (eventToEdit != null && eventToEdit.getAttachedFileName() != null) {
            fileField.setText(eventToEdit.getAttachedFileName());
        }

        if (eventToEdit != null) {
            titleField.setText(eventToEdit.getTitle());
            descArea.setText(eventToEdit.getDescription() != null ? eventToEdit.getDescription() : "");
            typeCombo.setSelectedItem(eventToEdit.getType());
            if (eventToEdit.getCourseId() != null) {
                for (int i = 0; i < courseCombo.getItemCount(); i++) {
                    if (eventToEdit.getCourseId().equals(courseCombo.getItemAt(i).id)) {
                        courseCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }

        // Layout rows
        contentPanel.add(header);
        contentPanel.add(Box.createVerticalStrut(15));

        contentPanel.add(createFieldRow("Title / Subject:", titleField));
        contentPanel.add(Box.createVerticalStrut(8));

        contentPanel.add(createFieldRow("Event Classification:", typeCombo));
        contentPanel.add(Box.createVerticalStrut(8));

        contentPanel.add(createFieldRow("Course Binding:", courseCombo));
        contentPanel.add(Box.createVerticalStrut(8));

        JPanel dateTimeRow = new JPanel(new GridLayout(1, 3, 8, 0));
        dateTimeRow.setOpaque(false);
        dateTimeRow.add(createFieldRow("Date:", dateField));
        dateTimeRow.add(createFieldRow("Start Time:", startTimeField));
        dateTimeRow.add(createFieldRow("End Time:", endTimeField));
        contentPanel.add(dateTimeRow);
        contentPanel.add(Box.createVerticalStrut(8));

        contentPanel.add(createFieldRow("Location / Venue:", locationField));
        contentPanel.add(Box.createVerticalStrut(8));

        contentPanel.add(createFieldRow("Lecture Material (.pdf):", fileField));
        contentPanel.add(Box.createVerticalStrut(8));

        JLabel descLbl = new JLabel("Description / Notes:");
        descLbl.setFont(UIUtils.FONT_SMALL_BOLD);
        contentPanel.add(descLbl);
        contentPanel.add(Box.createVerticalStrut(4));
        contentPanel.add(new JScrollPane(descArea));
        contentPanel.add(Box.createVerticalStrut(15));

        // Buttons
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonRow.setOpaque(false);

        if (eventToEdit != null && currentUser.canCreateEvent()) {
            ModernButton deleteBtn = new ModernButton("Delete", new Color(220, 53, 69), new Color(200, 35, 51));
            deleteBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this event?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    calendarService.deleteEvent(eventToEdit.getId(), currentUser);
                    saved = true;
                    dispose();
                }
            });
            buttonRow.add(deleteBtn);
        }

        ModernButton cancelBtn = new ModernButton("Cancel", new Color(140, 145, 155), new Color(120, 125, 135));
        cancelBtn.addActionListener(e -> dispose());
        buttonRow.add(cancelBtn);

        ModernButton saveBtn = new ModernButton(eventToEdit == null ? "Save Event" : "Update Event", UIUtils.LOGIN_ACCENT, UIUtils.LOGIN_ACCENT_HOVER);
        saveBtn.addActionListener(e -> onSave());
        buttonRow.add(saveBtn);

        contentPanel.add(buttonRow);
        add(new JScrollPane(contentPanel), BorderLayout.CENTER);
    }

    private JPanel createFieldRow(String labelText, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UIUtils.FONT_SMALL_BOLD);
        lbl.setForeground(new Color(60, 65, 75));
        p.add(lbl, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private void onSave() {
        try {
            String title = titleField.getText().trim();
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Event title cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDate date = LocalDate.parse(dateField.getText().trim());
            LocalTime start = LocalTime.parse(startTimeField.getText().trim());
            LocalTime end = LocalTime.parse(endTimeField.getText().trim());

            EventType type = (EventType) typeCombo.getSelectedItem();
            CourseItem ci = (CourseItem) courseCombo.getSelectedItem();
            Integer courseId = ci != null ? ci.id : null;
            String loc = locationField.getText().trim();
            String file = fileField.getText().trim();
            String desc = descArea.getText().trim();

            if (existingEvent == null) {
                CalendarEvent event = new CalendarEvent(0, title, desc, type, date, start, end,
                        courseId, currentUser.getId(), loc, file.isEmpty() ? null : file);
                calendarService.createEvent(event, currentUser);
            } else {
                existingEvent.setTitle(title);
                existingEvent.setDescription(desc);
                existingEvent.setType(type);
                existingEvent.setDate(date);
                existingEvent.setStartTime(start);
                existingEvent.setEndTime(end);
                existingEvent.setCourseId(courseId);
                existingEvent.setLocation(loc);
                existingEvent.setAttachedFileName(file.isEmpty() ? null : file);
                calendarService.updateEvent(existingEvent, currentUser);
            }

            saved = true;
            dispose();
        } catch (SecurityException se) {
            JOptionPane.showMessageDialog(this, se.getMessage(), "Permission Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid date/time format. Please check inputs (YYYY-MM-DD and HH:mm).", "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
