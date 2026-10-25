package unitrack.view;

import unitrack.model.TodoItem;
import unitrack.model.User;
import unitrack.service.AuthService;
import unitrack.service.TodoService;
import unitrack.util.UIUtils;
import unitrack.view.components.ModernButton;
import unitrack.view.components.ModernTextField;
import unitrack.view.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Step 33: Student Personal To-Do Task Dialog
 * Provides students with a personal task checklist independent of public course calendars.
 */
public class TodoDialog extends JDialog {
    private final TodoService todoService = TodoService.getInstance();
    private final User currentUser = AuthService.getInstance().getCurrentUser();
    private final JPanel taskListContainer;
    private final ModernTextField newTaskField;
    private final JComboBox<TodoItem.Priority> priorityCombo;

    public TodoDialog(Frame owner) {
        super(owner, "Personal Study Tasks & To-Do List", true);
        setSize(580, 620);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 247, 250));

        // Header Panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(new EmptyBorder(20, 25, 12, 25));
        topPanel.setOpaque(false);

        JLabel title = new JLabel("Personal Study Checklist");
        title.setFont(UIUtils.FONT_HEADER);
        title.setForeground(UIUtils.CAL_DARK_BG);

        JLabel subtitle = new JLabel("Keep track of your individual study goals, readings, and assignment milestones.");
        subtitle.setFont(UIUtils.FONT_SMALL);
        subtitle.setForeground(UIUtils.LOGIN_TEXT_MUTED);

        // Add New Task Input Bar
        JPanel addBar = new RoundedPanel(16, Color.WHITE, new Color(225, 228, 235), 1.0f);
        addBar.setLayout(new BorderLayout(8, 0));
        addBar.setBorder(new EmptyBorder(6, 12, 6, 8));
        addBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        newTaskField = new ModernTextField("What do you need to study or complete?");
        priorityCombo = new JComboBox<>(TodoItem.Priority.values());
        priorityCombo.setSelectedItem(TodoItem.Priority.HIGH);
        priorityCombo.setFont(UIUtils.FONT_SMALL);

        ModernButton addBtn = new ModernButton("+ Add", UIUtils.LOGIN_ACCENT, UIUtils.LOGIN_ACCENT_HOVER);
        addBtn.setPreferredSize(new Dimension(80, 34));
        addBtn.addActionListener(e -> onAddTask());

        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        rightControls.setOpaque(false);
        rightControls.add(priorityCombo);
        rightControls.add(addBtn);

        addBar.add(newTaskField, BorderLayout.CENTER);
        addBar.add(rightControls, BorderLayout.EAST);

        topPanel.add(title);
        topPanel.add(Box.createVerticalStrut(4));
        topPanel.add(subtitle);
        topPanel.add(Box.createVerticalStrut(14));
        topPanel.add(addBar);

        add(topPanel, BorderLayout.NORTH);

        // Task List Container
        taskListContainer = new JPanel();
        taskListContainer.setLayout(new BoxLayout(taskListContainer, BoxLayout.Y_AXIS));
        taskListContainer.setOpaque(false);
        taskListContainer.setBorder(new EmptyBorder(10, 25, 10, 25));

        JScrollPane scroll = new JScrollPane(taskListContainer);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(245, 247, 250));
        add(scroll, BorderLayout.CENTER);

        // Bottom Close
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 25, 12));
        bottomBar.setOpaque(false);
        ModernButton closeBtn = new ModernButton("Close", new Color(140, 145, 155), new Color(120, 125, 135));
        closeBtn.addActionListener(e -> dispose());
        bottomBar.add(closeBtn);
        add(bottomBar, BorderLayout.SOUTH);

        reloadTasks();
    }

    private void onAddTask() {
        String text = newTaskField.getText().trim();
        if (text.isEmpty()) return;

        TodoItem.Priority p = (TodoItem.Priority) priorityCombo.getSelectedItem();
        todoService.addTodo(currentUser.getId(), text, p, LocalDate.now().plusDays(2));
        newTaskField.setText("");
        reloadTasks();
    }

    private void reloadTasks() {
        taskListContainer.removeAll();
        List<TodoItem> tasks = todoService.getTasksForUser(currentUser.getId());

        if (tasks.isEmpty()) {
            JLabel empty = new JLabel("No tasks yet. Add your study goals above!");
            empty.setFont(UIUtils.FONT_BODY);
            empty.setForeground(UIUtils.LOGIN_TEXT_MUTED);
            taskListContainer.add(empty);
        } else {
            for (TodoItem item : tasks) {
                JPanel card = new RoundedPanel(12, Color.WHITE, new Color(230, 233, 240), 1.0f);
                card.setLayout(new BorderLayout(10, 0));
                card.setBorder(new EmptyBorder(8, 12, 8, 12));
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

                JCheckBox check = new JCheckBox();
                check.setSelected(item.isCompleted());
                check.setOpaque(false);
                check.addActionListener(e -> {
                    todoService.toggleComplete(item.getId());
                    reloadTasks();
                });

                JLabel taskText = new JLabel(item.getTask());
                taskText.setFont(UIUtils.FONT_BODY);
                if (item.isCompleted()) {
                    taskText.setForeground(new Color(150, 155, 165));
                } else {
                    taskText.setForeground(UIUtils.CAL_DARK_BG);
                }

                // Priority Badge
                Color pColor;
                switch (item.getPriority()) {
                    case HIGH:   pColor = new Color(233, 30, 99); break;
                    case MEDIUM: pColor = new Color(255, 152, 0); break;
                    default:     pColor = new Color(76, 175, 80); break;
                }

                JLabel pBadge = new JLabel(" " + item.getPriority() + " ");
                pBadge.setFont(UIUtils.FONT_SMALL_BOLD);
                pBadge.setForeground(pColor);

                JLabel dateLbl = new JLabel("Due: " + item.getDueDate());
                dateLbl.setFont(UIUtils.FONT_SMALL);
                dateLbl.setForeground(UIUtils.LOGIN_TEXT_MUTED);

                JButton delBtn = new JButton("×");
                delBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
                delBtn.setForeground(new Color(200, 60, 60));
                delBtn.setContentAreaFilled(false);
                delBtn.setBorderPainted(false);
                delBtn.setFocusPainted(false);
                delBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                delBtn.addActionListener(e -> {
                    todoService.deleteTodo(item.getId());
                    reloadTasks();
                });

                JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
                right.setOpaque(false);
                right.add(pBadge);
                right.add(dateLbl);
                right.add(delBtn);

                JPanel left = new JPanel(new BorderLayout(8, 0));
                left.setOpaque(false);
                left.add(check, BorderLayout.WEST);
                left.add(taskText, BorderLayout.CENTER);

                card.add(left, BorderLayout.CENTER);
                card.add(right, BorderLayout.EAST);

                taskListContainer.add(card);
                taskListContainer.add(Box.createVerticalStrut(8));
            }
        }

        taskListContainer.revalidate();
        taskListContainer.repaint();
    }
}
