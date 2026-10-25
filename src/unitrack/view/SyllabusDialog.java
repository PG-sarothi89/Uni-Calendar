package unitrack.view;

import unitrack.model.Course;
import unitrack.model.SyllabusTopic;
import unitrack.model.User;
import unitrack.service.AuthService;
import unitrack.service.CourseService;
import unitrack.util.UIUtils;
import unitrack.view.components.ModernButton;
import unitrack.view.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Step 32: Course Syllabus Progress Tracker Dialog
 * Features an interactive topic checklist and dynamic JProgressBar
 * calculating the completed percentage in real time.
 */
public class SyllabusDialog extends JDialog {
    private final CourseService courseService = CourseService.getInstance();
    private final User currentUser = AuthService.getInstance().getCurrentUser();
    private JComboBox<Course> courseSelector;
    private JProgressBar progressBar;
    private JLabel percentLabel;
    private JPanel topicsContainer;

    public SyllabusDialog(Frame owner) {
        super(owner, "Course Syllabus Progress Tracker", true);
        setSize(560, 620);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 247, 250));

        // Header
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(new EmptyBorder(20, 25, 15, 25));
        topPanel.setOpaque(false);

        JLabel title = new JLabel("Curriculum & Syllabus Tracker");
        title.setFont(UIUtils.FONT_HEADER);
        title.setForeground(UIUtils.CAL_DARK_BG);

        JLabel subtitle = new JLabel(currentUser.canUpdateSyllabus() ?
                "Select a course to view and check off completed lecture topics." :
                "View course topic completion and semester milestones.");
        subtitle.setFont(UIUtils.FONT_SMALL);
        subtitle.setForeground(UIUtils.LOGIN_TEXT_MUTED);

        // Course Selector ComboBox
        List<Course> courses = courseService.getAllCourses();
        courseSelector = new JComboBox<>(courses.toArray(new Course[0]));
        courseSelector.setFont(UIUtils.FONT_BODY);
        courseSelector.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        courseSelector.addActionListener(e -> reloadTopics());

        // Progress Bar Panel
        RoundedPanel progressCard = new RoundedPanel(16, Color.WHITE, new Color(225, 228, 235), 1.0f);
        progressCard.setLayout(new BorderLayout(10, 8));
        progressCard.setBorder(new EmptyBorder(12, 16, 12, 16));
        progressCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel progTitle = new JLabel("Semester Progress:");
        progTitle.setFont(UIUtils.FONT_SMALL_BOLD);
        progTitle.setForeground(new Color(50, 55, 65));

        percentLabel = new JLabel("0%");
        percentLabel.setFont(UIUtils.FONT_BODY_BOLD);
        percentLabel.setForeground(new Color(46, 125, 50));

        JPanel progHeader = new JPanel(new BorderLayout());
        progHeader.setOpaque(false);
        progHeader.add(progTitle, BorderLayout.WEST);
        progHeader.add(percentLabel, BorderLayout.EAST);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(false);
        progressBar.setForeground(new Color(76, 175, 80)); // Fresh green
        progressBar.setBackground(new Color(235, 238, 245));
        progressBar.setPreferredSize(new Dimension(100, 14));

        progressCard.add(progHeader, BorderLayout.NORTH);
        progressCard.add(progressBar, BorderLayout.CENTER);

        topPanel.add(title);
        topPanel.add(Box.createVerticalStrut(4));
        topPanel.add(subtitle);
        topPanel.add(Box.createVerticalStrut(12));
        topPanel.add(courseSelector);
        topPanel.add(Box.createVerticalStrut(12));
        topPanel.add(progressCard);

        add(topPanel, BorderLayout.NORTH);

        // Topics Checklist Area
        topicsContainer = new JPanel();
        topicsContainer.setLayout(new BoxLayout(topicsContainer, BoxLayout.Y_AXIS));
        topicsContainer.setOpaque(false);
        topicsContainer.setBorder(new EmptyBorder(10, 25, 10, 25));

        JScrollPane scrollPane = new JScrollPane(topicsContainer);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(245, 247, 250));
        add(scrollPane, BorderLayout.CENTER);

        // Close Button
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 25, 12));
        bottomBar.setOpaque(false);
        ModernButton closeBtn = new ModernButton("Close", new Color(140, 145, 155), new Color(120, 125, 135));
        closeBtn.addActionListener(e -> dispose());
        bottomBar.add(closeBtn);
        add(bottomBar, BorderLayout.SOUTH);

        // Initial Load
        reloadTopics();
    }

    private void reloadTopics() {
        topicsContainer.removeAll();
        Course selected = (Course) courseSelector.getSelectedItem();
        if (selected == null) {
            topicsContainer.revalidate();
            topicsContainer.repaint();
            return;
        }

        // Update Progress Bar
        int percentage = selected.getSyllabusCompletionPercentage();
        progressBar.setValue(percentage);
        percentLabel.setText(percentage + "% Completed");

        // Populate Topics
        for (SyllabusTopic topic : selected.getSyllabus()) {
            JPanel row = new RoundedPanel(12, Color.WHITE, new Color(230, 233, 240), 1.0f);
            row.setLayout(new BorderLayout(12, 0));
            row.setBorder(new EmptyBorder(8, 14, 8, 14));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

            JLabel weekLbl = new JLabel(String.format("Week %02d", topic.getWeekNumber()));
            weekLbl.setFont(UIUtils.FONT_SMALL_BOLD);
            weekLbl.setForeground(UIUtils.CAL_HIGHLIGHT_PINK);
            weekLbl.setPreferredSize(new Dimension(65, 20));

            JLabel topicLbl = new JLabel(topic.getTitle());
            topicLbl.setFont(UIUtils.FONT_BODY);
            topicLbl.setForeground(topic.isCompleted() ? new Color(130, 135, 145) : UIUtils.CAL_DARK_BG);

            JCheckBox check = new JCheckBox();
            check.setSelected(topic.isCompleted());
            check.setOpaque(false);
            check.setEnabled(currentUser.canUpdateSyllabus()); // Only teachers/admins can modify

            check.addActionListener(e -> {
                boolean checked = check.isSelected();
                courseService.updateTopicStatus(selected.getId(), topic.getId(), checked, currentUser);
                reloadTopics();
            });

            JPanel textWrap = new JPanel(new BorderLayout(8, 0));
            textWrap.setOpaque(false);
            textWrap.add(weekLbl, BorderLayout.WEST);
            textWrap.add(topicLbl, BorderLayout.CENTER);

            row.add(textWrap, BorderLayout.CENTER);
            row.add(check, BorderLayout.EAST);

            topicsContainer.add(row);
            topicsContainer.add(Box.createVerticalStrut(8));
        }

        topicsContainer.revalidate();
        topicsContainer.repaint();
    }
}
