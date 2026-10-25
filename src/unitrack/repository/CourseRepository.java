package unitrack.repository;

import unitrack.model.Course;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Step 15: Academic Course Repository Implementation
 * Manages courses, instructors, and syllabus curriculum records.
 */
public class CourseRepository implements Repository<Course, Integer> {
    private final DataStore dataStore = DataStore.getInstance();

    @Override
    public Optional<Course> findById(Integer id) {
        return Optional.ofNullable(dataStore.getCourses().get(id));
    }

    @Override
    public List<Course> findAll() {
        return new ArrayList<>(dataStore.getCourses().values());
    }

    @Override
    public boolean save(Course entity) {
        if (entity.getId() <= 0) {
            entity.setId(dataStore.getNextCourseId());
        }
        dataStore.getCourses().put(entity.getId(), entity);
        dataStore.saveToFile();
        return true;
    }

    @Override
    public boolean update(Course entity) {
        if (dataStore.getCourses().containsKey(entity.getId())) {
            dataStore.getCourses().put(entity.getId(), entity);
            dataStore.saveToFile();
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteById(Integer id) {
        boolean removed = dataStore.getCourses().remove(id) != null;
        if (removed) dataStore.saveToFile();
        return removed;
    }
}
