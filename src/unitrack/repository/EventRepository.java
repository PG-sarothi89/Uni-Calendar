package unitrack.repository;

import unitrack.model.CalendarEvent;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Step 14: Calendar Event Repository Implementation
 * Encapsulates event persistence, date filtering, and range lookups.
 */
public class EventRepository implements Repository<CalendarEvent, Integer> {
    private final DataStore dataStore = DataStore.getInstance();

    @Override
    public Optional<CalendarEvent> findById(Integer id) {
        return Optional.ofNullable(dataStore.getEvents().get(id));
    }

    @Override
    public List<CalendarEvent> findAll() {
        return dataStore.getEvents().values().stream()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Filters events by a specific date
     */
    public List<CalendarEvent> findByDate(LocalDate date) {
        return dataStore.getEvents().values().stream()
                .filter(e -> e.getDate().equals(date))
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Filters events within a given month and year
     */
    public List<CalendarEvent> findByMonth(int year, int month) {
        return dataStore.getEvents().values().stream()
                .filter(e -> e.getDate().getYear() == year && e.getDate().getMonthValue() == month)
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Filters events scheduled within a date range [start, end]
     */
    public List<CalendarEvent> findBetween(LocalDate start, LocalDate end) {
        return dataStore.getEvents().values().stream()
                .filter(e -> !e.getDate().isBefore(start) && !e.getDate().isAfter(end))
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public boolean save(CalendarEvent entity) {
        if (entity.getId() <= 0) {
            entity.setId(dataStore.getNextEventId());
        }
        dataStore.getEvents().put(entity.getId(), entity);
        dataStore.saveToFile();
        return true;
    }

    @Override
    public boolean update(CalendarEvent entity) {
        if (dataStore.getEvents().containsKey(entity.getId())) {
            dataStore.getEvents().put(entity.getId(), entity);
            dataStore.saveToFile();
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteById(Integer id) {
        boolean removed = dataStore.getEvents().remove(id) != null;
        if (removed) dataStore.saveToFile();
        return removed;
    }
}
