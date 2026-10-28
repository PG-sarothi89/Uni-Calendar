package unitrack.model;

import java.awt.Color;

/**
 * Step 2: Event Type Enumeration
 * Classifies academic, personal, and consultancy events on the calendar.
 * Encapsulates presentation styling (color codes) matching the UI reference palette.
 */
public enum EventType {
    LECTURE("Lecture", new Color(0, 188, 212)),          // Cyan / Teal
    TEST("Class Test", new Color(233, 30, 99)),          // Pink / Coral
    ASSIGNMENT("Assignment", new Color(156, 39, 176)),   // Purple / Magenta
    EXAM("Final Exam", new Color(244, 67, 54)),           // Red / Crimson
    HOLIDAY("Holiday", new Color(76, 175, 80)),           // Green
    CONSULTANCY("Consultancy", new Color(255, 152, 0)),   // Amber / Orange for student-teacher consultancy
    TASK("Personal Task", new Color(33, 150, 243));       // Blue

    private final String displayName;
    private final Color color;

    EventType(String displayName, Color color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
