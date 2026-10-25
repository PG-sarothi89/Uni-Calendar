package unitrack.service;

import unitrack.model.Course;
import unitrack.model.SyllabusTopic;
import unitrack.model.User;
import unitrack.repository.CourseRepository;
import unitrack.repository.DataStore;

import java.util.List;
import java.util.Optional;

/**
 * Step 19: Course & Syllabus Progress Service
 * Provides course information and calculates syllabus completion percentages.
 * Enforces role verification so only authorized instructors/admins can check off topics.
 */
public class CourseService {
    private static CourseService instance;
    private final CourseRepository courseRepository = new CourseRepository();

    private CourseService() {}

    public static synchronized CourseService getInstance() {
        if (instance == null) {
            instance = new CourseService();
        }
        return instance;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(int id) {
        return courseRepository.findById(id);
    }

    /**
     * Toggles a syllabus topic's completion status
     *
     * @param courseId  Target course
     * @param topicId   Target syllabus topic
     * @param completed New completed boolean flag
     * @param user      Active user performing action
     */
    public boolean updateTopicStatus(int courseId, int topicId, boolean completed, User user) {
        if (user == null || !user.canUpdateSyllabus()) {
            throw new SecurityException("Access Denied: Only Teachers and Administrators can modify syllabus topic status.");
        }

        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isPresent()) {
            Course course = courseOpt.get();
            for (SyllabusTopic topic : course.getSyllabus()) {
                if (topic.getId() == topicId) {
                    topic.setCompleted(completed);
                    DataStore.getInstance().saveToFile();
                    return true;
                }
            }
        }
        return false;
    }
}
