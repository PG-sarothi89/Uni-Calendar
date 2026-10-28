package unitrack.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Step 7: Central Calendar Event Entity
 * Models lectures, class tests, assignments, exams, personal tasks, and student-teacher consultancy sessions.
 * Uses modern java.time types (LocalDate, LocalTime).
 */
public class CalendarEvent implements Serializable, Comparable<CalendarEvent> {
    private static final long serialVersionUID = 2L;

    private int id;
    private String title;
    private String description;
    private EventType type;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer courseId;        // Optional: Links to Course model if academic
    private int createdBy;           // User ID of author/faculty
    private Integer attendeeId;      // Optional: User ID of student/attendee (for 1-on-1 consultancy)
    private String location;         // e.g., "Room 402", "Faculty Room 214", or "Online"
    private String attachedFileName; // e.g. "Lecture_04_Polymorphism.pdf"

    public CalendarEvent(int id, String title, String description, EventType type,
                         LocalDate date, LocalTime startTime, LocalTime endTime,
                         Integer courseId, int createdBy, String location, String attachedFileName) {
        this(id, title, description, type, date, startTime, endTime, courseId, createdBy, null, location, attachedFileName);
    }

    public CalendarEvent(int id, String title, String description, EventType type,
                         LocalDate date, LocalTime startTime, LocalTime endTime,
                         Integer courseId, int createdBy, Integer attendeeId, String location, String attachedFileName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.courseId = courseId;
        this.createdBy = createdBy;
        this.attendeeId = attendeeId;
        this.location = location;
        this.attachedFileName = attachedFileName;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getAttendeeId() {
        return attendeeId;
    }

    public void setAttendeeId(Integer attendeeId) {
        this.attendeeId = attendeeId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAttachedFileName() {
        return attachedFileName;
    }

    public void setAttachedFileName(String attachedFileName) {
        this.attachedFileName = attachedFileName;
    }

    /**
     * Natural chronological sorting by date and start time
     */
    @Override
    public int compareTo(CalendarEvent other) {
        int dateCmp = this.date.compareTo(other.date);
        if (dateCmp != 0) return dateCmp;
        if (this.startTime != null && other.startTime != null) {
            return this.startTime.compareTo(other.startTime);
        }
        return Integer.compare(this.id, other.id);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s)", type, title, date);
    }
}
