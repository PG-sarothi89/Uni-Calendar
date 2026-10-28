package unitrack.service;

import unitrack.model.CalendarEvent;
import unitrack.model.EventType;
import unitrack.model.Role;
import unitrack.model.User;
import unitrack.repository.EventRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Step 18: Calendar Business Logic Service
 * Enforces business rules, RBAC authorization, and date-range calculations.
 * Supports privacy-aware event filtering for student-teacher consultancies.
 */
public class CalendarService {
    private static CalendarService instance;
    private final EventRepository eventRepository = new EventRepository();

    private CalendarService() {}

    public static synchronized CalendarService getInstance() {
        if (instance == null) {
            instance = new CalendarService();
        }
        return instance;
    }

    /**
     * Gets all visible events scheduled on a specific date for the currently logged in user
     */
    public List<CalendarEvent> getEventsForDate(LocalDate date) {
        return getEventsForDate(date, AuthService.getInstance().getCurrentUser());
    }

    /**
     * Gets visible events scheduled on a date, filtered by user privacy permissions
     */
    public List<CalendarEvent> getEventsForDate(LocalDate date, User user) {
        List<CalendarEvent> allOnDate = eventRepository.findByDate(date);
        return filterVisibleEvents(allOnDate, user);
    }

    /**
     * Gets all events for an entire month for the current user
     */
    public List<CalendarEvent> getEventsForMonth(int year, int month) {
        return getEventsForMonth(year, month, AuthService.getInstance().getCurrentUser());
    }

    /**
     * Gets all events for an entire month, filtered by user
     */
    public List<CalendarEvent> getEventsForMonth(int year, int month, User user) {
        List<CalendarEvent> allInMonth = eventRepository.findByMonth(year, month);
        return filterVisibleEvents(allInMonth, user);
    }

    /**
     * Filters events according to user visibility:
     * - Public academic events (LECTURE, TEST, ASSIGNMENT, EXAM, HOLIDAY) are visible to all
     * - CONSULTANCY events are visible to the teacher (creator), student (attendee), and Batch Advisor
     * - TASK items are visible to their author
     */
    private List<CalendarEvent> filterVisibleEvents(List<CalendarEvent> events, User user) {
        if (user == null || user.getRole() == Role.ADMIN) {
            return events;
        }

        return events.stream().filter(e -> {
            if (e.getType() == EventType.CONSULTANCY) {
                boolean isTeacher = e.getCreatedBy() == user.getId();
                boolean isStudent = e.getAttendeeId() != null && e.getAttendeeId() == user.getId();
                return isTeacher || isStudent;
            }
            if (e.getType() == EventType.TASK) {
                return e.getCreatedBy() == user.getId();
            }
            return true; // Lectures, tests, exams, holidays are academic public events
        }).collect(Collectors.toList());
    }

    /**
     * Checks if a specific day has any scheduled events visible to the active user
     */
    public boolean hasEventsOnDate(LocalDate date) {
        return !getEventsForDate(date).isEmpty();
    }

    /**
     * Creates a new academic event after checking user role permissions (RBAC)
     *
     * @param event The event entity to persist
     * @param user  The active user attempting to perform the operation
     * @return true if successful; throws SecurityException or IllegalArgumentException on failure
     */
    public boolean createEvent(CalendarEvent event, User user) {
        if (user == null) {
            throw new SecurityException("User session required to create events.");
        }
        if (!user.canCreateEvent() && event.getType() != EventType.TASK) {
            throw new SecurityException("Access Denied: Only Teachers and Administrators can publish academic events.");
        }
        if (event.getTitle() == null || event.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Event title cannot be empty.");
        }
        if (event.getDate() == null) {
            throw new IllegalArgumentException("Event date must be specified.");
        }

        if (event.getCreatedBy() <= 0) {
            event.setCreatedBy(user.getId());
        }
        return eventRepository.save(event);
    }

    /**
     * Updates an existing event
     */
    public boolean updateEvent(CalendarEvent event, User user) {
        if (user == null || !user.canCreateEvent()) {
            throw new SecurityException("Access Denied: Unauthorized to edit events.");
        }
        return eventRepository.update(event);
    }

    /**
     * Deletes an event by ID
     */
    public boolean deleteEvent(int eventId, User user) {
        if (user == null || !user.canCreateEvent()) {
            throw new SecurityException("Access Denied: Unauthorized to delete events.");
        }
        return eventRepository.deleteById(eventId);
    }

    /**
     * Returns event by ID
     */
    public Optional<CalendarEvent> getEventById(int id) {
        return eventRepository.findById(id);
    }

    /**
     * Finds upcoming events within the next N days
     */
    public List<CalendarEvent> getUpcomingEvents(int daysAhead) {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(daysAhead);
        return filterVisibleEvents(eventRepository.findBetween(start, end), AuthService.getInstance().getCurrentUser());
    }
}
