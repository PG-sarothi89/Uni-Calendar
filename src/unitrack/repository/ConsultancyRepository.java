package unitrack.repository;

import unitrack.model.ConsultancyRequest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Step 15: Consultancy Request Repository
 * Provides thread-safe CRUD and query filters for student consultancy bookings.
 */
public class ConsultancyRepository implements Repository<ConsultancyRequest, Integer> {
    private final DataStore dataStore = DataStore.getInstance();

    @Override
    public Optional<ConsultancyRequest> findById(Integer id) {
        return Optional.ofNullable(dataStore.getConsultancyRequests().get(id));
    }

    @Override
    public List<ConsultancyRequest> findAll() {
        return dataStore.getConsultancyRequests().values().stream()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<ConsultancyRequest> findByStudentId(int studentId) {
        return dataStore.getConsultancyRequests().values().stream()
                .filter(r -> r.getStudentId() == studentId)
                .sorted()
                .collect(Collectors.toList());
    }

    public List<ConsultancyRequest> findByTeacherId(int teacherId) {
        return dataStore.getConsultancyRequests().values().stream()
                .filter(r -> r.getTeacherId() == teacherId)
                .sorted()
                .collect(Collectors.toList());
    }

    public List<ConsultancyRequest> findPendingByTeacherId(int teacherId) {
        return dataStore.getConsultancyRequests().values().stream()
                .filter(r -> r.getTeacherId() == teacherId && r.getStatus() == ConsultancyRequest.Status.PENDING)
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public boolean save(ConsultancyRequest entity) {
        if (entity.getId() <= 0) {
            entity.setId(dataStore.getNextConsultancyId());
        }
        dataStore.getConsultancyRequests().put(entity.getId(), entity);
        dataStore.saveToFile();
        return true;
    }

    @Override
    public boolean update(ConsultancyRequest entity) {
        if (dataStore.getConsultancyRequests().containsKey(entity.getId())) {
            dataStore.getConsultancyRequests().put(entity.getId(), entity);
            dataStore.saveToFile();
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteById(Integer id) {
        boolean removed = dataStore.getConsultancyRequests().remove(id) != null;
        if (removed) {
            dataStore.saveToFile();
        }
        return removed;
    }
}
