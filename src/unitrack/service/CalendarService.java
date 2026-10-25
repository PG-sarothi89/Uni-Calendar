package unitrack.service;

import unitrack.model.CalendarEvent;
import unitrack.model.User;
import unitrack.repository.EventRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Step 18: Calendar Business Logic Service
 * Enforces business rules, RBAC authorization, and date-range calculations.
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
     * Gets all events scheduled on a specific date
     */
    public List<CalendarEvent> getEventsForDate(LocalDate date) {
        return eventRepository.findByDate(date);
    }

    /**
     * Gets all events for an entire month
     */
    public List<CalendarEvent> getEventsForMonth(int year, int month) {
        return eventRepository.findByMonth(year, month);
    }

    /**
     * Checks if a specific day has any scheduled events
     */
    public boolean hasEventsOnDate(LocalDate date) {
        return !eventRepository.findByDate(date).isEmpty();
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
        if (!user.canCreateEvent()) {
            throw new SecurityException("Access Denied: Only Teachers and Administrators can publish academic events.");
        }
        if (event.getTitle() == null || event.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Event title cannot be empty.");
        }
        if (event.getDate() == null) {
            throw new IllegalArgumentException("Event date must be specified.");
        }

        event.setCreatedBy(user.getId());
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
        return eventRepository.findBetween(start, end);
    }
}
