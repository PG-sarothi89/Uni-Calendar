package unitrack.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Step 9: Consultancy Request Model
 * Encapsulates the complete lifecycle of a student requesting faculty office hours/consultancy:
 *   1. Student submits request with proposed date, time, and academic topic.
 *   2. Faculty reviews request with approval or decline + guidance notes.
 *   3. Upon approval, automatically binds to a CalendarEvent added to both teacher and student calendars.
 */
public class ConsultancyRequest implements Serializable, Comparable<ConsultancyRequest> {
    private static final long serialVersionUID = 1L;

    public enum Status {
        PENDING("Pending", "#FF9800"),     // Amber
        APPROVED("Approved", "#4CAF50"),   // Emerald Green
        DECLINED("Declined", "#F44336");   // Crimson

        private final String label;
        private final String hexColor;

        Status(String label, String hexColor) {
            this.label = label;
            this.hexColor = hexColor;
        }

        public String getLabel() {
            return label;
        }

        public String getHexColor() {
            return hexColor;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private int id;
    private int studentId;
    private String studentName;
    private int teacherId;
    private String teacherName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String topic;
    private Status status = Status.PENDING;
    private String responseNote;
    private Integer calendarEventId;
    private LocalDateTime createdAt = LocalDateTime.now();

    public ConsultancyRequest(int id, int studentId, String studentName,
                              int teacherId, String teacherName,
                              LocalDate date, LocalTime startTime, LocalTime endTime,
                              String topic) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.topic = topic;
        this.status = Status.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
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

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getResponseNote() {
        return responseNote;
    }

    public void setResponseNote(String responseNote) {
        this.responseNote = responseNote;
    }

    public Integer getCalendarEventId() {
        return calendarEventId;
    }

    public void setCalendarEventId(Integer calendarEventId) {
        this.calendarEventId = calendarEventId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public int compareTo(ConsultancyRequest other) {
        // Pending first, then chronologically by appointment date and start time
        if (this.status != other.status) {
            if (this.status == Status.PENDING) return -1;
            if (other.status == Status.PENDING) return 1;
        }
        int dateCmp = this.date.compareTo(other.date);
        if (dateCmp != 0) return dateCmp;
        return this.startTime.compareTo(other.startTime);
    }

    @Override
    public String toString() {
        return String.format("[%s] Student %s with Teacher %s on %s %s (%s)",
                status, studentName, teacherName, date, startTime, topic);
    }
}
